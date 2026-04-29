(function(){
  // In clean-our-sea-local the Spring Boot backend runs on :8080 while the
  // static frontend is served on :8000, so we can't use bare /api/ paths any
  // more — they'd hit the Python static server. Prefix every call with the
  // backend origin.
  const BACKEND = "https://clean-our-sea-backend.onrender.com";

  // Communities page logic moved from inline script
  const sample = [
    {id:1,name:'Brighton Coastal Volunteers',desc:'Local volunteers co-ordinating beach cleanups and trash pickups across Brighton shoreline.',members:124,tags:['cleanup']},
    {id:2,name:'Marine Research Network',desc:'Researchers sharing data on microplastics, species impact, and survey results.',members:86,tags:['research']},
    {id:3,name:'Policy & Advocacy',desc:'Groups co-ordinating advocacy campaigns and local policy pushes to reduce single-use plastics.',members:42,tags:['policy']},
  ];

  async function fetchBase(){
    try{
      const res = await fetch(BACKEND + '/api/communities');
      if(!res.ok) throw new Error('Network');
      return await res.json();
    }catch(e){
      try{
        const res2 = await fetch('data/2419616/communities.json');
        if(res2.ok) return await res2.json();
      }catch(e2){}
      return sample;
    }
  }

  function getAdded(){
    return JSON.parse(localStorage.getItem('communities_added')||'[]');
  }
  function setAdded(arr){ localStorage.setItem('communities_added', JSON.stringify(arr)); }

  function getDeletedIds(){
    return JSON.parse(localStorage.getItem('communities_deleted')||'[]');
  }
  function setDeletedIds(arr){ localStorage.setItem('communities_deleted', JSON.stringify(arr)); }

  (async function init(){
    // Note: automatic demo-clearing on page load removed so created communities persist across navigation.
    // Provide a manual Reset Demo button to clear demo data when preparing to go live.
    const resetBtn = document.getElementById('resetDemo');
    function clearDemoData(){
      try{
        localStorage.removeItem('communities_added');
        localStorage.removeItem('communities_deleted');
        // remove chat keys
        for(let i = localStorage.length - 1; i >= 0; i--){
          const k = localStorage.key(i);
          if(k && k.startsWith('chat_')) localStorage.removeItem(k);
        }
      }catch(e){ /* ignore */ }
    }
    if(resetBtn){
      resetBtn.addEventListener('click', ()=>{
        const ok = confirm('Reset demo data? This will remove created communities, deletions and chat logs.');
        if(!ok) return;
        clearDemoData();
        location.reload();
      });
    }

    const base = await fetchBase();
    let communities = [...getAdded(), ...base];

    const list = document.getElementById('list');
    const tmpl = document.getElementById('cardTmpl');
    const search = document.getElementById('search');
    const sortSelect = document.getElementById('sort');
    const categorySelect = document.getElementById('category');
    const createBtn = document.getElementById('createBtn');
    const modal = document.getElementById('modal');
    const createForm = document.getElementById('createForm');

    // ensure modal hidden — CSS uses both the `hidden` attribute and the
    // `.active` class to drive visibility, so toggle them together.
    if(modal){ modal.hidden = true; modal.classList.remove('active'); modal.setAttribute('aria-hidden','true'); }

    function render(items){
      const deleted = getDeletedIds();
      list.innerHTML='';
      items.filter(c=>!deleted.includes(c.id)).forEach(c=>{
        const node = tmpl.content.cloneNode(true);
        node.querySelector('.title').textContent = c.name;
        node.querySelector('.desc').textContent = c.desc || '';
        node.querySelector('.members').textContent = (c.members || 0) + ' members';
        node.querySelector('.tags').textContent = c.tags ? c.tags.join(', ') : '';

        const btn = node.querySelector('.join');
        btn.dataset.id = c.id;
        // join calls backend then navigates to chat
        btn.addEventListener('click', async (e)=>{ 
          e.stopPropagation(); 
          try{
            await fetch(`${BACKEND}/api/communities/${c.id}/join`, { method: 'POST' });
          }catch(_){}
          // include name in query so chat page can display it even if backend lookup fails
          window.location.href = `chat.html?id=${c.id}&name=${encodeURIComponent(c.name)}`; 
        });

        // delete button
        const del = document.createElement('button');
        del.className = 'btn delete';
        del.textContent = 'Delete';
        del.addEventListener('click', async (e)=>{
          e.stopPropagation();
          const confirmDel = confirm('Delete community "' + c.name + '"?');
          if(!confirmDel) return;
          try{
            const res = await fetch(`${BACKEND}/api/communities/${c.id}`, { method: 'DELETE' });
            if(res.ok || res.status === 204){
              communities = communities.filter(x=>x.id !== c.id);
              updateView();
            } else {
              alert('Delete failed');
            }
          }catch(err){
            alert('Delete failed');
          }
        });

        node.querySelector('.card-actions').appendChild(del);

        node.querySelector('.card').addEventListener('click', ()=>{
          window.location.href = `community.html?id=${c.id}`;
        });
        list.appendChild(node);
      });
    }

    // create modal handlers
    if(createBtn){
      createBtn.addEventListener('click', ()=>{
        if(modal){ modal.hidden = false; modal.classList.add('active'); modal.setAttribute('aria-hidden','false'); const nameInput = modal.querySelector('input[name="name"]'); if(nameInput) nameInput.focus(); }
      });
    }
    const cancelBtn = document.getElementById('cancel');
    const modalCloseBtn = document.getElementById('modalClose');
    function closeModal(){ if(modal){ modal.hidden = true; modal.classList.remove('active'); modal.setAttribute('aria-hidden','true'); } if(createForm) createForm.reset(); if(createBtn) createBtn.focus(); }
    if(cancelBtn) cancelBtn.addEventListener('click', closeModal);
    if(modalCloseBtn) modalCloseBtn.addEventListener('click', closeModal);
    if(modal) modal.addEventListener('click', (e)=>{ if(e.target === modal){ closeModal(); } });
    document.addEventListener('keydown', (e)=>{ if(e.key === 'Escape' && modal && !modal.hidden){ closeModal(); } });

    if(createForm){
      createForm.addEventListener('submit', async (e)=>{
        e.preventDefault();
        const fd = new FormData(createForm);
        const body = { name: fd.get('name'), description: fd.get('description'), category: fd.get('category') };
        try{
          const res = await fetch(BACKEND + '/api/communities', { method: 'POST', headers: {'Content-Type':'application/json'}, body: JSON.stringify(body) });
          if(!res.ok) throw new Error('Create failed');
          const created = await res.json();
          communities.unshift(created);
          closeModal();
          updateView();
        }catch(err){
          alert('Could not create community.');
        }
      });
    }

    // search
    // centralized filter + sort + search
    function getFilteredSorted(){
      const q = (search && search.value) ? search.value.trim().toLowerCase() : '';
      const cat = (categorySelect && categorySelect.value) ? categorySelect.value : 'all';
      const sort = (sortSelect && sortSelect.value) ? sortSelect.value : 'trending';

      let out = communities.slice();
      // remove deleted
      const deleted = getDeletedIds();
      out = out.filter(c => !deleted.includes(c.id));
      // category filter
      if(cat && cat !== 'all'){
        out = out.filter(c => Array.isArray(c.tags) ? c.tags.includes(cat) : false);
      }
      // search filter
      if(q){
        out = out.filter(c => (c.name && c.name.toLowerCase().includes(q)) || (c.desc && c.desc.toLowerCase().includes(q)));
      }
      // sort
      if(sort === 'newest'){
        out.sort((a,b)=> (b.id||0) - (a.id||0));
      } else if(sort === 'active'){
        out.sort((a,b)=> (b.members||0) - (a.members||0));
      } else { // trending - combination: members desc then id
        out.sort((a,b)=> {
          const m = (b.members||0) - (a.members||0);
          if(m !== 0) return m;
          return (b.id||0) - (a.id||0);
        });
      }
      return out;
    }

    function updateView(){
      const list = getFilteredSorted();
      render(list);
    }

    if(search) search.addEventListener('input', updateView);
    if(sortSelect) sortSelect.addEventListener('change', updateView);
    if(categorySelect) categorySelect.addEventListener('change', updateView);

    // initial render via updateView
    updateView();
    
  })();
})();
