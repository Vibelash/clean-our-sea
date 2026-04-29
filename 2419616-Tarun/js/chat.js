(function(){
  // Combined backend origin — frontend is on :8000, API on :8080 in local dev.
  const BACKEND = "https://clean-our-sea-backend.onrender.com";

  function qs(name){ return new URLSearchParams(location.search).get(name); }
  const id = Number(qs('id')) || null;

  async function fetchCommunity(){
    try{ const res = await fetch(`${BACKEND}/api/communities/${id}`); if(!res.ok) throw new Error('Network'); return await res.json(); }catch(e){ return null; }
  }

  (async function init(){

    const c = await fetchCommunity();
    // fallback name from query param if server request fails
    const maybeName = qs('name');
    const community = c || { name: maybeName || 'Unknown' };
    const titleText = community.name + ' — Chat';
    document.getElementById('communityName').textContent = titleText;
    document.title = titleText;

    const messages = document.getElementById('messages');

    function escapeHtml(s){ return String(s).replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;'); }

    function formatTime(iso){ try{ return new Date(iso).toLocaleString(); }catch(e){ return ''; } }

    async function loadMsgs(){
      let msgs = [];
      try{
        const res = await fetch(`${BACKEND}/api/communities/${id}/posts`);
        if(res.ok) msgs = await res.json();
      }catch(e){ /* ignore network errors */ }
      // merge any locally saved messages that couldn't be sent
      try{
        const local = JSON.parse(localStorage.getItem(`chat_${id}`) || '[]');
        if(Array.isArray(local) && local.length) msgs = msgs.concat(local);
      }catch(e){ /* ignore parse errors */ }
      return msgs;
    }

    const render = (msgs) => {
      messages.innerHTML = '';
      msgs.forEach(m=>{
        const el = document.createElement('div');
        el.style.padding = '.4rem';
        const time = m.createdAt ? formatTime(m.createdAt) : '';
        el.innerHTML = `<strong>${escapeHtml(m.author)}</strong> <span class="muted">• ${time}</span><div>${escapeHtml(m.text)}</div>`;
        messages.appendChild(el);
      });
      messages.scrollTop = messages.scrollHeight;
    }

    const chatForm = document.getElementById('chatForm');
    chatForm.addEventListener('submit', async (e)=>{
      e.preventDefault();
      const author = document.getElementById('author').value || 'Anon';
      const msg = document.getElementById('msg').value;
      const entry = { author, text: msg, createdAt: new Date().toISOString() };
      try{
        const res = await fetch(`${BACKEND}/api/communities/${id}/posts`, { method: 'POST', headers: {'Content-Type':'application/json'}, body: JSON.stringify({ author, text: msg })});
        if(!res.ok) throw new Error('Send failed');
        document.getElementById('msg').value = '';
      }catch(err){
        // save locally for reliability
        try{
          const key = `chat_${id}`;
          const arr = JSON.parse(localStorage.getItem(key) || '[]');
          arr.push(entry);
          localStorage.setItem(key, JSON.stringify(arr));
        }catch(e){ /* ignore */ }
        alert('Message will appear locally; server unreachable.');
      }
      const msgs = await loadMsgs();
      render(msgs);
    });

    // initial render
    (async ()=>{ const msgs = await loadMsgs(); render(msgs); })();
  })();
})();
