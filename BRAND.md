# A Story — Brand Reference

> The brand is **locked**. These are not suggestions. Changes require Daniel's
> sign-off. `STYLEGUIDE.md` is the quick cheat-sheet version of this file.

## The idea in one line
A warm AI that gently interviews the people you love, and turns a lifetime of
memories into a story your family keeps forever.

**Tagline:** Keep their story forever.
**Hero words:** Talk. Listen. Remembered.

---

## Color palette

| Token   | Hex       | Use |
|---------|-----------|-----|
| Ink     | `#2B2117` | Primary text; logo legs on light bg |
| Ember   | `#B45A2B` | Primary accent; buttons, links; logo heart on light bg |
| Gold    | `#C7A24E` | Secondary accent; glow; logo heart on dark bg |
| Peach   | `#D9A97B` | Soft highlight; elder voice in demo |
| Cream   | `#EFE6D4` | Primary background (daylight); text on dark bg |
| Paper 2 | `#E6D8BF` | Alt background |
| Paper 3 | `#F6EFE2` | Lightest background; cards |

CSS variables are defined in `:root` at the top of the `<style>` block. Always use
the variables (e.g. `var(--ember)`), never hardcode hex in new rules.

---

## Two-mode system

**DAYLIGHT — primary (~80% of the page)**
- Background cream `#EFE6D4`, text ink `#2B2117`, accents ember + gold.
- Rationale: elderly eyes; memory should feel warm and sunlit, not funereal.

**FIRELIGHT — accent only (emotional beats)**
- Background dark `#231a12`, text cream `#EFE6D4`, gold radial glow.
- Used in: Hook, Demo, Our Story hero, footer.
- Don't add new firelight sections without a reason — the contrast is what makes
  the emotional moments land. Overusing it kills the effect.

---

## Typography

- **Display / serif:** Cormorant Garamond — headlines, quotes, elder voice,
  timeline cards. Often italic for warmth.
- **UI / sans:** Figtree — buttons, labels, eyebrows, small body text, nav.

Loaded from Google Fonts in `<head>`. Weights in use: Cormorant 300/400/500/600
(+ italic); Figtree 300/400/500/600. Do not introduce other typefaces.

---

## Logo

A calligraphic "A" formed by two leg paths, with a heart nested in the center.

- **On light bg:** legs `#2B2117` (ink), heart `#B45A2B` (ember)
- **On dark bg:** legs `#EFE6D4` (cream), heart `#C7A24E` (gold)

The exact SVG paths are already in `index.html` (search `viewBox="0 0 64 64"`).
**Reuse them verbatim.** Never redraw it, never recolor outside the two variants
above, never replace it with an icon font or emoji.

---

## Voice & tone

Write like **a kind grandchild** — warm, plain, unhurried, never salesy.

- ✅ "Someone, finally, to ask."
- ✅ "Before someday becomes too late."
- ❌ "Unlock your family's potential with AI-powered memory solutions!"
- ❌ Hype, urgency gimmicks, exclamation overload, corporate jargon.

Respect the gravity of the subject (aging, memory, legacy) without being morbid.
The feeling is golden-hour, not graveyard.

---

## Iconography

Custom inline SVG only — ember/gold palette, ~1.4 stroke weight, rounded caps and
joins. **Never emoji.** Match the existing icons in the Legacy and Safety sections.

---

## Imagery direction

Vintage / sepia family photographs — weddings, dinners, mid-century snapshots.
Warm tones that sit naturally on the paper background. Must be royalty-free or
properly licensed. Avoid stock clichés (smiling models, white backdrops).
