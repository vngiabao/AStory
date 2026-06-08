# A Story — Style Cheat Sheet

Keep this open while you code. Full detail in BRAND.md.

## Colors (use the CSS var, never the raw hex)
```
--ink     #2B2117   text / dark logo legs
--ember   #B45A2B   primary accent, buttons, links
--gold    #C7A24E   secondary accent, glow, dark-bg heart
--peach   #D9A97B   soft highlight, elder voice
--cream   #EFE6D4   daylight bg / dark-bg text
--paper-2 #E6D8BF   alt bg
--paper-3 #F6EFE2   lightest bg / cards
```

## Fonts
- **Cormorant Garamond** (serif) → headlines, quotes, anything emotional. Italic = warmth.
- **Figtree** (sans) → buttons, labels, eyebrows, nav, small text.

## Two modes
- **Daylight** = cream bg + ink text (default, ~80%).
- **Firelight** = `#231a12` bg + cream text + gold glow (emotional beats only).

## Reusable patterns already in the file
- `.reveal` + optional `.rd1`/`.rd2`/`.rd3`/`.rd4` → fade-in-on-scroll (stagger).
- `.section-title`, `.eyebrow` → consistent heading + label styles.
- `.btn-primary` (ember fill) / `.btn-ghost` (outline) → buttons.
- Logo SVG → search `viewBox="0 0 64 64"`, copy verbatim, recolor only per BRAND.md.

## Don'ts
- ❌ No emoji (custom SVG icons only).
- ❌ No new fonts or colors.
- ❌ No framework / build step / npm.
- ❌ No localStorage.
- ❌ Don't connect the waitlist form (it's a stub).
- ❌ Don't redraw or recolor the logo outside the two approved variants.

## Dev loop
VS Code → Live Server → edit `index.html` → save → auto-reload.
(Music only plays via Live Server / http, not file://.)
Sections below the hero are hidden until you click "Hear it in action"
(or temporarily set `#content-gate` to `display:block`).
