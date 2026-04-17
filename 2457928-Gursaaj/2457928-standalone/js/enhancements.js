/* ============================================================
   Visual enhancements — Clean Our Sea
   Scroll-triggered animations + nav scroll state.
   Safe to remove: the site works fine without this file.
   ============================================================ */

document.addEventListener("DOMContentLoaded", () => {

  /* --- Fade-up on scroll via IntersectionObserver --- */
  const fadeEls = document.querySelectorAll(".fade-up, .fade-up-stagger");
  if (fadeEls.length && "IntersectionObserver" in window) {
    const obs = new IntersectionObserver(
      (entries) => {
        entries.forEach((e) => {
          if (e.isIntersecting) {
            e.target.classList.add("visible");
            obs.unobserve(e.target);      // animate once only
          }
        });
      },
      { threshold: 0.15 }
    );
    fadeEls.forEach((el) => obs.observe(el));
  } else {
    // Fallback: show everything immediately
    fadeEls.forEach((el) => el.classList.add("visible"));
  }

  /* --- Nav scroll state --- */
  const nav = document.querySelector("nav");
  if (nav) {
    const onScroll = () => {
      nav.classList.toggle("scrolled", window.scrollY > 40);
    };
    window.addEventListener("scroll", onScroll, { passive: true });
    onScroll();
  }
});
