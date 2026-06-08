# A Story — Intern Task Backlog

Prioritized. Work top-down. Test on a **real iPhone**, not just desktop Chrome.
When in doubt about a brand or product call, ask Daniel — don't guess.

---

## P0 — Accessibility pass  (most important — our users are elderly)
Our end-user is 75+. This is part of the product, not polish. None of the items
below currently exist in the code — confirmed gaps, all yours to add.

- [ ] **`prefers-reduced-motion` support.** When the user has reduced motion on,
      disable the ember float, the hero word-reveal, and heavy transitions. Wrap
      animation rules in `@media (prefers-reduced-motion: no-preference)` or add a
      `@media (prefers-reduced-motion: reduce){ ... }` override. (Not present yet.)
- [ ] **Visible focus states** on every interactive element (buttons, nav links,
      music toggle, the email input). Keyboard `Tab` should always show where you
      are. Use an ember outline that fits the brand.
- [ ] **Labels / alt text.** Every icon-only button needs `aria-label`; decorative
      SVGs get `aria-hidden="true"`; meaningful images get real `alt`.
- [ ] **Contrast audit** to WCAG AA (4.5:1 for text). Watch the dimmed cream text
      on firelight backgrounds and the `--ink-dim` greys — some may fail.
- [ ] **Readable sizing.** Nothing critical below ~16px; comfortable line-height.

**Done when:** fully keyboard-navigable, axe-devtools shows no critical issues, and
a reduced-motion user gets a calm (not frozen, not broken) experience.

---

## P0 — Share + browser polish  (needed before the link goes anywhere public)
Also confirmed missing from `<head>` right now.

- [ ] **Favicon + apple-touch-icon** using the logo. (No `rel="icon"` yet.)
- [ ] **Meta description** for search/sharing. (None present.)
- [ ] **Open Graph + Twitter tags** (`og:title`, `og:description`, `og:image`,
      `twitter:card`) so the link previews nicely in iMessage / LinkedIn / WhatsApp.
      Daniel will supply a 1200×630 `og:image`; build the tags and wire it in.

**Done when:** pasting the URL into iMessage shows a proper title, blurb, and image;
the browser tab shows the logo favicon.

---

## P1 — Real mobile navigation
The nav crowds on small screens (logo + music toggle + "Our Story" + "Join Waitlist").

- [ ] Design a clean mobile nav — hamburger or a simplified bar, your call.
- [ ] "Join Waitlist" must stay reachable; music toggle must stay accessible.
- [ ] Must not cover the hero headline on first paint.

**Done when:** the nav looks intentional and uncramped on a 375px iPhone SE.

---

## P1 — Replace placeholder photos
The "What gets lost" section uses crude hand-drawn SVG photo placeholders.

- [ ] Source 2–3 royalty-free vintage/sepia family photos (see BRAND.md imagery).
- [ ] Drop them into the photo-stack, keeping the existing rotation/overlap and the
      floating "memory tag" labels.
- [ ] Optimize (WebP, lazy-load).

**Done when:** the section reads as real family memories, loads fast, and the photos
sit naturally on the paper palette.

---

## P2 — Timeline touch polish
The horizontal life-timeline scrolls awkwardly on touch.

- [ ] Smooth momentum scrolling on iOS/Android.
- [ ] Add a subtle scroll affordance (edge fade or hint) so users know it scrolls.
- [ ] Verify the above/below card alternation never overlaps on small screens.

---

## P2 — Privacy policy
We collect emails, so this is required before public launch.

- [ ] Add a lightweight privacy-policy overlay (reuse the Our Story overlay pattern)
      or a `privacy.html`. Plain-language, on-brand voice.
- [ ] Link it from the footer.
- [ ] Cover: what we collect (email), why, that we never sell data, how to opt out.

> Daniel provides final legal copy — you build the container + styling.

---

## P3 — Nice-to-haves (only after the above)
- [ ] Skeleton/loading state for any section that feels heavy on slow connections.
- [ ] Revisit the demo dialogue for emotional punch (the `script[]` array in JS).
- [ ] Light QA pass across Safari, Chrome, Firefox + iOS + Android.

---

## Hard constraints (do not break)
- Single static `index.html` + `assets/`. No framework, no build step, no npm.
- No `localStorage` / `sessionStorage`.
- Do NOT connect the waitlist form to a backend — it's a deliberate stub for now.
- Brand is locked (colors, fonts, logo, voice) — see BRAND.md, get sign-off.

## Workflow
- Branch per task; small PRs; say what changed and why.
- Include **before/after screenshots (desktop + iPhone)** in every PR.
- Ask early when unsure → contact@astoryapp.com
