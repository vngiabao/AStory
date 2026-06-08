# A Story — Landing Page

A voice-first AI app (coming soon, iOS + Android) that gently interviews older
relatives, turns their memories into "memory cards" + a life timeline, and becomes
a keepsake families keep forever. Marketed as a **gift** for parents/grandparents.

- **Buyer:** the adult child / grandchild (e.g. "Sarah", 48)
- **User:** the elder (e.g. "Walter", 79) — every experience must work for someone
  with limited tech patience and aging eyes.
- **Domain:** astoryapp.com · **Contact:** contact@astoryapp.com

---

## Quick start (you can be editing in 2 minutes)

There is **no build step, no install, no dependencies.** It's one static HTML file.

1. Download/unzip this folder.
2. Open it in **VS Code**.
3. Install the **Live Server** extension (by Ritwick Dey) if you don't have it.
4. Right-click `index.html` → **"Open with Live Server."** It opens in your
   browser and auto-reloads every time you save. That's your whole dev loop.

> ⚠️ Don't just double-click `index.html` to open it via `file://` — the
> background **music won't play** that way (browser security blocks local audio
> over `file://`). Live Server serves over `http://`, which fixes it. This is the
> #1 "is it broken?" gotcha — it's not broken, just open it through Live Server.

To edit: everything is in `index.html`. **CSS** is in the `<style>` block in the
`<head>`. **JavaScript** is in the `<script>` block right before `</body>`.

---

## File structure

```
/
├── index.html          ← the entire site (HTML + CSS + JS, all inline)
├── assets/
│   └── music.mp3       ← background score (royalty-free)
├── README.md           ← you are here
├── BRAND.md            ← locked brand: colors, fonts, logo, voice (read this!)
├── TASKS.md            ← your prioritized backlog with acceptance criteria
├── STYLEGUIDE.md       ← one-page cheat sheet to keep open while coding
├── .editorconfig       ← keeps formatting consistent
└── .gitignore
```

---

## How the page actually works

Read this once — it'll save you an hour of confusion.

1. **The gate.** The hero shows only a headline + two buttons. *Everything* below
   (Hook → Demo → How → Photos → Legacy → Safety → Gift → Timeline → Testimonial →
   Waitlist) lives inside a hidden `<div id="content-gate" style="display:none">`.
   Clicking **"Hear it in action"** calls `unlockContent()`, which: reveals the
   gate, starts the music (this click is the required user-gesture for iOS audio),
   re-arms the scroll animations, and scrolls down to the Hook.
   → So if you're editing a section below the hero and "nothing shows up," that's
     why. Temporarily set `#content-gate` to `display:block` while working, or just
     click the hero button each reload.

2. **The interactive demo.** A fake phone runs a scripted AI ↔ elder conversation
   using the browser's `SpeechSynthesis` API + typing animation + a waveform. The
   dialogue lives in the `const script = [ ... ]` array in the JS. Edit copy there.

3. **"Our Story" (founder note).** It is NOT a separate page. It's a full-screen
   overlay `<div id="our-story-page">`, toggled by `openOurStory()` /
   `closeOurStory()` from the nav link. Closing returns you exactly where you were.

4. **Music toggle.** The speaker icon in the nav mutes/unmutes. State lives in
   `musicWanted` / `musicMuted`; the icon swaps between `#icon-sound-on` and
   `#icon-sound-off`. It does nothing until content is unlocked (by design).

5. **Scroll reveals.** Any element with class `.reveal` fades in on scroll via an
   `IntersectionObserver` (the `io` variable). Add `.rd1` / `.rd2` / `.rd3` / `.rd4`
   for staggered delays. New sections you add should follow the same pattern.

---

## ⚠️ Before you change anything visual — read BRAND.md

The brand is **locked**. Colors, fonts, the logo, and the writing voice are not up
for redesign. If you want to change one, message Daniel first. `STYLEGUIDE.md` is
the fast version to keep open while you work.

---

## Hard constraints (please don't break these)

- **Stays a single static `index.html` + `assets/`.** No React, no framework, no
  bundler, no npm. It has to deploy to Cloudflare Pages with zero build.
- **No `localStorage` / `sessionStorage`.** Not needed.
- **Don't wire up the waitlist form.** `submitWaitlist()` is a front-end stub on
  purpose — Daniel is still choosing the email provider. Leave it faking success.
- **Test on a real iPhone (Safari)**, not only desktop Chrome. iOS handles audio,
  scroll, and `100vh` differently — bugs hide there.

---

## Your task list

See **TASKS.md**. It's ordered by priority — work top-down. P0 (accessibility) is
the most important because our end-users are elderly; it's part of the product,
not polish.

Questions, or a better idea than what's written? → contact@astoryapp.com
