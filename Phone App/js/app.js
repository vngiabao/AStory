import {
  loadState, saveState, resetState,
  createStory, createMemory,
  ERAS, RELATIONSHIPS, REL_COLOR,
} from "./store.js";
import {
  getPrompts, aiFollowUpResponse, aiReflection, aiPeriodQuestion, draftMemoryFromAnswers,
} from "./interview.js";
import { printLegacy } from "./print.js";
import {
  getSession, setSession, clearSession,
  createAccount, verifyLogin, ensureDemoState,
  DEMO_EMAIL, DEMO_PASSWORD,
} from "./auth.js";

/* ─── Globals ─── */
let currentUser      = null;   // { email, firstName, lastName }
let state            = null;
let route            = { name: "dashboard", params: {} };
let session          = null;   // active interview session
let storyDraft       = {};     // accumulates new-story answers
let photoMemoryDraft = null;   // photos pending post creation

const MONTHS = ["January","February","March","April","May","June",
  "July","August","September","October","November","December"];
const DAYS = Array.from({ length: 31 }, (_, i) => i + 1);

const DEMO_VOICES = [
  "The wooden porch creaked every time it rained. I can still hear her calling us in.",
  "I was terrified — I'd never done anything like it before. But somehow I kept going.",
  "She had this laugh that filled the whole room. You couldn't help but smile.",
  "The smell of sawdust and coffee. That's what I remember most.",
  "We stayed up till sunrise. I don't think I've ever felt that free since.",
];

const DAILY_SUGGESTIONS = [
  ["Childhood", "School years", "Career"],
  ["Love & family", "Later years", "Wisdom"],
  ["Childhood", "Career", "Wisdom"],
  ["School years", "Love & family", "Later years"],
];

const app = document.getElementById("app");
const h = (s) => String(s ?? "")
  .replace(/&/g,"&amp;").replace(/</g,"&lt;")
  .replace(/>/g,"&gt;").replace(/"/g,"&quot;");

/* ─── SVG icon library ─── */
const ICONS = {
  home:     `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="m3 9 9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/></svg>`,
  mic:      `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><rect x="9" y="2" width="6" height="11" rx="3"/><path d="M5 10v1a7 7 0 0 0 14 0v-1"/><path d="M12 19v3"/><path d="M8 22h8"/></svg>`,
  tree:     `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M12 22V2"/><path d="M12 8C10 8 6 7 5 4c2 0 5.5.5 7 4z"/><path d="M12 14c-2 0-6-1-7-4 2 0 5.5.5 7 4z"/><path d="M12 8c2 0 6-1 7-4-2 0-5.5.5-7 4z"/><path d="M12 14c2 0 6 1 7 4-2 0-5.5-.5-7-4z"/></svg>`,
  menu:     `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"><path d="M4 6h16M4 12h16M4 18h16"/></svg>`,
  book:     `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"/><path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"/></svg>`,
  people:   `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>`,
  star:     `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/></svg>`,
  chat:     `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/></svg>`,
  lock:     `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="11" width="18" height="11" rx="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg>`,
  settings: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="3"/><path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1-2.83 2.83l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-4 0v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83-2.83l.06-.06A1.65 1.65 0 0 0 4.68 15a1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1 0-4h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 2.83-2.83l.06.06A1.65 1.65 0 0 0 9 4.68a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 4 0v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 2.83l-.06.06A1.65 1.65 0 0 0 19.4 9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 0 4h-.09a1.65 1.65 0 0 0-1.51 1z"/></svg>`,
  refresh:  `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><polyline points="23 4 23 10 17 10"/><path d="M20.49 15a9 9 0 1 1-2.12-9.36L23 10"/></svg>`,
  signout:  `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" y1="12" x2="9" y2="12"/></svg>`,
  search:   `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><circle cx="11" cy="11" r="8"/><path d="m21 21-4.35-4.35"/></svg>`,
  faq:      `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><path d="M9.09 9a3 3 0 0 1 5.83 1c0 2-3 3-3 3"/><circle cx="12" cy="17" r=".5" fill="currentColor"/></svg>`,
  mail:     `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"/><polyline points="22,6 12,13 2,6"/></svg>`,
  bug:      `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M20 9v-1a4 4 0 0 0-4-4h-8a4 4 0 0 0-4 4v1"/><path d="M4 13h16"/><path d="M4 17h16"/><rect x="8" y="9" width="8" height="12" rx="2"/><path d="M8 9V7"/><path d="M16 9V7"/><path d="M2 13h2"/><path d="M20 13h2"/></svg>`,
  idea:     `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M9 18h6M10 22h4"/><path d="M12 2a7 7 0 0 1 5 11.9V17H7v-3.1A7 7 0 0 1 12 2z"/></svg>`,
  camera:   `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M23 19a2 2 0 0 1-2 2H3a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h4l2-3h6l2 3h4a2 2 0 0 1 2 2z"/><circle cx="12" cy="13" r="4"/></svg>`,
  user:     `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>`,
  bell:     `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"/><path d="M13.73 21a2 2 0 0 1-3.46 0"/></svg>`,
  download: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="7 10 12 15 17 10"/><line x1="12" y1="15" x2="12" y2="3"/></svg>`,
  chevron:  `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"><polyline points="9 18 15 12 9 6"/></svg>`,
  add:      `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>`,
  photo:    `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="3" width="18" height="18" rx="2"/><circle cx="8.5" cy="8.5" r="1.5"/><polyline points="21 15 16 10 5 21"/></svg>`,
};

/* ═══════════════════════════════════════════
   BOOT
═══════════════════════════════════════════ */
export function init() {
  const savedTheme = localStorage.getItem("astory-theme");
  if (savedTheme) document.body.dataset.theme = savedTheme;

  const s = getSession();
  if (s?.email) {
    currentUser = s;
    if (s.email === DEMO_EMAIL) ensureDemoState();
    state = loadState(s.email);
    startApp();
  } else {
    showSplash();
  }
}

function startApp() {
  window.addEventListener("hashchange", parseRoute);
  parseRoute();
}

function persist() {
  if (currentUser) saveState(currentUser.email, state);
}

function fmtDate(iso) {
  return new Date(iso).toLocaleDateString(undefined,
    { month: "short", day: "numeric", year: "numeric" });
}

function greeting() {
  const hr = new Date().getHours();
  return hr < 12 ? "Good morning" : hr < 18 ? "Good afternoon" : "Good evening";
}

function activeStory() {
  return state.stories.find(s => s.id === state.activeStoryId)
      || state.stories[0]
      || null;
}

function storyById(id) { return state.stories.find(s => s.id === id) || null; }

function fmtDob(day, month, year) {
  const parts = [day, month, year].filter(Boolean);
  return parts.length ? parts.join(" ") : "";
}

function relLabel(story) {
  return story.relationship === "Other" && story.relationshipOther
    ? story.relationshipOther : story.relationship;
}

function storyAccent(story) { return REL_COLOR[story.relationship] || "#d4a574"; }

function isOwn(story) { return story.relationship === "Myself"; }

/* ═══════════════════════════════════════════
   AUTH SCREENS
═══════════════════════════════════════════ */
function showSplash() {
  app.innerHTML = `
    <section class="auth-splash">
      <div class="auth-splash-mark">
        <span class="auth-splash-a">A</span>
        <span class="auth-splash-word">Story</span>
      </div>
      <p class="auth-splash-tag">Every life holds a story worth preserving.</p>
    </section>`;
  setTimeout(showWelcome, 2000);
}

function showWelcome() {
  app.innerHTML = `
    <section class="auth-screen auth-welcome">
      <div class="auth-glow" aria-hidden="true"></div>
      <div class="auth-brand">
        <div class="auth-mark">
          <span class="auth-mark-a">A</span>
          <span class="auth-mark-word">Story</span>
        </div>
        <p class="auth-mark-tag">Every life holds a story worth preserving.</p>
      </div>
      <div class="auth-cta-group">
        <button class="btn btn--primary btn--large btn--block" id="btn-started">Get started</button>
        <button class="btn btn--ghost btn--large btn--block" id="btn-signin-go">I already have an account</button>
      </div>
      <button class="auth-demo-link" id="btn-demo">Try Walter's demo archive →</button>
    </section>`;

  document.getElementById("btn-started").onclick  = showSignUp;
  document.getElementById("btn-signin-go").onclick = showSignIn;
  document.getElementById("btn-demo").onclick      = () => loginAs(DEMO_EMAIL, "Sarah", "", true);
}

function showSignUp() {
  app.innerHTML = `
    <section class="auth-screen auth-form-screen">
      <div class="auth-glow" aria-hidden="true"></div>
      <button class="auth-back" id="auth-back">←</button>
      <div class="auth-form-top">
        <p class="auth-form-eyebrow">Create account</p>
        <h2 class="auth-form-title">Join A Story</h2>
        <p class="auth-form-sub">Start building your family's archive.</p>
      </div>
      <form class="auth-form" id="su-form" novalidate>
        <div class="auth-field-row">
          <div class="auth-field">
            <label class="auth-label" for="su-fn">First name</label>
            <input class="auth-input" id="su-fn" name="firstName" type="text"
              placeholder="First" autocomplete="given-name" required />
          </div>
          <div class="auth-field">
            <label class="auth-label" for="su-ln">Last name</label>
            <input class="auth-input" id="su-ln" name="lastName" type="text"
              placeholder="Last" autocomplete="family-name" required />
          </div>
        </div>
        <div class="auth-field">
          <label class="auth-label" for="su-em">Email</label>
          <input class="auth-input" id="su-em" name="email" type="email"
            placeholder="you@example.com" autocomplete="email" required />
        </div>
        <div class="auth-field">
          <label class="auth-label" for="su-pw">Password</label>
          <div class="auth-input-row">
            <input class="auth-input" id="su-pw" name="password" type="password"
              placeholder="Min. 6 characters" autocomplete="new-password" required minlength="6" />
            <button type="button" class="auth-reveal" data-for="su-pw"><span class="eye">👁</span></button>
          </div>
        </div>
        <div class="auth-field">
          <label class="auth-label" for="su-cp">Confirm password</label>
          <div class="auth-input-row">
            <input class="auth-input" id="su-cp" name="confirm" type="password"
              placeholder="Repeat password" autocomplete="new-password" required />
            <button type="button" class="auth-reveal" data-for="su-cp"><span class="eye">👁</span></button>
          </div>
        </div>
        <label class="auth-privacy">
          <input type="checkbox" id="su-privacy" required />
          <span>I agree to the <button type="button" class="auth-link-btn" onclick="window.showPrivacy()">Terms & Privacy Policy</button></span>
        </label>
        <p class="auth-error" id="su-err" aria-live="polite"></p>
        <button type="submit" class="btn btn--primary btn--large btn--block" id="btn-su">Create account →</button>
      </form>
      <div class="social-divider"><span>or continue with</span></div>
      <div class="social-btns">
        <button class="social-btn" onclick="alert('Google sign-in coming soon.')">
          <span class="social-icon">G</span> Google
        </button>
        <button class="social-btn" onclick="alert('Facebook sign-in coming soon.')">
          <span class="social-icon social-icon--fb">f</span> Facebook
        </button>
      </div>
      <p class="auth-toggle">Already have an account?
        <button type="button" class="auth-toggle-btn" id="go-si">Sign in</button>
      </p>
    </section>`;

  document.getElementById("auth-back").onclick = showWelcome;
  document.getElementById("go-si").onclick      = showSignIn;
  document.querySelectorAll(".auth-reveal").forEach(btn => {
    btn.onclick = (e) => togglePw(e.currentTarget.dataset.for, e.currentTarget);
  });
  document.getElementById("su-form").onsubmit = (e) => {
    e.preventDefault();
    const fd  = new FormData(e.target);
    const fn  = fd.get("firstName").toString().trim();
    const ln  = fd.get("lastName").toString().trim();
    const em  = fd.get("email").toString().trim();
    const pw  = fd.get("password").toString();
    const cp  = fd.get("confirm").toString();
    const prv = document.getElementById("su-privacy").checked;
    const err = document.getElementById("su-err");
    const btn = document.getElementById("btn-su");

    if (!fn || !ln || !em) { err.textContent = "Please fill in all fields."; return; }
    if (pw.length < 6)     { err.textContent = "Password must be at least 6 characters."; return; }
    if (pw !== cp)         { err.textContent = "Passwords don't match."; return; }
    if (!prv)              { err.textContent = "Please agree to the Privacy Policy."; return; }

    btn.disabled = true; btn.textContent = "Creating…";
    const res = createAccount(fn, ln, em, pw);
    if (res.error) { err.textContent = res.error; btn.disabled = false; btn.textContent = "Create account →"; return; }
    loginAs(em, fn, ln, false, true); // isNew=true → show pricing gate
  };
  document.getElementById("su-fn").focus();
}

function showSignIn() {
  app.innerHTML = `
    <section class="auth-screen auth-form-screen">
      <div class="auth-glow" aria-hidden="true"></div>
      <button class="auth-back" id="auth-back">←</button>
      <div class="auth-form-top">
        <p class="auth-form-eyebrow">Welcome back</p>
        <h2 class="auth-form-title">Sign in</h2>
        <p class="auth-form-sub">Your stories are waiting.</p>
      </div>
      <form class="auth-form" id="si-form" novalidate>
        <div class="auth-field">
          <label class="auth-label" for="si-em">Email</label>
          <input class="auth-input" id="si-em" name="email" type="email"
            placeholder="you@example.com" autocomplete="email" required />
        </div>
        <div class="auth-field">
          <label class="auth-label" for="si-pw">Password</label>
          <div class="auth-input-row">
            <input class="auth-input" id="si-pw" name="password" type="password"
              placeholder="Your password" autocomplete="current-password" required />
            <button type="button" class="auth-reveal" data-for="si-pw"><span class="eye">👁</span></button>
          </div>
        </div>
        <p class="auth-error" id="si-err" aria-live="polite"></p>
        <button type="submit" class="btn btn--primary btn--large btn--block" id="btn-si">Sign in →</button>
      </form>
      <div class="social-divider"><span>or</span></div>
      <div class="social-btns">
        <button class="social-btn" onclick="alert('Google sign-in coming soon.')">
          <span class="social-icon">G</span> Google
        </button>
        <button class="social-btn" onclick="alert('Facebook sign-in coming soon.')">
          <span class="social-icon social-icon--fb">f</span> Facebook
        </button>
      </div>
      <div class="demo-card">
        <div class="demo-card-info">
          <p class="demo-card-label">Try the demo</p>
          <p class="demo-card-name">Walter's archive — 3 memories ready to explore</p>
          <p class="demo-card-creds">${DEMO_EMAIL} · ${DEMO_PASSWORD}</p>
        </div>
        <button class="demo-card-btn" id="btn-fill">Use demo →</button>
      </div>
      <p class="auth-toggle">Don't have an account?
        <button type="button" class="auth-toggle-btn" id="go-su">Create one</button>
      </p>
    </section>`;

  document.getElementById("auth-back").onclick = showWelcome;
  document.getElementById("go-su").onclick      = showSignUp;
  document.getElementById("btn-fill").onclick   = () => {
    document.getElementById("si-em").value = DEMO_EMAIL;
    document.getElementById("si-pw").value = DEMO_PASSWORD;
  };
  document.querySelector(".auth-reveal").onclick = (e) => togglePw("si-pw", e.currentTarget);
  document.getElementById("si-form").onsubmit = (e) => {
    e.preventDefault();
    const fd  = new FormData(e.target);
    const em  = fd.get("email").toString().trim();
    const pw  = fd.get("password").toString();
    const err = document.getElementById("si-err");
    const btn = document.getElementById("btn-si");
    btn.disabled = true; btn.textContent = "Signing in…";
    const res = verifyLogin(em, pw);
    if (res.error) { err.textContent = res.error; btn.disabled = false; btn.textContent = "Sign in →"; return; }
    loginAs(em, res.firstName, res.lastName, res.isDemo);
  };
  document.getElementById("si-em").focus();
}

function togglePw(inputId, btn) {
  const input = document.getElementById(inputId);
  input.type  = input.type === "password" ? "text" : "password";
  btn.querySelector(".eye").textContent = input.type === "password" ? "👁" : "🙈";
}

function loginAs(email, firstName, lastName, isDemo, isNew = false) {
  if (isDemo) ensureDemoState();
  currentUser = { email: email.toLowerCase().trim(), firstName, lastName };
  setSession(currentUser);
  state = loadState(currentUser.email);
  if (isNew) {
    showWelcomeVideo();   // new user → video → pricing → app
  } else {
    startApp();           // returning user / demo → straight to app
  }
}

function showWelcomeVideo() {
  app.innerHTML = `
    <section class="welcome-video-screen">
      <div class="auth-glow" aria-hidden="true"></div>
      <div class="wv-inner">
        <div class="wv-logo">
          <span class="auth-mark-a">A</span><span class="auth-mark-word">Story</span>
        </div>
        <p class="wv-tagline">Preserving what matters most.</p>
        <div class="wv-play-area">
          <div class="wv-play-btn" aria-hidden="true">▶</div>
          <p class="wv-play-label">Welcome video · 45 sec</p>
          <p class="wv-play-sub">Coming soon — tap Continue to explore</p>
        </div>
        <button class="btn btn--primary btn--large" id="btn-wv-continue">
          Continue →
        </button>
      </div>
    </section>`;
  document.getElementById("btn-wv-continue").onclick = showPricingGate;
  setTimeout(() => { if (document.getElementById("btn-wv-continue")) showPricingGate(); }, 3500);
}

/* Pricing gate shown to new users after sign-up — before they enter the app */
function showPricingGate() {
  app.innerHTML = `
    <section class="auth-screen auth-pricing-gate">
      <div class="auth-glow" aria-hidden="true"></div>

      <div class="pg-header">
        <h2 class="pg-title">A Story</h2>
        <p class="pg-sub">Keep their voice forever</p>
      </div>

      <!-- Billing toggle -->
      <div class="pricing-toggle-wrap" style="position:relative;z-index:1;">
        <div class="plan-toggle">
          <button class="toggle-pill is-active" id="pg-monthly">Monthly</button>
          <button class="toggle-pill" id="pg-annual">Annual</button>
        </div>
        <span class="pricing-save-badge" id="pg-save-badge">2 months free</span>
      </div>

      <!-- Individual -->
      <div class="pricing-card tier-pro" style="position:relative;z-index:1;">
        <div class="pricing-card-row" style="margin-bottom:0.875rem;">
          <div>
            <h3 class="plan-name">Individual</h3>
            <p class="plan-desc">One storyteller, full access</p>
          </div>
          <div class="plan-price-wrap">
            <span class="plan-price" id="pg-pro-price">$8</span>
            <span class="plan-period" id="pg-pro-period">per month</span>
          </div>
        </div>
        <ul class="plan-features">
          <li class="feat-yes"><span class="feat-icon">✓</span> Unlimited AI conversations</li>
          <li class="feat-yes"><span class="feat-icon">✓</span> 50 memory cards / month</li>
          <li class="feat-yes"><span class="feat-icon">✓</span> Photo uploads &amp; free export</li>
          <li class="feat-yes"><span class="feat-icon">✓</span> Ad-free · 7-day free trial</li>
        </ul>
        <button class="btn plan-btn tier-pro-btn btn--large btn--block" id="pg-pro">
          Start 7 days free →
        </button>
        <p class="plan-footnote">No charge for 7 days · cancel anytime</p>
      </div>

      <!-- Family (featured) -->
      <div class="pricing-card pricing-card--featured" style="position:relative;z-index:1;">
        <div class="pricing-badge"><span class="badge-dot"></span> Most families choose this</div>
        <div class="pricing-card-row" style="margin-bottom:0.875rem;">
          <div>
            <h3 class="plan-name">Family</h3>
            <p class="plan-desc">Up to 3 storytellers</p>
          </div>
          <div class="plan-price-wrap">
            <span class="plan-price" id="pg-fam-price">$13</span>
            <span class="plan-period" id="pg-fam-period">per month</span>
          </div>
        </div>
        <ul class="plan-features">
          <li class="feat-yes"><span class="feat-icon">✓</span> Everything in Individual</li>
          <li class="feat-yes"><span class="feat-icon">✓</span> Up to 3 storyteller profiles</li>
          <li class="feat-yes"><span class="feat-icon">✓</span> Shared family archive</li>
          <li class="feat-yes"><span class="feat-icon">✓</span> Invite family to view</li>
        </ul>
        <button class="btn plan-btn plan-btn--featured btn--large btn--block" id="pg-family">
          Start 7 days free →
        </button>
        <p class="plan-footnote">No charge for 7 days · cancel anytime</p>
      </div>

      <p class="pricing-gate-note" style="position:relative;z-index:1;">
        <a href="#" id="pg-all-plans">See all plans including Family Plus ($20/mo) →</a>
      </p>

      <button class="pg-skip" id="pg-skip" style="position:relative;z-index:1;">Continue for free →</button>
    </section>`;

  const prices = {
    pro: { m: "$8",  mPer: "per month", a: "$80",  aPer: "per year" },
    fam: { m: "$13", mPer: "per month", a: "$130", aPer: "per year" },
  };

  function switchBilling(annual) {
    document.getElementById("pg-monthly").classList.toggle("is-active", !annual);
    document.getElementById("pg-annual").classList.toggle("is-active", annual);
    document.getElementById("pg-save-badge").classList.toggle("visible", annual);
    document.getElementById("pg-pro-price").textContent  = annual ? prices.pro.a : prices.pro.m;
    document.getElementById("pg-pro-period").textContent = annual ? prices.pro.aPer : prices.pro.mPer;
    document.getElementById("pg-fam-price").textContent  = annual ? prices.fam.a : prices.fam.m;
    document.getElementById("pg-fam-period").textContent = annual ? prices.fam.aPer : prices.fam.mPer;
  }

  document.getElementById("pg-monthly").onclick    = () => switchBilling(false);
  document.getElementById("pg-annual").onclick     = () => switchBilling(true);
  document.getElementById("pg-pro").onclick        = () => window.showComingSoon("Individual plan");
  document.getElementById("pg-family").onclick     = () => window.showComingSoon("Family plan");
  document.getElementById("pg-all-plans").onclick  = (e) => { e.preventDefault(); startApp(); navigate("pricing"); };
  document.getElementById("pg-skip").onclick       = () => startApp();
}

/* ═══════════════════════════════════════════
   ROUTER
═══════════════════════════════════════════ */
function parseRoute() {
  const hash  = location.hash.slice(1) || "/";
  const parts = hash.split("/").filter(Boolean);
  const [p0, p1, p2, p3] = parts;

  if (!state.onboardingComplete && p0 !== "new-story") {
    navigate("new-story"); return;
  }

  if (p0 === "story" && p1) {
    const p2base = p2 ? p2.split("?")[0] : "";
    if      (p2base === "talk")   route = { name: "talk",   params: { storyId: p1 } };
    else if (p2base === "tree")   route = { name: "tree",   params: { storyId: p1 } };
    else if (p2base === "memory") route = { name: "memory", params: { storyId: p1, memId: p3 } };
    else                          route = { name: "story",  params: { storyId: p1 } };
  } else {
    switch (p0) {
      case "new-story":    route = { name: "new-story",    params: {} }; break;
      case "menu":         route = { name: "menu",         params: {} }; break;
      case "pricing":      route = { name: "pricing",      params: {} }; break;
      case "legacy-book":  route = { name: "legacy-book",  params: {} }; break;
      case "invite":       route = { name: "invite",        params: {} }; break;
      case "help":         route = { name: "help",          params: {} }; break;
      case "faq":          route = { name: "faq",           params: {} }; break;
      case "feedback":     route = { name: "feedback",      params: {} }; break;
      case "contact":      route = { name: "contact",       params: {} }; break;
      case "privacy":       route = { name: "privacy",       params: {} }; break;
      case "settings":      route = { name: "settings",      params: {} }; break;
      case "photo-memory":  route = { name: "photo-memory",  params: {} }; break;
      default:             route = { name: "dashboard",     params: {} };
    }
  }
  render();
}

function navigate(name, params = {}) {
  const map = {
    dashboard:  "#/",
    "new-story":"#/new-story",
    story:      `#/story/${params.storyId}`,
    talk:       `#/story/${params.storyId}/talk`,
    tree:       `#/story/${params.storyId}/tree`,
    memory:     `#/story/${params.storyId}/memory/${params.memId}`,
    menu:          "#/menu",
    pricing:       "#/pricing",
    "legacy-book": "#/legacy-book",
    invite:        "#/invite",
    help:          "#/help",
    faq:           "#/faq",
    feedback:      "#/feedback",
    contact:       "#/contact",
    privacy:         "#/privacy",
    "photo-memory":  "#/photo-memory",
  };
  location.hash = map[name] || "#/";
}

function render() {
  switch (route.name) {
    case "new-story":  app.innerHTML = renderNewStory();                                break;
    case "dashboard":  app.innerHTML = renderDashboard();                               break;
    case "story":      app.innerHTML = renderStoryView(route.params.storyId);           break;
    case "talk":       app.innerHTML = renderChat(route.params.storyId);                break;
    case "tree":       app.innerHTML = renderTree(route.params.storyId);                break;
    case "memory":     app.innerHTML = renderMemory(route.params.storyId, route.params.memId); break;
    case "menu":       app.innerHTML = renderMenu();                                    break;
    case "pricing":      app.innerHTML = renderPricing();                              break;
    case "legacy-book":  app.innerHTML = renderLegacyBook();                           break;
    case "invite":       app.innerHTML = renderInvite();                               break;
    case "help":         app.innerHTML = renderHelp();                                 break;
    case "faq":          app.innerHTML = renderFAQ();                                  break;
    case "feedback":     app.innerHTML = renderFeedback();                             break;
    case "contact":      app.innerHTML = renderContact();                              break;
    case "privacy":      app.innerHTML = renderPrivacy();                              break;
    case "settings":       app.innerHTML = renderSettings();                           break;
    case "photo-memory":   app.innerHTML = renderPhotoMemory();                        break;
    default:             app.innerHTML = renderDashboard();
  }
  bindEvents();
}

/* ─── 4-tab nav shell ─── */
function tabs(active, storyId) {
  const sid    = storyId || activeStory()?.id || "";
  const talkHref = sid ? `#/story/${sid}/talk` : "#/new-story";
  const treeHref = sid ? `#/story/${sid}/tree` : "#/";

  return `
    <nav class="tab-bar">
      <a href="#/" class="tab ${active === "home" ? "is-active" : ""}">
        <span class="tab-icon">${ICONS.home}</span><span>Home</span>
      </a>
      <a href="${talkHref}" class="tab tab--talk ${active === "talk" ? "is-active" : ""}">
        <span class="tab-icon tab-icon--talk">${ICONS.mic}</span><span>Talk</span>
      </a>
      <a href="${treeHref}" class="tab ${active === "tree" ? "is-active" : ""}">
        <span class="tab-icon">${ICONS.tree}</span><span>Archive</span>
      </a>
      <a href="#/menu" class="tab ${active === "menu" ? "is-active" : ""}">
        <span class="tab-icon">${ICONS.menu}</span><span>Menu</span>
      </a>
    </nav>`;
}

/* ═══════════════════════════════════════════
   NEW STORY  (story creation)
═══════════════════════════════════════════ */
function renderNewStory() {
  const step = route.params.step || "self-check";
  const d    = storyDraft;

  const brandOrBack = state.stories.length > 0
    ? `<button type="button" class="btn-back" id="back-dash">← Dashboard</button>`
    : `<div class="ob-brand"><span class="ob-brand-a">A</span><span class="ob-brand-w">Story</span></div>`;

  if (step === "self-check") {
    return `
      <section class="view view--full ob-screen" style="padding:2rem 1.75rem">
        ${brandOrBack}
        <h2 class="ob-q">Are you preserving for yourself?</h2>
        <p class="ob-q-sub">You can always preserve someone else's story later.</p>
        <div class="self-check-group">
          <button type="button" class="self-check-btn" id="btn-self-yes">
            <span class="sc-label">Yes</span>
            <span class="sc-sub">This is my own story</span>
          </button>
          <button type="button" class="self-check-btn" id="btn-self-no">
            <span class="sc-label">No</span>
            <span class="sc-sub">I'm preserving someone else's story</span>
          </button>
        </div>
      </section>`;
  }

  if (step === "relationship") {
    const otherSel = d.relationship === "Other";
    const rels = RELATIONSHIPS.filter(r => r !== "Myself");
    return `
      <section class="view view--full ob-screen" style="padding:2rem 1.75rem">
        <button type="button" class="btn-back" data-ns-back="self-check">← Back</button>
        <h2 class="ob-q">How are they related to you?</h2>
        <div class="rel-grid" id="rel-grid">
          ${rels.map((r) => `
            <button type="button" class="rel-btn ${d.relationship === r ? "is-selected" : ""}"
              data-rel="${r}" style="--rel-color:${REL_COLOR[r]||"#d4a574"}">${r}</button>`
          ).join("")}
        </div>
        <div class="rel-other-wrap ${otherSel ? "visible" : ""}" id="rel-other-wrap">
          <label class="auth-label" for="rel-other-input" style="margin-top:1rem;display:block">Describe the relationship</label>
          <input class="auth-input" id="rel-other-input" type="text"
            placeholder="e.g. Family friend, Mentor…"
            value="${h(d.relationshipOther||"")}" style="width:100%" />
        </div>
        <button type="button" class="btn btn--primary" id="rel-continue"
          style="margin-top:1.5rem" ${d.relationship ? "" : "disabled"}>Continue →</button>
      </section>`;
  }

  if (step === "name") {
    const isSelf = d.relationship === "Myself";
    const backStep = isSelf ? "self-check" : "relationship";
    return `
      <section class="view view--full ob-screen" style="padding:2rem 1.75rem">
        <button type="button" class="btn-back" data-ns-back="${backStep}">← Back</button>
        <h2 class="ob-q">${isSelf ? "What's your name?" : "And what's their name?"}</h2>
        <form class="conversational-form" id="ns-name-form">
          <div style="display:flex;gap:.75rem">
            <div style="flex:1">
              <label class="auth-label" for="ns-fn">First name</label>
              <input class="auth-input" id="ns-fn" name="firstName" type="text"
                placeholder="First" value="${h(d.name||"")}" autofocus required style="width:100%" />
            </div>
            <div style="flex:1">
              <label class="auth-label" for="ns-ln">Last name</label>
              <input class="auth-input" id="ns-ln" name="lastName" type="text"
                placeholder="Last" value="${h(d.lastName||"")}" style="width:100%" />
            </div>
          </div>
          <button type="submit" class="btn btn--primary" style="margin-top:1.5rem">Continue →</button>
        </form>
      </section>`;
  }

  if (step === "has-account") {
    return `
      <section class="view view--full ob-screen" style="padding:2rem 1.75rem">
        <button type="button" class="btn-back" data-ns-back="name">← Back</button>
        <h2 class="ob-q">Does <em>${h(d.name)}</em> have an A Story account?</h2>
        <div class="account-q-cards">
          <button type="button" class="account-q-card ${d.hasAccount==="yes"?"is-sel":""}" data-acct="yes">
            <span class="aq-icon">✓</span>
            <div>
              <p class="aq-label">Yes — connect their account</p>
              <p class="aq-sub">They'll be able to see and add to their own story</p>
            </div>
          </button>
          <button type="button" class="account-q-card ${d.hasAccount==="no"?"is-sel":""}" data-acct="no">
            <span class="aq-icon">+</span>
            <div>
              <p class="aq-label">Not yet — I'll add their memories</p>
              <p class="aq-sub">You can invite them to join later</p>
            </div>
          </button>
          <button type="button" class="account-q-card ${d.hasAccount==="cannot"?"is-sel":""}" data-acct="cannot">
            <span class="aq-icon">♡</span>
            <div>
              <p class="aq-label">This person can't use the app</p>
              <p class="aq-sub">You'll preserve their story on their behalf</p>
            </div>
          </button>
        </div>
        ${d.hasAccount === "yes" ? `
          <div class="aq-connect-form">
            <label class="auth-label" for="aq-email" style="margin-top:1.25rem;display:block">Their email address</label>
            <input class="auth-input" id="aq-email" type="email"
              placeholder="their@email.com" value="${h(d.connectedEmail||"")}" style="width:100%" />
            <p style="font-size:.8125rem;color:var(--text-muted);margin-top:.5rem">
              We'll send them a connection request.
            </p>
          </div>` : ""}
        <button type="button" class="btn btn--primary" id="aq-continue"
          style="margin-top:1.5rem" ${d.hasAccount ? "" : "disabled"}>Continue →</button>
      </section>`;
  }

  if (step === "dob") {
    return `
      <section class="view view--full ob-screen" style="padding:2rem 1.75rem">
        <button type="button" class="btn-back" data-ns-back="${isOwn({relationship:d.relationship}) ? "name" : "has-account"}">← Back</button>
        <h2 class="ob-q">When was <em>${h(d.name)}</em> born? <span style="font-size:1rem;color:var(--text-muted)">(optional)</span></h2>
        <form class="conversational-form" id="ns-dob-form">
          <div class="dob-row">
            <div class="dob-field dob-field--day">
              <label class="auth-label" for="dob-day">Day</label>
              <select class="auth-input dob-select" id="dob-day" name="day">
                <option value="">—</option>
                ${DAYS.map(n => `<option value="${n}" ${d.birthDay==n?"selected":""}>${n}</option>`).join("")}
              </select>
            </div>
            <div class="dob-field dob-field--month">
              <label class="auth-label" for="dob-month">Month</label>
              <select class="auth-input dob-select" id="dob-month" name="month">
                <option value="">Month</option>
                ${MONTHS.map(m => `<option value="${m}" ${d.birthMonth===m?"selected":""}>${m}</option>`).join("")}
              </select>
            </div>
            <div class="dob-field dob-field--year">
              <label class="auth-label" for="dob-year">Year</label>
              <input class="auth-input" id="dob-year" name="year" type="number"
                placeholder="1945" min="1900" max="2020"
                value="${h(d.birthYear||"")}" />
            </div>
          </div>
          <button type="submit" class="btn btn--primary" style="margin-top:1rem">Continue →</button>
          <button type="button" class="btn btn--ghost" id="dob-skip" style="margin-top:.5rem">Skip for now</button>
        </form>
      </section>`;
  }

  // Done
  const accent = storyAccent({ relationship: d.relationship });
  return `
    <section class="view view--full ob-screen ready-view" style="padding:2rem 1.75rem">
      <div class="ready-avatar" style="background:${accent}18;border:2px solid ${accent}60;color:${accent}">
        ${(d.name||"?")[0].toUpperCase()}
      </div>
      <h2 class="ready-title">${h(d.name)}'s archive is ready.</h2>
      <p class="ready-sub">Every word you share becomes a memory their family can hold forever.</p>
      <div class="ready-cta">
        <button type="button" class="btn btn--primary btn--large btn--block" id="btn-create-story"
          style="background:${accent}">
          Open ${h(d.name)}'s story →
        </button>
      </div>
    </section>`;
}

/* ═══════════════════════════════════════════
   DASHBOARD
═══════════════════════════════════════════ */
function renderDashboard() {
  const story   = activeStory();
  const todayStr = new Date().toDateString();
  const doneToday = (story.doneTopicsToday?.date === todayStr ? story.doneTopicsToday.eras : []);
  const allSuggest = DAILY_SUGGESTIONS[(new Date().getDay()) % DAILY_SUGGESTIONS.length];
  const suggest = allSuggest.filter(s => !doneToday.includes(s));

  if (!story) {
    return `
      <section class="view dashboard">
        <header class="dash-header">
          <div>
            <p class="home-greeting">${greeting()}, ${h(currentUser?.firstName || "")}</p>
            <h1 class="dash-title">Your stories</h1>
          </div>
        </header>
        <div class="dash-empty">
          <p class="dash-empty-text">No stories yet.</p>
          <p class="dash-empty-sub">Whose life do you want to preserve?</p>
        </div>
        <a href="#/new-story" class="add-story-card" style="margin-top:1rem">
          <span class="add-story-icon">+</span>
          <div>
            <p class="add-story-label">Add a person</p>
            <p class="add-story-sub">Start preserving someone's life</p>
          </div>
        </a>
      </section>
      ${tabs("home")}`;
  }

  const accent = storyAccent(story);
  const recent = [...story.memories]
    .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
    .slice(0, 3);

  return `
    <section class="view dashboard">
      <!-- Story selector -->
      <header class="dash-header">
        <div class="dash-story-sel">
          <div class="dash-story-avatar" style="background:${accent}18;border:1.5px solid ${accent}50;color:${accent}">
            ${(story.name||"?")[0].toUpperCase()}
          </div>
          <div>
            <p class="home-greeting">${greeting()}, ${h(currentUser?.firstName || "")}</p>
            <button class="dash-story-name-btn" id="btn-switch-story">
              ${h(story.name)} <span class="dash-story-chevron">▾</span>
            </button>
          </div>
        </div>
        <a href="#/new-story" class="icon-btn">+ Add</a>
      </header>

      <!-- Story switcher dropdown -->
      ${state.stories.length > 1 ? `
        <div class="story-switcher hidden" id="story-switcher">
          ${state.stories.map(s => `
            <button class="switcher-item ${s.id === story.id ? "is-active" : ""}" data-switch="${s.id}">
              <span class="switcher-dot" style="background:${storyAccent(s)}"></span>
              ${h(s.name)} · ${h(relLabel(s))}
            </button>`).join("")}
        </div>` : ""}

      <!-- Primary Talk CTA -->
      <div class="dash-talk-cta">
        <p class="dash-talk-label">What are we preserving today?</p>
        <a href="#/story/${story.id}/talk" class="dash-talk-btn" style="border-color:${accent}50">
          <span class="dash-mic-icon" style="background:${accent}">${ICONS.mic}</span>
          <div>
            <p class="dash-talk-name">Talk to ${h(story.name)}'s story</p>
            <p class="dash-talk-sub">Voice · Tap to begin</p>
          </div>
        </a>
      </div>

      <!-- Daily suggestions -->
      ${suggest.length > 0 ? `
      <div class="dash-suggestions">
        <p class="dash-sug-label">Today's topics</p>
        <div class="era-chips">
          ${suggest.map(s => `
            <button type="button" class="era-chip" data-era="${h(s)}" data-era-sid="${story.id}">${h(s)}</button>
          `).join("")}
        </div>
      </div>` : `
      <div class="dash-suggestions">
        <p class="dash-sug-label">Today's topics</p>
        <p class="dash-topics-done">All done for today — great session. Come back tomorrow for more.</p>
      </div>`}

      <!-- Recent memories -->
      ${recent.length ? `
        <div class="section-head">
          <h3>Recent memories</h3>
          <a href="#/story/${story.id}/tree" class="link-sm">View all</a>
        </div>
        <div class="memory-list">
          ${recent.map(m => `
            <a href="#/story/${story.id}/memory/${m.id}" class="memory-card">
              <span class="memory-card-era" style="color:${accent}">${h(m.era)}</span>
              <h4>${h(m.title)}</h4>
              <p>${h(m.excerpt)}</p>
              <div class="mc-row-bottom">
                <time>${fmtDate(m.createdAt)}</time>
              </div>
            </a>`).join("")}
        </div>` : ""}

      <!-- Keepsake book teaser -->
      <div class="keepsake-section">
        <p class="keepsake-eyebrow">A LASTING LEGACY</p>
        <h3 class="keepsake-heading">Their <em>keepsake</em></h3>
        <p class="keepsake-sub">Every answer is kept privately, in their own voice. One day, it becomes a book you can hold.</p>
        <div class="keepsake-book-wrap">
          <div class="keepsake-book">
            <div class="book-spine"></div>
            <div class="book-app-icon">A</div>
            <p class="book-serif-a">A</p>
            <p class="book-legend">A LIFE, IN THEIR WORDS</p>
          </div>
        </div>
        <div class="keepsake-stats">
          <div class="ks-stat">
            <span class="ks-num">${story.memories.length}</span>
            <span class="ks-label">MEMORIES</span>
          </div>
          <div class="ks-stat">
            <span class="ks-num">100%</span>
            <span class="ks-label">PRIVATE</span>
          </div>
          <div class="ks-stat">
            <span class="ks-num">∞</span>
            <span class="ks-label">KEPT FOREVER</span>
          </div>
        </div>
        <a href="#/legacy-book" class="btn btn--secondary btn--block ks-cta">
          Get ${h(story.name)}'s legacy book
        </a>
      </div>

      <!-- Actions -->
      <div class="dash-actions">
        <a href="#/invite" class="dash-action-btn" style="text-decoration:none">
          <span class="menu-icon">${ICONS.people}</span> Invite family members
        </a>
      </div>
    </section>
    ${tabs("home", story.id)}`;
}

/* ═══════════════════════════════════════════
   STORY VIEW  (tapped from elsewhere)
═══════════════════════════════════════════ */
function renderStoryView(storyId) {
  // Just navigate to dashboard with this story active
  const story = storyById(storyId);
  if (story) { state.activeStoryId = story.id; persist(); }
  navigate("dashboard");
  return "";
}

/* ═══════════════════════════════════════════
   CHAT / TALK
═══════════════════════════════════════════ */
function renderChat(storyId) {
  const story = storyById(storyId);
  if (!story) { navigate("dashboard"); return ""; }

  // If session is in "card" phase, render memory card instead
  if (session?.phase === "card") return renderMemoryCardUI(story);

  const firstP   = isOwn(story);
  const prompts  = getPrompts(firstP, story.name);
  const urlEra   = new URLSearchParams(location.hash.includes("?") ? location.hash.split("?")[1] : "").get("era");
  let prompt;
  if (urlEra) {
    const m = prompts.filter(p => p.era === urlEra);
    prompt  = m.length ? m[Math.floor(Math.random()*m.length)] : prompts[story.sessionsCompleted % prompts.length];
  } else {
    prompt = prompts[story.sessionsCompleted % prompts.length];
  }

  if (session && (session.storyId !== storyId || (urlEra && session.prompt.era !== urlEra))) {
    session = null;
  }
  if (!session) {
    session = { storyId, prompt, firstPerson: firstP, personName: story.name, phase: "first", answers: [], selectedEra: urlEra || null, period: "", privacy: "shared" };
  }

  const accent = storyAccent(story);
  return `
    <section class="view view--chat" id="chat-view">
      <header class="chat-header">
        <button class="chat-back" id="chat-back">←</button>
        <div class="chat-avatar-sm" style="background:${accent}18;border:1.5px solid ${accent}60;color:${accent}">
          ${(story.name||"?")[0].toUpperCase()}
        </div>
        <div class="chat-identity">
          <p class="chat-ai-name">${h(story.name)}</p>
          <p class="chat-era-label" style="color:${accent}">${h(session.prompt.era)}</p>
        </div>
        <div class="chat-header-spacer"></div>
      </header>
      <div class="chat-messages" id="chat-messages"></div>
      <footer class="chat-footer">
        <div class="chat-input-wrap">
          <button class="btn-mic-chat" id="btn-mic" title="Demo voice">${ICONS.mic}</button>
          <textarea class="chat-textarea" id="chat-textarea"
            placeholder="Speak or type…" rows="1" maxlength="2000"></textarea>
          <button class="btn-send" id="btn-send" disabled style="background:${accent}">↑</button>
        </div>
      </footer>
    </section>`;
}

function renderMemoryCardUI(story) {
  const draft  = session.draft;
  const accent = storyAccent(story);
  return `
    <section class="view memory-card-screen">
      <header class="chat-header">
        <button class="chat-back" id="mc-back">←</button>
        <div class="chat-identity">
          <p class="chat-ai-name">Memory Card</p>
          <p class="chat-era-label" style="color:${accent}">${h(draft.era)}</p>
        </div>
        <div class="chat-header-spacer"></div>
      </header>

      <div class="mc-content">
        <div class="mc-card-preview">
          <span class="memory-card-era" style="color:${accent}">${h(draft.era)}</span>
          ${session.period ? `<p class="mc-period">${h(session.period)}</p>` : ""}
          <h3 class="mc-title" id="mc-title-el">${h(draft.title)}</h3>
          <p class="mc-excerpt">${h(draft.excerpt)}</p>
        </div>

        <div class="mc-rating-section">
          <p class="mc-rating-label">How satisfied are you with this memory card?</p>
          <p class="mc-rating-sub">The more details and feelings captured, the more your family will treasure it.</p>
          <div class="star-rating" id="star-rating" role="group" aria-label="Rate this memory">
            ${[1,2,3,4,5].map(n => `
              <button type="button" class="star-btn ${(session.rating||0) >= n ? "is-active":""}"
                data-star="${n}" aria-label="${n} star${n>1?"s":""}">${(session.rating||0) >= n ? "★" : "☆"}</button>
            `).join("")}
          </div>
          <textarea class="mc-note-input" id="mc-note" rows="2"
            placeholder="Why? (optional)">${h(session.ratingNote||"")}</textarea>
        </div>

        <div class="mc-media-section">
          <p class="mc-rating-label">Photos</p>
          <div class="photo-preview-grid" id="mc-photo-grid">
            ${(session.photos||[]).map((src,i)=>`
              <div class="photo-thumb">
                <img src="${src}" alt="Memory photo" />
                <button class="photo-remove" data-photo-idx="${i}" aria-label="Remove photo">×</button>
              </div>`).join("")}
          </div>
          <label class="mc-upload-btn" for="mc-photo-input">
            <span class="menu-icon">${ICONS.camera}</span> Add photos
            <input type="file" id="mc-photo-input" accept="image/*" multiple style="display:none" />
          </label>
        </div>
      </div>

      <div class="mc-privacy-row" id="mc-privacy-row">
        <div class="mc-privacy-info">
          <p class="mc-privacy-title" id="mc-privacy-title">Shared with family</p>
          <p class="mc-privacy-sub" id="mc-privacy-desc">Family members with your invite link can view and contribute — listed as co-authors.</p>
        </div>
        <button type="button" class="mc-privacy-btn is-shared" id="btn-mc-privacy" aria-label="Toggle privacy">
          <span class="mc-privacy-btn-label" id="mc-privacy-label">Shared</span>
        </button>
      </div>

      <div class="mc-footer">
        <button type="button" class="btn btn--primary btn--block" id="btn-save-card"
          style="background:${accent}">Save to archive →</button>
      </div>
    </section>`;
}

/* ─── Chat DOM helpers ─── */
const chatEl    = () => document.getElementById("chat-messages");
const scrollDn  = () => { const e = chatEl(); if (e) e.scrollTop = e.scrollHeight; };

function appendAI(text) {
  const el = chatEl(); if (!el) return;
  const d = document.createElement("div");
  d.className = "msg msg--ai";
  d.innerHTML = `<div class="msg-avatar">A</div>
    <div class="msg-bubble">${h(text).replace(/\n/g,"<br>")}</div>`;
  el.appendChild(d); scrollDn();
}

function appendUser(text) {
  const el = chatEl(); if (!el) return;
  const d = document.createElement("div");
  d.className = "msg msg--user";
  d.innerHTML = `<div class="msg-bubble">${h(text).replace(/\n/g,"<br>")}</div>`;
  el.appendChild(d); scrollDn();
}

function showThinking() {
  const el = chatEl(); if (!el) return;
  const d  = document.createElement("div");
  d.className = "msg msg--ai"; d.id = "thinking";
  d.innerHTML = `<div class="msg-avatar">A</div>
    <div class="msg-bubble thinking-dots">
      <span class="thinking-dot"></span>
      <span class="thinking-dot"></span>
      <span class="thinking-dot"></span>
    </div>`;
  el.appendChild(d); scrollDn();
}

function hideThinking() { document.getElementById("thinking")?.remove(); }

/* ═══════════════════════════════════════════
   MEMORY TREE  (per-story)
═══════════════════════════════════════════ */
function renderTree(storyId) {
  const story = storyById(storyId);
  if (!story) { navigate("dashboard"); return ""; }

  const accent  = storyAccent(story);
  const grouped = ERAS.map(era => ({
    era, items: story.memories.filter(m => m.era === era),
  })).filter(g => g.items.length);
  const other = story.memories.filter(m => !ERAS.includes(m.era));
  if (other.length) grouped.push({ era: "Life", items: other });

  const allMemories = story.memories;

  return `
    <section class="view tree-view">
      <header style="margin-bottom:1rem">
        <p class="eyebrow" style="color:${accent}">${h(story.name)}'s life</p>
        <h2 class="view-title">Archive</h2>
      </header>

      ${allMemories.length ? `
      <div class="tree-search-wrap">
        <span class="tree-search-icon">${ICONS.search}</span>
        <input class="tree-search-input" id="tree-search" type="search"
          placeholder="Search memories…" autocomplete="off" />
      </div>` : ""}

      <div id="tree-content">
      ${grouped.length ? `
        <ol class="timeline">
          ${grouped.map(g => `
            <li class="timeline-era" data-era-group="${h(g.era)}">
              <h3 style="color:${accent}">${h(g.era)}</h3>
              <ul>
                ${g.items.map(m => `
                  <li data-mem-search="${h((m.title + " " + m.body + " " + (m.period||"")).toLowerCase())}">
                    <a href="#/story/${storyId}/memory/${m.id}" class="timeline-item">
                      <div>
                        <strong>${h(m.title)}</strong>
                        ${m.period ? `<span class="tl-period">${h(m.period)}</span>` : ""}
                      </div>
                      <span>${fmtDate(m.createdAt)}</span>
                    </a>
                  </li>`).join("")}
              </ul>
            </li>`).join("")}
        </ol>` : `
        <div class="empty-state">
          <p>No memories yet.<br>Start talking to grow ${h(story.name)}'s tree.</p>
          <a href="#/story/${storyId}/talk" class="btn btn--primary"
            style="margin-top:1.5rem;display:inline-flex;background:${accent}">Start talking</a>
        </div>`}
      </div>
      <p class="tree-no-results" id="tree-no-results" style="display:none">No memories match your search.</p>

      <div class="archive-upload-section">
        <p class="archive-upload-label">Photos &amp; videos</p>
        <label class="archive-upload-btn" for="archive-photo-input">
          <span class="archive-upload-icon">${ICONS.camera}</span>
          <div>
            <p class="archive-upload-title">Upload from your device</p>
            <p class="archive-upload-sub">Add photos or videos to ${h(story.name)}'s archive</p>
          </div>
          <input type="file" id="archive-photo-input" accept="image/*,video/*" multiple style="display:none" />
        </label>
      </div>
    </section>
    ${tabs("tree", storyId)}`;
}

/* ═══════════════════════════════════════════
   MEMORY DETAIL
═══════════════════════════════════════════ */
function renderMemory(storyId, memId) {
  const story  = storyById(storyId);
  const memory = story?.memories.find(m => m.id === memId);
  if (!story || !memory) { navigate("dashboard"); return ""; }

  const accent = storyAccent(story);
  return `
    <section class="view memory-detail">
      <header class="detail-header">
        <a href="#/story/${storyId}/tree" class="btn-text">← ${h(story.name)}</a>
        <button type="button" class="btn-text btn-text--danger"
          data-del-mem="${memory.id}" data-del-story="${storyId}">Delete</button>
      </header>
      <span class="memory-card-era" style="color:${accent}">${h(memory.era)}</span>
      <h1 class="detail-title">${h(memory.title)}</h1>
      ${memory.period ? `<p class="detail-period">${h(memory.period)}</p>` : ""}
      <time class="detail-date">${fmtDate(memory.createdAt)}</time>

      <div class="detail-speak-row">
        <button type="button" class="detail-speak-btn" id="btn-detail-speak">
          <span class="detail-speak-icon">${ICONS.mic}</span>
          <span>Speak to update</span>
        </button>
        <button type="button" class="detail-ai-btn" id="btn-ai-refine">
          <span>✦ Ask AI to refine</span>
        </button>
      </div>

      <form class="form-stack" id="mem-form">
        <label class="field"><span>Story</span>
          <textarea name="body" rows="10" required>${h(memory.body)}</textarea>
        </label>
        <div class="field">
          <span>Photos</span>
          <div class="photo-preview-grid" id="detail-photo-grid">
            ${(memory.photos||[]).map((src,i)=>`
              <div class="photo-thumb">
                <img src="${src}" alt="Memory photo" />
                <button type="button" class="photo-remove" data-detail-photo="${i}" aria-label="Remove">×</button>
              </div>`).join("")}
          </div>
          <label class="mc-upload-btn" for="detail-photo-input" style="margin-top:.5rem">
            <span class="menu-icon">${ICONS.camera}</span> Add photos
            <input type="file" id="detail-photo-input" accept="image/*" multiple style="display:none" />
          </label>
        </div>
        <button type="submit" class="btn btn--primary" style="background:${accent}">Save changes</button>
      </form>
    </section>
    ${tabs("tree", storyId)}`;
}

/* ═══════════════════════════════════════════
   FAQ DATA
═══════════════════════════════════════════ */
const FAQS = [
  {
    q: "How does the AI interview work?",
    a: "A Story's AI acts like a warm, patient interviewer. It focuses on one topic at a time — childhood, family, career, or any chapter of life — then listens and asks a thoughtful follow-up. You can speak or type your answer. Each conversation becomes a memory card that builds your family's archive.",
  },
  {
    q: "Is my family's data private and secure?",
    a: "Yes. Your stories are stored privately on your device and never shared publicly. A Story does not sell your data or use it for advertising. Your family's archive is only visible to people you personally invite.",
  },
  {
    q: "Can I save more than one person's story?",
    a: "Absolutely. You can create as many story profiles as you like — one for Grandma, one for Dad, one for yourself. Each profile has its own memories, timeline, and archive, all accessible from your Home dashboard.",
  },
  {
    q: "How do I invite family members to contribute?",
    a: "Tap 'Invite Family Members' on the Home screen or go to Menu → Invite Family Members. You'll get a shareable link you can send via text, email, or WhatsApp. Invited members can view memories, add their own, and upload photos.",
  },
  {
    q: "What happens to my memories if I stop using the app?",
    a: "Your memories are stored locally on your device, so they stay with you. We recommend ordering a legacy book to create a permanent hardcover keepsake. Cloud backup and export features are coming soon.",
  },
];

/* ═══════════════════════════════════════════
   HELP & FEEDBACK  (WhatsApp-style)
═══════════════════════════════════════════ */
function renderHelp() {
  return `
    <section class="view help-view">
      <header class="chat-header">
        <a href="#/menu" class="chat-back">←</a>
        <div class="chat-identity">
          <p class="chat-ai-name">Help &amp; Feedback</p>
        </div>
        <div class="chat-header-spacer"></div>
      </header>

      <div class="help-search-wrap">
        <span class="help-search-icon">${ICONS.search}</span>
        <input class="help-search-input" type="search" placeholder="Search help topics…"
          readonly onclick="window.showComingSoon('Search')" />
      </div>

      <div class="help-section">
        <p class="help-section-label">Help Center</p>
        <a href="#/faq" class="help-item">
          <div class="help-item-icon">${ICONS.faq}</div>
          <div class="help-item-body">
            <p class="help-item-title">Frequently Asked Questions</p>
            <p class="help-item-sub">Quick answers to common questions</p>
          </div>
          <span class="help-item-chevron">${ICONS.chevron}</span>
        </a>
        <a href="#/contact" class="help-item">
          <div class="help-item-icon">${ICONS.mail}</div>
          <div class="help-item-body">
            <p class="help-item-title">Contact Us</p>
            <p class="help-item-sub">Get in touch with our support team</p>
          </div>
          <span class="help-item-chevron">${ICONS.chevron}</span>
        </a>
      </div>

      <div class="help-section">
        <p class="help-section-label">Feedback</p>
        <a href="#/feedback?type=bug" class="help-item">
          <div class="help-item-icon">${ICONS.bug}</div>
          <div class="help-item-body">
            <p class="help-item-title">Report an issue</p>
            <p class="help-item-sub">Something not working? Let us know</p>
          </div>
          <span class="help-item-chevron">${ICONS.chevron}</span>
        </a>
        <a href="#/feedback" class="help-item">
          <div class="help-item-icon">${ICONS.idea}</div>
          <div class="help-item-body">
            <p class="help-item-title">Send a suggestion</p>
            <p class="help-item-sub">Share ideas to make A Story better</p>
          </div>
          <span class="help-item-chevron">${ICONS.chevron}</span>
        </a>
      </div>

      <div class="help-section">
        <p class="help-section-label">Legal</p>
        <a href="#/privacy" class="help-item">
          <div class="help-item-icon">${ICONS.lock}</div>
          <div class="help-item-body">
            <p class="help-item-title">Terms &amp; Privacy Policy</p>
            <p class="help-item-sub">How we handle your data</p>
          </div>
          <span class="help-item-chevron">${ICONS.chevron}</span>
        </a>
      </div>

      <p class="help-version">A Story · Version 1.0 · Demo build</p>
    </section>
    ${tabs("menu")}`;
}

/* ─── FAQ ─── */
function renderFAQ() {
  return `
    <section class="view faq-view">
      <header class="chat-header">
        <a href="#/menu" class="chat-back">←</a>
        <div class="chat-identity">
          <p class="chat-ai-name">FAQ</p>
          <p class="chat-era-label">Top questions</p>
        </div>
        <div class="chat-header-spacer"></div>
      </header>

      <div class="faq-list">
        ${FAQS.map((faq, i) => `
          <div class="faq-item">
            <button class="faq-q" data-faq="${i}" aria-expanded="false">
              <span>${h(faq.q)}</span>
              <span class="faq-chevron" id="faq-ch-${i}">›</span>
            </button>
            <div class="faq-a" id="faq-a-${i}" hidden>
              <p>${h(faq.a)}</p>
            </div>
          </div>`).join("")}
      </div>

      <div class="faq-footer">
        <p>Still have questions?</p>
        <a href="#/contact" class="btn btn--secondary" style="display:inline-flex;margin-top:.875rem">
          Contact us →
        </a>
      </div>
    </section>
    ${tabs("menu")}`;
}

/* ─── Send Feedback ─── */
function renderFeedback() {
  const urlType = location.hash.includes("bug") ? "bug" : "suggestion";
  return `
    <section class="view feedback-view">
      <header class="chat-header">
        <a href="#/menu" class="chat-back">←</a>
        <div class="chat-identity">
          <p class="chat-ai-name">Send Feedback</p>
        </div>
        <div class="chat-header-spacer"></div>
      </header>

      <div class="feedback-content">
        <p class="feedback-intro">We read every message. Your feedback makes A Story better for every family.</p>

        <p class="feedback-type-label">Type</p>
        <div class="feedback-type-row">
          <button class="fb-chip ${urlType === "bug" ? "is-active" : ""}" data-fb-type="bug">Bug report</button>
          <button class="fb-chip ${urlType === "suggestion" ? "is-active" : ""}" data-fb-type="suggestion">Suggestion</button>
          <button class="fb-chip" data-fb-type="other">Other</button>
        </div>

        <form class="form-stack" id="feedback-form" style="margin-top:1.5rem">
          <label class="field">
            <span>Your message</span>
            <textarea name="message" rows="6"
              placeholder="Describe your issue or share an idea…" required></textarea>
          </label>
          <label class="field">
            <span>Email <span style="font-weight:400;text-transform:none;letter-spacing:0">(optional — so we can follow up)</span></span>
            <input type="email" name="email" placeholder="you@example.com" autocomplete="email" />
          </label>
          <button type="submit" class="btn btn--primary btn--large btn--block">
            Send feedback →
          </button>
        </form>
      </div>
    </section>
    ${tabs("menu")}`;
}

/* ─── Contact Us ─── */
function renderContact() {
  return `
    <section class="view contact-view">
      <header class="chat-header">
        <a href="#/menu" class="chat-back">←</a>
        <div class="chat-identity">
          <p class="chat-ai-name">Contact Us</p>
        </div>
        <div class="chat-header-spacer"></div>
      </header>

      <div class="contact-content">
        <div class="contact-info-card">
          <p class="contact-info-label">Email us</p>
          <a href="mailto:support@astoryapp.com" class="contact-email">support@astoryapp.com</a>
          <p class="contact-info-note">We usually respond within 24 hours.</p>
        </div>

        <div class="contact-divider"><span>or send a message</span></div>

        <form class="form-stack" id="contact-form">
          <label class="field">
            <span>Subject</span>
            <input type="text" name="subject" placeholder="What's this about?" />
          </label>
          <label class="field">
            <span>Message</span>
            <textarea name="message" rows="5" placeholder="How can we help?" required></textarea>
          </label>
          <label class="field">
            <span>Your email</span>
            <input type="email" name="email" placeholder="So we can reply" autocomplete="email" required />
          </label>
          <button type="submit" class="btn btn--primary btn--large btn--block">
            Send message →
          </button>
        </form>
      </div>
    </section>
    ${tabs("menu")}`;
}

/* ─── Terms & Privacy ─── */
function renderPrivacy() {
  return `
    <section class="view privacy-view">
      <header class="chat-header">
        <a href="#/menu" class="chat-back" id="privacy-back">←</a>
        <div class="chat-identity">
          <p class="chat-ai-name">Terms &amp; Privacy</p>
        </div>
        <div class="chat-header-spacer"></div>
      </header>

      <div class="privacy-content">
        <div class="privacy-hero">
          <p class="privacy-icon">${ICONS.lock}</p>
          <h2 class="privacy-title">Terms &amp; Privacy Policy</h2>
          <p class="privacy-coming">Our legal document is being finalized.<br>We'll notify you as soon as it's ready.</p>
        </div>

        <div class="privacy-promise-card">
          <p class="promise-heading">Our commitment to you</p>
          <ul class="promise-list">
            <li>
              <span class="promise-check">✓</span>
              Your stories are stored privately on your device
            </li>
            <li>
              <span class="promise-check">✓</span>
              We never sell your data or use it for advertising
            </li>
            <li>
              <span class="promise-check">✓</span>
              Your family archive is visible only to people you invite
            </li>
            <li>
              <span class="promise-check">✓</span>
              You can delete your data at any time, permanently
            </li>
            <li>
              <span class="promise-check">✓</span>
              We will never share your stories without your consent
            </li>
          </ul>
        </div>

        <p class="privacy-notify-note">
          Want to be notified when the full document is available?
        </p>
        <button class="btn btn--secondary btn--block" onclick="window.showComingSoon('Privacy policy notification')">
          Notify me →
        </button>
      </div>
    </section>
    ${tabs("menu")}`;
}

/* ═══════════════════════════════════════════
   LEGACY BOOK ORDER
═══════════════════════════════════════════ */
function renderLegacyBook() {
  const story  = activeStory();
  const accent = story ? storyAccent(story) : "#d4a574";
  const name   = story ? h(story.name) : "your loved one";

  return `
    <section class="view lb-view">
      <header class="chat-header">
        <a href="#/menu" class="chat-back">←</a>
        <div class="chat-identity">
          <p class="chat-ai-name">Legacy Book</p>
          <p class="chat-era-label" style="color:${accent}">${name}</p>
        </div>
        <div class="chat-header-spacer"></div>
      </header>

      <div class="lb-hero">
        <div class="keepsake-book lb-book-sm">
          <div class="book-spine"></div>
          <div class="book-app-icon" style="font-size:1rem;width:32px;height:32px;border-radius:7px">A</div>
          <p class="book-serif-a" style="font-size:1.1rem">A</p>
          <p class="book-legend">A LIFE, IN THEIR WORDS</p>
        </div>
        <div class="lb-hero-text">
          <h2 class="lb-title">${name}'s legacy book</h2>
          <p class="lb-sub">A beautifully printed hardcover memoir — every memory, in their voice, yours to hold forever.</p>
        </div>
      </div>

      <form class="form-stack" id="lb-form">
        <label class="field">
          <span>Recipient name</span>
          <input type="text" name="fullName" placeholder="Full name" autocomplete="name" />
        </label>
        <label class="field">
          <span>Address line 1</span>
          <input type="text" name="addr1" placeholder="Street address" autocomplete="address-line1" />
        </label>
        <label class="field">
          <span>Address line 2 <span style="font-weight:400;text-transform:none;letter-spacing:0">(optional)</span></span>
          <input type="text" name="addr2" placeholder="Apt, suite, floor…" autocomplete="address-line2" />
        </label>
        <div class="lb-field-row">
          <label class="field" style="flex:2">
            <span>City</span>
            <input type="text" name="city" placeholder="City" autocomplete="address-level2" />
          </label>
          <label class="field" style="flex:1">
            <span>State</span>
            <input type="text" name="state" placeholder="State" autocomplete="address-level1" />
          </label>
        </div>
        <div class="lb-field-row">
          <label class="field" style="flex:1">
            <span>ZIP / Postal</span>
            <input type="text" name="zip" placeholder="ZIP" autocomplete="postal-code" inputmode="numeric" />
          </label>
          <label class="field" style="flex:1.5">
            <span>Country</span>
            <input type="text" name="country" placeholder="Country" autocomplete="country-name" value="United States" />
          </label>
        </div>
        <button type="submit" class="btn btn--primary btn--large btn--block" style="background:${accent}">
          Place order →
        </button>
      </form>
    </section>
    ${tabs("home")}`;
}

/* ═══════════════════════════════════════════
   FAMILY INVITE
═══════════════════════════════════════════ */
function renderInvite() {
  const story  = activeStory();
  const accent = story ? storyAccent(story) : "#d4a574";
  const storyName = story ? h(story.name) : "the story";
  // Deterministic fake link based on story id
  const code  = (story?.id || "family").replace(/-/g, "").slice(0, 8).toUpperCase();
  const link  = `https://astory.app/join/${code}`;

  return `
    <section class="view invite-view">
      <header class="chat-header">
        <a href="#/" class="chat-back">←</a>
        <div class="chat-identity">
          <p class="chat-ai-name">Invite Family</p>
          <p class="chat-era-label" style="color:${accent}">${storyName}</p>
        </div>
        <div class="chat-header-spacer"></div>
      </header>

      <div class="invite-content">
        <h2 class="invite-title">Invite family to contribute</h2>
        <p class="invite-sub">Family members can view memories, add their own, and upload photos.</p>

        <div class="invite-link-card">
          <p class="invite-link-label">Shareable link</p>
          <div class="invite-link-row">
            <code class="invite-link-text" id="invite-link-text">${link}</code>
            <button class="invite-copy-btn" id="btn-copy-link" type="button">Copy</button>
          </div>
        </div>

        <div class="invite-divider"><span>or send directly</span></div>

        <form class="form-stack" id="invite-form">
          <label class="field">
            <span>Email address</span>
            <input type="email" name="email" placeholder="family@example.com"
              autocomplete="email" inputmode="email" />
          </label>
          <label class="field">
            <span>Phone number <span style="font-weight:400;text-transform:none;letter-spacing:0">(optional)</span></span>
            <input type="tel" name="phone" placeholder="+1 (555) 000-0000"
              autocomplete="tel" inputmode="tel" />
          </label>
          <button type="submit" class="btn btn--primary btn--large btn--block" style="background:${accent}">
            Send invite link
          </button>
        </form>

        <p class="invite-note">
          They'll be able to see ${storyName}'s memories and add their own perspective.
        </p>
      </div>
    </section>
    ${tabs("home")}`;
}

/* ═══════════════════════════════════════════
   COMING SOON MODAL (global so inline onclick works)
═══════════════════════════════════════════ */
window.showPrivacy = function() {
  document.getElementById("cs-modal")?.remove();
  const el = document.createElement("div");
  el.id = "cs-modal";
  el.className = "cs-overlay";
  el.innerHTML = `
    <div class="cs-modal">
      <p class="cs-icon">${ICONS.lock}</p>
      <h3 class="cs-title">Terms &amp; Privacy Policy</h3>
      <p class="cs-body">Our legal document is being finalized. We commit to never selling your data, sharing your stories without consent, or using your information for advertising.</p>
      <button class="btn btn--primary" onclick="document.getElementById('cs-modal').remove()">Got it</button>
    </div>`;
  document.body.appendChild(el);
  el.onclick = (e) => { if (e.target === el) el.remove(); };
};

function showNotificationPrompt() {
  if ("Notification" in window && Notification.permission === "granted") return;
  document.getElementById("cs-modal")?.remove();
  const el = document.createElement("div");
  el.id = "cs-modal"; el.className = "cs-overlay";
  el.innerHTML = `
    <div class="cs-modal" role="dialog" aria-modal="true">
      <p class="cs-icon">${ICONS.bell}</p>
      <h3 class="cs-title">Stay connected to their story</h3>
      <p class="cs-body">Get a gentle daily reminder so their story keeps growing — one memory at a time.</p>
      <button class="btn btn--primary" id="btn-notif-yes" style="margin-bottom:.5rem">Turn on reminders</button>
      <button class="btn btn--ghost" onclick="document.getElementById('cs-modal').remove()">Maybe later</button>
    </div>`;
  document.body.appendChild(el);
  el.onclick = (e) => { if (e.target === el) el.remove(); };
  document.getElementById("btn-notif-yes")?.addEventListener("click", () => {
    el.remove();
    if ("Notification" in window) {
      Notification.requestPermission();
    }
  });
}

window.showComingSoon = function(feature = "This feature") {
  document.getElementById("cs-modal")?.remove();
  const el = document.createElement("div");
  el.id = "cs-modal";
  el.className = "cs-overlay";
  el.innerHTML = `
    <div class="cs-modal" role="dialog" aria-modal="true">
      <p class="cs-icon">${ICONS.star}</p>
      <h3 class="cs-title">${feature}</h3>
      <p class="cs-body">We're working on something beautiful. This will be ready soon.</p>
      <button class="btn btn--primary" onclick="document.getElementById('cs-modal').remove()">Got it</button>
    </div>`;
  document.body.appendChild(el);
  el.onclick = (e) => { if (e.target === el) el.remove(); };
};

/* ═══════════════════════════════════════════
   PRICING
═══════════════════════════════════════════ */
function renderPricing() {
  return `
    <section class="view pricing-view">
      <header class="pricing-header-bar">
        <a href="#/menu" class="chat-back">←</a>
        <div class="pricing-header-title">
          <h2 class="pricing-brand">A Story</h2>
          <p class="pricing-brand-sub">Keep their voice forever</p>
        </div>
        <div style="width:44px"></div>
      </header>

      <!-- Billing toggle -->
      <div class="pricing-toggle-wrap">
        <div class="plan-toggle">
          <button class="toggle-pill is-active" id="toggle-monthly">Monthly</button>
          <button class="toggle-pill" id="toggle-annual">Annual</button>
        </div>
        <span class="pricing-save-badge" id="toggle-save-badge">2 months free</span>
      </div>

      <!-- Free -->
      <div class="pricing-card tier-free">
        <div class="pricing-card-row">
          <div>
            <h3 class="plan-name">Free</h3>
            <p class="plan-desc">Try A Story, no commitment</p>
          </div>
          <div class="plan-price-wrap">
            <span class="plan-price">$0</span>
            <span class="plan-period">forever</span>
          </div>
        </div>
        <ul class="plan-features">
          <li class="feat-yes"><span class="feat-icon">✓</span> 3 AI conversations per day</li>
          <li class="feat-yes"><span class="feat-icon">✓</span> Up to 10 memory cards</li>
          <li class="feat-yes"><span class="feat-icon">✓</span> 1 storyteller profile</li>
          <li class="feat-no"><span class="feat-icon">—</span> Photo uploads</li>
          <li class="feat-no"><span class="feat-icon">—</span> Data export</li>
          <li class="feat-no"><span class="feat-icon">—</span> Ad-free experience</li>
        </ul>
        <button class="btn plan-btn tier-free-btn btn--large btn--block"
          onclick="navigate('dashboard')">Continue free →</button>
        <p class="plan-footnote">Hardcover book +$20 premium · Export $9.99</p>
      </div>

      <!-- Individual -->
      <div class="pricing-card tier-pro">
        <div class="pricing-card-row">
          <div>
            <h3 class="plan-name">Individual</h3>
            <p class="plan-desc">One storyteller, full access</p>
          </div>
          <div class="plan-price-wrap">
            <span class="plan-price" id="pro-price">$8</span>
            <span class="plan-period" id="pro-period">per month</span>
          </div>
        </div>
        <ul class="plan-features">
          <li class="feat-yes"><span class="feat-icon">✓</span> Unlimited AI conversations</li>
          <li class="feat-yes"><span class="feat-icon">✓</span> 50 memory cards / month</li>
          <li class="feat-yes"><span class="feat-icon">✓</span> Photo uploads (unlimited)</li>
          <li class="feat-yes"><span class="feat-icon">✓</span> Export anytime, free</li>
          <li class="feat-yes"><span class="feat-icon">✓</span> Ad-free experience</li>
        </ul>
        <div class="plan-addon">
          <div>
            <p class="addon-name">Add hardcover book</p>
            <p class="addon-desc">Order anytime · yours to keep</p>
          </div>
          <span class="addon-price">+ $69</span>
        </div>
        <button class="btn plan-btn tier-pro-btn btn--large btn--block"
          onclick="window.showComingSoon('Individual plan')">Start 7 days free →</button>
        <p class="plan-footnote">No charge for 7 days · cancel anytime</p>
      </div>

      <!-- Family (featured) -->
      <div class="pricing-card pricing-card--featured">
        <div class="pricing-badge"><span class="badge-dot"></span> Most families choose this</div>
        <div class="pricing-card-row">
          <div>
            <h3 class="plan-name">Family</h3>
            <p class="plan-desc">Up to 3 storytellers</p>
          </div>
          <div class="plan-price-wrap">
            <span class="plan-price" id="fam-price">$13</span>
            <span class="plan-period" id="fam-period">per month</span>
          </div>
        </div>
        <ul class="plan-features">
          <li class="feat-yes"><span class="feat-icon">✓</span> Everything in Individual</li>
          <li class="feat-yes"><span class="feat-icon">✓</span> Up to 3 storyteller profiles</li>
          <li class="feat-yes"><span class="feat-icon">✓</span> Shared family archive</li>
          <li class="feat-yes"><span class="feat-icon">✓</span> Invite family to view</li>
          <li class="feat-yes"><span class="feat-icon">✓</span> 100 memory cards / month</li>
        </ul>
        <div class="plan-addon">
          <div>
            <p class="addon-name">Add hardcover book</p>
            <p class="addon-desc">Order anytime · yours to keep</p>
          </div>
          <span class="addon-price">+ $69</span>
        </div>
        <button class="btn plan-btn plan-btn--featured btn--large btn--block"
          onclick="window.showComingSoon('Family plan')">Start 7 days free →</button>
        <p class="plan-footnote">No charge for 7 days · cancel anytime</p>
      </div>

      <!-- Family Plus -->
      <div class="pricing-card tier-plus">
        <div class="pricing-card-row">
          <div>
            <h3 class="plan-name">Family Plus</h3>
            <p class="plan-desc">Unlimited storytellers</p>
          </div>
          <div class="plan-price-wrap">
            <span class="plan-price" id="plus-price">$20</span>
            <span class="plan-period" id="plus-period">per month</span>
          </div>
        </div>
        <ul class="plan-features">
          <li class="feat-yes"><span class="feat-icon">✓</span> Everything in Family</li>
          <li class="feat-yes"><span class="feat-icon">✓</span> Unlimited storyteller profiles</li>
          <li class="feat-yes"><span class="feat-icon">✓</span> Unlimited memory cards</li>
          <li class="feat-yes"><span class="feat-icon">✓</span> 1 complimentary book / year</li>
          <li class="feat-yes"><span class="feat-icon">✓</span> Priority support</li>
        </ul>
        <button class="btn plan-btn tier-plus-btn btn--large btn--block"
          onclick="window.showComingSoon('Family Plus plan')">Start 7 days free →</button>
        <p class="plan-footnote">No charge for 7 days · cancel anytime</p>
      </div>

      <!-- Early access / Founding Family -->
      <div class="founding-section">
        <p class="founding-eyebrow">Early Access · 47 of 100 spots remaining</p>
        <p class="founding-text">Want lifetime access, no subscription?<br>
          <a href="#" onclick="event.preventDefault();window.showComingSoon('Founding family plan')">
            Claim a founding family spot — $249 one-time →
          </a>
        </p>
      </div>

    </section>
    ${tabs("menu")}`;
}

/* ═══════════════════════════════════════════
   MENU
═══════════════════════════════════════════ */
/* ═══════════════════════════════════════════
   SETTINGS
═══════════════════════════════════════════ */
function renderPhotoMemory() {
  const draft = photoMemoryDraft;
  if (!draft) { navigate("dashboard"); return ""; }
  const story  = storyById(draft.storyId);
  const accent = story ? storyAccent(story) : "#d4a574";
  return `
    <section class="view photo-memory-view">
      <header class="detail-header">
        <a href="#/story/${h(draft.storyId)}/tree" class="btn-text">← Archive</a>
      </header>
      <h2 class="pm-title">Create a photo memory</h2>
      <p class="pm-sub">Give these photos a story so they're never just pixels.</p>

      <div class="pm-photos">
        ${draft.photos.map((src, i) => `
          <div class="pm-photo-thumb">
            <img src="${src}" alt="Photo ${i+1}" />
          </div>`).join("")}
      </div>

      <form class="form-stack" id="pm-form">
        <label class="field"><span>What's the story behind these?</span>
          <textarea name="body" rows="6" placeholder="A memory, a moment, a feeling — anything you want to preserve…" required></textarea>
        </label>
        <label class="field"><span>Chapter</span>
          <select name="era">
            ${ERAS.map(e => `<option value="${h(e)}">${h(e)}</option>`).join("")}
          </select>
        </label>
        <button type="submit" class="btn btn--primary btn--block" style="background:${accent}">Save to archive →</button>
      </form>
    </section>
    ${tabs("tree", draft.storyId)}`;
}

function renderSettings() {
  const user = currentUser;
  return `
    <section class="view settings-view">
      <header class="chat-header">
        <a href="#/menu" class="chat-back">←</a>
        <div class="chat-identity">
          <p class="chat-ai-name">Settings</p>
        </div>
        <div class="chat-header-spacer"></div>
      </header>

      <div class="settings-section">
        <p class="settings-section-title">Account</p>
        <div class="settings-item" id="settings-name-display">
          <div class="settings-item-icon">${ICONS.user}</div>
          <div class="settings-item-body">
            <p class="settings-item-label">Name</p>
            <p class="settings-item-value" id="settings-name-value">${h(user?.firstName||"")} ${h(user?.lastName||"")}</p>
          </div>
          <button class="settings-item-action" id="btn-edit-name">Edit</button>
        </div>
        <div class="settings-item settings-name-edit-row" id="settings-name-edit" style="display:none">
          <div class="settings-item-icon">${ICONS.user}</div>
          <div class="settings-item-body" style="gap:.5rem;display:flex;flex-direction:column">
            <input class="auth-input" id="edit-first-name" placeholder="First name"
              value="${h(user?.firstName||"")}" style="margin:0" />
            <input class="auth-input" id="edit-last-name" placeholder="Last name"
              value="${h(user?.lastName||"")}" style="margin:0" />
            <div style="display:flex;gap:.5rem">
              <button class="btn btn--primary" id="btn-save-name" style="flex:1;padding:.5rem">Save</button>
              <button class="btn btn--ghost" id="btn-cancel-name" style="flex:1;padding:.5rem">Cancel</button>
            </div>
          </div>
        </div>
        <div class="settings-item">
          <div class="settings-item-icon">${ICONS.mail}</div>
          <div class="settings-item-body">
            <p class="settings-item-label">Email address</p>
            <p class="settings-item-value">${h(user?.email||"")}</p>
          </div>
        </div>
        <div class="settings-item">
          <div class="settings-item-icon">${ICONS.lock}</div>
          <div class="settings-item-body">
            <p class="settings-item-label">Password</p>
            <p class="settings-item-value settings-item-value--muted">••••••••</p>
          </div>
          <button class="settings-item-action" onclick="window.showComingSoon('Change password')">Change</button>
        </div>
      </div>

      <div class="settings-section">
        <p class="settings-section-title">Profiles</p>
        ${state.stories.map(s => {
          const dobDisplay = fmtDob(s.birthDay, s.birthMonth, s.birthYear);
          const accent = storyAccent(s);
          return `
          <div class="settings-item" id="dob-display-${h(s.id)}">
            <div class="settings-item-icon" style="color:${accent}">${ICONS.user}</div>
            <div class="settings-item-body">
              <p class="settings-item-label">${h(s.name)}${s.lastName ? " " + h(s.lastName) : ""}</p>
              <p class="settings-item-value">${dobDisplay || "Birthday not set"}</p>
            </div>
            <button class="settings-item-action" data-dob-edit="${h(s.id)}">Edit</button>
          </div>
          <div class="settings-item settings-dob-edit-row" id="dob-edit-${h(s.id)}" style="display:none">
            <div class="settings-item-icon" style="color:${accent}">${ICONS.user}</div>
            <div class="settings-item-body" style="gap:.5rem;display:flex;flex-direction:column">
              <div class="dob-row">
                <div class="dob-field dob-field--day">
                  <label class="auth-label">Day</label>
                  <select class="auth-input dob-select" id="edit-dob-day-${h(s.id)}">
                    <option value="">—</option>
                    ${DAYS.map(n => `<option value="${n}" ${s.birthDay==n?"selected":""}>${n}</option>`).join("")}
                  </select>
                </div>
                <div class="dob-field dob-field--month">
                  <label class="auth-label">Month</label>
                  <select class="auth-input dob-select" id="edit-dob-month-${h(s.id)}">
                    <option value="">Month</option>
                    ${MONTHS.map(m => `<option value="${m}" ${s.birthMonth===m?"selected":""}>${m}</option>`).join("")}
                  </select>
                </div>
                <div class="dob-field dob-field--year">
                  <label class="auth-label">Year</label>
                  <input class="auth-input" id="edit-dob-year-${h(s.id)}" type="number"
                    placeholder="1945" min="1900" max="2020" value="${h(s.birthYear||"")}" />
                </div>
              </div>
              <div style="display:flex;gap:.5rem">
                <button class="btn btn--primary" data-dob-save="${h(s.id)}" style="flex:1;padding:.5rem">Save</button>
                <button class="btn btn--ghost" data-dob-cancel="${h(s.id)}" style="flex:1;padding:.5rem">Cancel</button>
              </div>
            </div>
          </div>`;
        }).join("")}
      </div>

      <div class="settings-section">
        <p class="settings-section-title">Appearance</p>
        <div class="settings-item">
          <div class="settings-item-icon">${ICONS.eye || ICONS.star}</div>
          <div class="settings-item-body">
            <p class="settings-item-label">Light mode</p>
            <p class="settings-item-value">Warm, high-contrast — great for bright light</p>
          </div>
          <button class="theme-toggle" id="btn-theme-toggle" aria-label="Toggle light mode"
            data-active="${document.body.dataset.theme === "light" ? "true" : "false"}">
            <span class="theme-toggle-knob"></span>
          </button>
        </div>
      </div>

      <div class="settings-section">
        <p class="settings-section-title">Interview</p>
        <div class="settings-item">
          <div class="settings-item-icon">${ICONS.chat}</div>
          <div class="settings-item-body">
            <p class="settings-item-label">Language</p>
            <p class="settings-item-value">Auto-detect</p>
          </div>
          <button class="settings-item-action" onclick="window.showComingSoon('Language settings')">Change</button>
        </div>
        <div class="settings-item">
          <div class="settings-item-icon">${ICONS.mic}</div>
          <div class="settings-item-body">
            <p class="settings-item-label">Voice input</p>
            <p class="settings-item-value">Tap mic to record</p>
          </div>
          <button class="settings-item-action" onclick="window.showComingSoon('Voice settings')">Set up</button>
        </div>
      </div>

      <div class="settings-section">
        <p class="settings-section-title">Notifications</p>
        <div class="settings-item">
          <div class="settings-item-icon">${ICONS.bell}</div>
          <div class="settings-item-body">
            <p class="settings-item-label">Daily reminder</p>
            <p class="settings-item-value">Off</p>
          </div>
          <button class="settings-item-action" onclick="window.showComingSoon('Notifications')">Enable</button>
        </div>
        <div class="settings-item">
          <div class="settings-item-icon">${ICONS.bell}</div>
          <div class="settings-item-body">
            <p class="settings-item-label">Family activity</p>
            <p class="settings-item-value">Off</p>
          </div>
          <button class="settings-item-action" onclick="window.showComingSoon('Family notifications')">Enable</button>
        </div>
      </div>

      <div class="settings-section">
        <p class="settings-section-title">Your data</p>
        <div class="settings-item">
          <div class="settings-item-icon">${ICONS.download}</div>
          <div class="settings-item-body">
            <p class="settings-item-label">Export archive</p>
            <p class="settings-item-value settings-item-value--muted">Download all memories as JSON</p>
          </div>
          <button class="settings-item-action" id="btn-export-data">Export</button>
        </div>
        <div class="settings-item">
          <div class="settings-item-icon">${ICONS.lock}</div>
          <div class="settings-item-body">
            <p class="settings-item-label">Storage</p>
            <p class="settings-item-value">All data stored locally on this device</p>
          </div>
        </div>
      </div>

      <div class="settings-section">
        <p class="settings-section-title">About</p>
        <div class="settings-item settings-item--static">
          <div class="settings-item-body">
            <p class="settings-item-label">Version</p>
            <p class="settings-item-value">1.0 · Demo build</p>
          </div>
        </div>
        <div class="settings-item settings-item--static">
          <div class="settings-item-body">
            <p class="settings-item-label">Rate A Story</p>
            <p class="settings-item-value">Share your experience</p>
          </div>
          <button class="settings-item-action" onclick="window.showComingSoon('Rate A Story')">Rate</button>
        </div>
      </div>

      <p class="fine-print" style="margin-bottom:2rem">A Story · Version 1.0</p>
    </section>
    ${tabs("menu")}`;
}

function renderMenu() {
  const story = activeStory();
  return `
    <section class="view menu-view">
      <div class="menu-profile-card">
        <div class="menu-avatar">
          ${(currentUser?.firstName||"?")[0].toUpperCase()}
        </div>
        <div>
          <p class="menu-name">${h(currentUser?.firstName||"")} ${h(currentUser?.lastName||"")}</p>
          <p class="menu-email">${h(currentUser?.email||"")}</p>
        </div>
      </div>

      <div class="menu-section">
        <p class="menu-section-title">Your stories</p>
        ${state.stories.map(s => `
          <a href="#/story/${s.id}" class="menu-item">
            <span class="menu-item-dot" style="background:${storyAccent(s)}"></span>
            <span>${h(s.name)} · ${h(relLabel(s))}</span>
            <span class="menu-item-arrow">→</span>
          </a>`).join("")}
        <a href="#/new-story" class="menu-item">
          <span class="menu-item-dot" style="background:var(--border-mid)">+</span>
          <span>Add a person</span>
        </a>
      </div>

      ${story ? `
        <div class="menu-section">
          <p class="menu-section-title">Archive</p>
          <a href="#/legacy-book" class="menu-item">
            <span class="menu-icon">${ICONS.book}</span><span>Get ${h(story.name)}'s legacy book</span>
            <span class="menu-item-arrow">${ICONS.chevron}</span>
          </a>
          <a href="#/invite" class="menu-item">
            <span class="menu-icon">${ICONS.people}</span><span>Invite family members</span>
            <span class="menu-item-arrow">${ICONS.chevron}</span>
          </a>
        </div>` : ""}

      <div class="menu-section">
        <p class="menu-section-title">Plans</p>
        <a href="#/pricing" class="menu-item">
          <span class="menu-icon">${ICONS.star}</span><span>Get A Story Premium</span>
          <span class="menu-item-arrow">${ICONS.chevron}</span>
        </a>
      </div>

      <div class="menu-section">
        <p class="menu-section-title">Account</p>
        <a href="#/settings" class="menu-item">
          <span class="menu-icon">${ICONS.settings}</span><span>Settings</span>
          <span class="menu-item-arrow">${ICONS.chevron}</span>
        </a>
        <a href="#/help" class="menu-item">
          <span class="menu-icon">${ICONS.chat}</span><span>Help &amp; Feedback</span>
          <span class="menu-item-arrow">${ICONS.chevron}</span>
        </a>
        <a href="#/privacy" class="menu-item">
          <span class="menu-icon">${ICONS.lock}</span><span>Terms &amp; Privacy Policy</span>
          <span class="menu-item-arrow">${ICONS.chevron}</span>
        </a>
      </div>

      <div class="menu-section">
        <button type="button" class="menu-item menu-item--danger" data-reset>
          <span class="menu-icon">${ICONS.refresh}</span><span>Reset all data</span>
        </button>
        <button type="button" class="menu-item menu-item--danger" data-signout>
          <span class="menu-icon">${ICONS.signout}</span><span>Sign out</span>
        </button>
      </div>
      <p class="fine-print">All memories are stored locally on this device.</p>
    </section>
    ${tabs("menu")}`;
}

/* ═══════════════════════════════════════════
   EVENTS
═══════════════════════════════════════════ */
function bindEvents() {
  /* ── Story creation ── */
  document.getElementById("back-dash")?.addEventListener("click", () => navigate("dashboard"));

  /* ── Self-check ── */
  document.getElementById("btn-self-yes")?.addEventListener("click", () => {
    storyDraft.relationship = "Myself";
    setNsStep("name");
  });
  document.getElementById("btn-self-no")?.addEventListener("click", () => {
    storyDraft.relationship = "";
    setNsStep("relationship");
  });

  document.getElementById("ns-name-form")?.addEventListener("submit", (e) => {
    e.preventDefault();
    const fd = new FormData(e.target);
    storyDraft.name     = fd.get("firstName")?.toString().trim();
    storyDraft.lastName = fd.get("lastName")?.toString().trim();
    if (!storyDraft.name) return;
    const nextStep = storyDraft.relationship === "Myself" ? "dob" : "has-account";
    setNsStep(nextStep);
  });

  // Relationship grid
  app.querySelectorAll("[data-rel]").forEach(btn => {
    btn.onclick = () => {
      app.querySelectorAll("[data-rel]").forEach(b => b.classList.remove("is-selected"));
      btn.classList.add("is-selected");
      storyDraft.relationship = btn.dataset.rel;
      const wrap  = document.getElementById("rel-other-wrap");
      const cont  = document.getElementById("rel-continue");
      if (btn.dataset.rel === "Other") {
        wrap?.classList.add("visible");
        document.getElementById("rel-other-input")?.focus();
      } else {
        wrap?.classList.remove("visible");
        storyDraft.relationshipOther = "";
      }
      if (cont) cont.disabled = false;
    };
  });

  document.getElementById("rel-continue")?.addEventListener("click", () => {
    if (!storyDraft.relationship) return;
    if (storyDraft.relationship === "Other") {
      storyDraft.relationshipOther = document.getElementById("rel-other-input")?.value.trim() || "";
      if (!storyDraft.relationshipOther) { document.getElementById("rel-other-input")?.focus(); return; }
    }
    setNsStep("name");
  });

  // Account question
  app.querySelectorAll("[data-acct]").forEach(btn => {
    btn.onclick = () => {
      storyDraft.hasAccount = btn.dataset.acct;
      setNsStep("has-account");   // re-render to show connect form if needed
    };
  });

  document.getElementById("aq-continue")?.addEventListener("click", () => {
    if (!storyDraft.hasAccount) return;
    if (storyDraft.hasAccount === "yes") {
      storyDraft.connectedEmail = document.getElementById("aq-email")?.value.trim() || "";
    }
    setNsStep("dob");
  });

  document.getElementById("ns-dob-form")?.addEventListener("submit", (e) => {
    e.preventDefault();
    const fd = new FormData(e.target);
    storyDraft.birthDay   = fd.get("day")?.toString() || "";
    storyDraft.birthMonth = fd.get("month")?.toString() || "";
    storyDraft.birthYear  = fd.get("year")?.toString().trim() || "";
    setNsStep("done");
  });
  document.getElementById("dob-skip")?.addEventListener("click", () => setNsStep("done"));

  document.querySelectorAll("[data-ns-back]").forEach(btn => {
    btn.onclick = () => setNsStep(btn.dataset.nsBack);
  });

  document.getElementById("btn-create-story")?.addEventListener("click", () => {
    const story = createStory({
      name:              storyDraft.name || "",
      lastName:          storyDraft.lastName || "",
      relationship:      storyDraft.relationship || "Relative",
      relationshipOther: storyDraft.relationshipOther || "",
      birthDay:          storyDraft.birthDay || "",
      birthMonth:        storyDraft.birthMonth || "",
      birthYear:         storyDraft.birthYear || "",
      hasAccount:        storyDraft.hasAccount || null,
      connectedEmail:    storyDraft.connectedEmail || "",
    });
    state.stories.push(story);
    state.activeStoryId = story.id;
    state.onboardingComplete = true;
    storyDraft = {};
    persist();
    navigate("dashboard");
  });

  /* ── Dashboard ── */
  document.getElementById("btn-switch-story")?.addEventListener("click", () => {
    document.getElementById("story-switcher")?.classList.toggle("hidden");
  });
  app.querySelectorAll("[data-switch]").forEach(btn => {
    btn.onclick = () => {
      state.activeStoryId = btn.dataset.switch;
      persist();
      render();
    };
  });

  /* ── Era / topic chips on dashboard ── */
  app.querySelectorAll("[data-era]").forEach(btn => {
    btn.onclick = () => {
      session = null;
      const sid = btn.dataset.eraSid;
      location.hash = `#/story/${sid}/talk?era=${encodeURIComponent(btn.dataset.era)}`;
    };
  });

  /* ── Print ── */
  app.querySelectorAll("[data-print]").forEach(btn => {
    btn.onclick = () => {
      const story = storyById(btn.dataset.print);
      if (story) printLegacy({ ...state, memories: story.memories, profile: { name: story.name } });
    };
  });

  /* ── Memory detail form ── */
  document.getElementById("mem-form")?.addEventListener("submit", (e) => {
    e.preventDefault();
    const { storyId, memId } = route.params;
    const story  = storyById(storyId);
    const memory = story?.memories.find(m => m.id === memId);
    if (!story || !memory) return;
    const fd = new FormData(e.target);
    memory.body    = fd.get("body")?.toString().trim() || memory.body;
    memory.excerpt = memory.body.length > 160 ? `${memory.body.slice(0,157)}…` : memory.body;
    persist(); navigate("tree", { storyId });
  });

  app.querySelector("[data-del-mem]")?.addEventListener("click", (e) => {
    const { storyId } = route.params;
    const story = storyById(storyId);
    if (!story || !confirm("Delete this memory permanently?")) return;
    story.memories = story.memories.filter(m => m.id !== e.target.dataset.delMem);
    persist(); navigate("tree", { storyId });
  });

  /* ── Memory card screen ── */
  app.querySelectorAll("[data-star]").forEach(btn => {
    btn.onclick = () => {
      const n = parseInt(btn.dataset.star);
      if (session) session.rating = n;
      const starRow = document.getElementById("star-rating");
      if (starRow) starRow.style.display = "none";
    };
  });

  /* ── Privacy toggle on memory card ── */
  document.getElementById("btn-mc-privacy")?.addEventListener("click", () => {
    if (!session) return;
    const isShared = session.privacy !== "private";
    session.privacy = isShared ? "private" : "shared";
    const btn   = document.getElementById("btn-mc-privacy");
    const title = document.getElementById("mc-privacy-title");
    const desc  = document.getElementById("mc-privacy-desc");
    const label = document.getElementById("mc-privacy-label");
    if (session.privacy === "private") {
      btn.classList.remove("is-shared"); btn.classList.add("is-private");
      label.textContent  = "Private";
      title.textContent  = "Private memory";
      desc.textContent   = "Only you can see this memory.";
    } else {
      btn.classList.remove("is-private"); btn.classList.add("is-shared");
      label.textContent  = "Shared";
      title.textContent  = "Shared with family";
      desc.textContent   = "Family members with your invite link can view and contribute — listed as co-authors.";
    }
  });

  document.getElementById("btn-save-card")?.addEventListener("click", () => {
    if (!session) return;
    const story = storyById(session.storyId);
    if (!story) return;
    const ratingNote = document.getElementById("mc-note")?.value.trim() || "";
    const mem = createMemory({ ...session.draft, rating: session.rating || 0, ratingNote, period: session.period || "", privacy: session.privacy || "shared" });
    mem.photos = session.photos || [];
    story.memories.unshift(mem);
    story.sessionsCompleted += 1;

    // Mark this era/topic as done for today
    if (session.selectedEra) {
      const todayStr = new Date().toDateString();
      if (!story.doneTopicsToday || story.doneTopicsToday.date !== todayStr) {
        story.doneTopicsToday = { date: todayStr, eras: [] };
      }
      if (!story.doneTopicsToday.eras.includes(session.selectedEra)) {
        story.doneTopicsToday.eras.push(session.selectedEra);
      }
    }

    persist();
    const isFirst = story.sessionsCompleted === 1;
    session = null;
    navigate("tree", { storyId: story.id });
    if (isFirst) showNotificationPrompt();
  });

  document.getElementById("mc-back")?.addEventListener("click", () => {
    // Go back to chat (keep session but revert phase)
    if (session) session.phase = "followup";
    render();
  });

  /* ── Memory detail: speak + AI refine ── */
  document.getElementById("btn-detail-speak")?.addEventListener("click", () => {
    window.showComingSoon("Voice editing");
  });

  document.getElementById("btn-ai-refine")?.addEventListener("click", () => {
    const textarea = document.querySelector("#mem-form textarea[name=body]");
    if (!textarea || !textarea.value.trim()) return;
    const btn = document.getElementById("btn-ai-refine");
    btn.disabled = true; btn.textContent = "Refining…";
    setTimeout(() => {
      btn.disabled = false; btn.innerHTML = "✦ Ask AI to refine";
      const cs = document.getElementById("cs-modal");
      if (cs) cs.remove();
      const overlay = document.createElement("div");
      overlay.id = "cs-modal"; overlay.className = "cs-overlay";
      overlay.innerHTML = `
        <div class="cs-modal">
          <p class="cs-icon">${ICONS.sparkle || "✦"}</p>
          <h3 class="cs-title">AI Editing</h3>
          <p class="cs-body">AI-powered editing is coming soon. Your story has been preserved exactly as you wrote it.</p>
          <button class="btn btn--primary" onclick="document.getElementById('cs-modal').remove()">Got it</button>
        </div>`;
      document.body.appendChild(overlay);
      overlay.onclick = (ev) => { if (ev.target === overlay) overlay.remove(); };
    }, 1200);
  });

  /* ── Settings: edit name ── */
  document.getElementById("btn-edit-name")?.addEventListener("click", () => {
    document.getElementById("settings-name-display").style.display = "none";
    document.getElementById("settings-name-edit").style.display = "";
    document.getElementById("edit-first-name")?.focus();
  });

  document.getElementById("btn-cancel-name")?.addEventListener("click", () => {
    document.getElementById("settings-name-display").style.display = "";
    document.getElementById("settings-name-edit").style.display = "none";
  });

  document.getElementById("btn-save-name")?.addEventListener("click", () => {
    const first = document.getElementById("edit-first-name")?.value.trim();
    const last  = document.getElementById("edit-last-name")?.value.trim();
    if (!first) return;
    if (currentUser) {
      currentUser.firstName = first;
      currentUser.lastName  = last || "";
      setSession(currentUser);
    }
    document.getElementById("settings-name-value").textContent = `${first} ${last||""}`.trim();
    document.getElementById("settings-name-display").style.display = "";
    document.getElementById("settings-name-edit").style.display = "none";
  });

  /* ── Settings: DOB edit per story profile ── */
  app.querySelectorAll("[data-dob-edit]").forEach(btn => {
    const id = btn.dataset.dobEdit;
    btn.addEventListener("click", () => {
      document.getElementById(`dob-display-${id}`).style.display = "none";
      document.getElementById(`dob-edit-${id}`).style.display = "";
      document.getElementById(`edit-dob-year-${id}`)?.focus();
    });
  });

  app.querySelectorAll("[data-dob-cancel]").forEach(btn => {
    const id = btn.dataset.dobCancel;
    btn.addEventListener("click", () => {
      document.getElementById(`dob-display-${id}`).style.display = "";
      document.getElementById(`dob-edit-${id}`).style.display = "none";
    });
  });

  app.querySelectorAll("[data-dob-save]").forEach(btn => {
    const id = btn.dataset.dobSave;
    btn.addEventListener("click", () => {
      const story = storyById(id);
      if (!story) return;
      story.birthDay   = document.getElementById(`edit-dob-day-${id}`)?.value || "";
      story.birthMonth = document.getElementById(`edit-dob-month-${id}`)?.value || "";
      story.birthYear  = document.getElementById(`edit-dob-year-${id}`)?.value || "";
      persist();
      const display = document.getElementById(`dob-display-${id}`);
      if (display) {
        const val = display.querySelector(".settings-item-value");
        if (val) val.textContent = fmtDob(story.birthDay, story.birthMonth, story.birthYear) || "Birthday not set";
      }
      document.getElementById(`dob-display-${id}`).style.display = "";
      document.getElementById(`dob-edit-${id}`).style.display = "none";
    });
  });

  /* ── Settings: export data ── */
  document.getElementById("btn-export-data")?.addEventListener("click", () => {
    const exportData = {
      exportedAt: new Date().toISOString(),
      user: { email: currentUser?.email, firstName: currentUser?.firstName, lastName: currentUser?.lastName },
      stories: state.stories.map(s => ({
        name: s.name, lastName: s.lastName, relationship: s.relationship,
        birthDay: s.birthDay, birthMonth: s.birthMonth, birthYear: s.birthYear,
        memories: s.memories.map(m => ({
          title: m.title, body: m.body, era: m.era,
          period: m.period, privacy: m.privacy,
          createdAt: m.createdAt,
        })),
      })),
    };
    const blob = new Blob([JSON.stringify(exportData, null, 2)], { type: "application/json" });
    const url  = URL.createObjectURL(blob);
    const a    = document.createElement("a");
    a.href = url; a.download = "a-story-archive.json";
    a.click(); URL.revokeObjectURL(url);
  });

  /* ── Photo memory form ── */
  document.getElementById("pm-form")?.addEventListener("submit", (e) => {
    e.preventDefault();
    if (!photoMemoryDraft) return;
    const { storyId, photos } = photoMemoryDraft;
    const story = storyById(storyId);
    if (!story) return;
    const fd    = new FormData(e.target);
    const body  = fd.get("body")?.toString().trim() || "";
    const era   = fd.get("era")?.toString() || "Life";
    const title = body.length > 50 ? body.slice(0, 47) + "…" : body;
    const mem   = createMemory({ title: title || "Photo memory", body, era, excerpt: body.slice(0,140), rating: 0, ratingNote: "", period: "", privacy: "shared" });
    mem.photos  = photos;
    story.memories.unshift(mem);
    persist();
    photoMemoryDraft = null;
    navigate("tree", { storyId });
  });

  /* ── Archive photo upload ── */
  document.getElementById("archive-photo-input")?.addEventListener("change", (e) => {
    const files = Array.from(e.target.files);
    if (!files.length) return;
    const { storyId } = route.params;
    const story = storyById(storyId);
    if (!story) return;
    photoMemoryDraft = { storyId, photos: [] };
    let done = 0;
    files.forEach(file => {
      const reader = new FileReader();
      reader.onload = () => {
        photoMemoryDraft.photos.push(reader.result);
        done++;
        if (done === files.length) navigate("photo-memory");
      };
      reader.readAsDataURL(file);
    });
    e.target.value = "";
  });

  /* ── Tree fuzzy search ── */
  document.getElementById("tree-search")?.addEventListener("input", (e) => {
    const q = e.target.value.trim().toLowerCase();
    const content  = document.getElementById("tree-content");
    const noResult = document.getElementById("tree-no-results");
    if (!q) {
      content.querySelectorAll("[data-mem-search]").forEach(li => li.style.display = "");
      content.querySelectorAll("[data-era-group]").forEach(li => li.style.display = "");
      if (noResult) noResult.style.display = "none";
      return;
    }
    // Fuzzy: every char in query must appear in order in the target
    const fuzzy = (text, pattern) => {
      let pi = 0;
      for (const ch of text) { if (ch === pattern[pi]) pi++; if (pi === pattern.length) return true; }
      return false;
    };
    let anyVisible = false;
    content.querySelectorAll("[data-era-group]").forEach(eraLi => {
      let eraHasMatch = false;
      eraLi.querySelectorAll("[data-mem-search]").forEach(li => {
        const match = fuzzy(li.dataset.memSearch, q);
        li.style.display = match ? "" : "none";
        if (match) eraHasMatch = true;
      });
      eraLi.style.display = eraHasMatch ? "" : "none";
      if (eraHasMatch) anyVisible = true;
    });
    if (noResult) noResult.style.display = anyVisible ? "none" : "";
  });

  /* ── Theme toggle ── */
  document.getElementById("btn-theme-toggle")?.addEventListener("click", (e) => {
    const btn = e.currentTarget;
    const isLight = document.body.dataset.theme === "light";
    const next = isLight ? "" : "light";
    document.body.dataset.theme = next;
    localStorage.setItem("astory-theme", next);
    btn.dataset.active = next === "light" ? "true" : "false";
  });

  /* ── Settings / sign out ── */
  app.querySelector("[data-reset]")?.addEventListener("click", () => {
    if (!confirm("Reset all data? Cannot be undone.")) return;
    state = resetState(currentUser.email);
    session = null; storyDraft = {};
    navigate("new-story");
  });
  /* ── Memory card photo upload ── */
  document.getElementById("mc-photo-input")?.addEventListener("change", (e) => {
    if (!session) return;
    if (!session.photos) session.photos = [];
    readPhotos(Array.from(e.target.files), (base64s) => {
      session.photos.push(...base64s);
      refreshMcPhotoGrid();
    });
  });

  app.querySelectorAll("[data-photo-idx]").forEach(btn => {
    btn.onclick = () => {
      const idx = parseInt(btn.dataset.photoIdx);
      if (session?.photos) { session.photos.splice(idx, 1); refreshMcPhotoGrid(); }
    };
  });

  /* ── Memory detail photo upload ── */
  document.getElementById("detail-photo-input")?.addEventListener("change", (e) => {
    const { storyId, memId } = route.params;
    const story  = storyById(storyId);
    const memory = story?.memories.find(m => m.id === memId);
    if (!memory) return;
    if (!memory.photos) memory.photos = [];
    readPhotos(Array.from(e.target.files), (base64s) => {
      memory.photos.push(...base64s);
      persist();
      refreshDetailPhotoGrid(memory);
    });
  });

  app.querySelectorAll("[data-detail-photo]").forEach(btn => {
    btn.onclick = () => {
      const { storyId, memId } = route.params;
      const story  = storyById(storyId);
      const memory = story?.memories.find(m => m.id === memId);
      if (!memory) return;
      const idx = parseInt(btn.dataset.detailPhoto);
      memory.photos.splice(idx, 1);
      persist();
      refreshDetailPhotoGrid(memory);
    };
  });

  /* ── FAQ accordion ── */
  app.querySelectorAll("[data-faq]").forEach(btn => {
    btn.onclick = () => {
      const idx   = btn.dataset.faq;
      const panel = document.getElementById(`faq-a-${idx}`);
      const chev  = document.getElementById(`faq-ch-${idx}`);
      const open  = !panel.hidden;
      // Close all
      app.querySelectorAll(".faq-a").forEach(p => { p.hidden = true; });
      app.querySelectorAll(".faq-chevron").forEach(c => { c.textContent = "›"; c.classList.remove("open"); });
      app.querySelectorAll(".faq-q").forEach(b => b.setAttribute("aria-expanded", "false"));
      // Open this one (toggle)
      if (!open) {
        panel.hidden = false;
        if (chev)  { chev.textContent = "⌄"; chev.classList.add("open"); }
        btn.setAttribute("aria-expanded", "true");
      }
    };
  });

  /* ── Feedback type chips ── */
  app.querySelectorAll("[data-fb-type]").forEach(btn => {
    btn.onclick = () => {
      app.querySelectorAll("[data-fb-type]").forEach(b => b.classList.remove("is-active"));
      btn.classList.add("is-active");
    };
  });

  /* ── Feedback form ── */
  document.getElementById("feedback-form")?.addEventListener("submit", (e) => {
    e.preventDefault();
    const btn = e.target.querySelector("button[type=submit]");
    btn.disabled = true; btn.textContent = "Sending…";
    setTimeout(() => {
      btn.disabled = false; btn.textContent = "Send feedback →";
      e.target.reset();
      app.querySelectorAll("[data-fb-type]").forEach((b, i) => b.classList.toggle("is-active", i === 0));
      document.getElementById("cs-modal")?.remove();
      const el = document.createElement("div");
      el.id = "cs-modal"; el.className = "cs-overlay";
      el.innerHTML = `
        <div class="cs-modal">
          <p class="cs-icon">${ICONS.mail}</p>
          <h3 class="cs-title">Feedback sent</h3>
          <p class="cs-body">Thank you — we read every message and use it to make A Story better.</p>
          <button class="btn btn--primary" onclick="document.getElementById('cs-modal').remove()">Done</button>
        </div>`;
      document.body.appendChild(el);
      el.onclick = (ev) => { if (ev.target === el) el.remove(); };
    }, 800);
  });

  /* ── Contact form ── */
  document.getElementById("contact-form")?.addEventListener("submit", (e) => {
    e.preventDefault();
    const btn = e.target.querySelector("button[type=submit]");
    btn.disabled = true; btn.textContent = "Sending…";
    setTimeout(() => {
      btn.disabled = false; btn.textContent = "Send message →";
      e.target.reset();
      document.getElementById("cs-modal")?.remove();
      const el = document.createElement("div");
      el.id = "cs-modal"; el.className = "cs-overlay";
      el.innerHTML = `
        <div class="cs-modal">
          <p class="cs-icon">${ICONS.mail}</p>
          <h3 class="cs-title">Message sent</h3>
          <p class="cs-body">We'll get back to you at your email within 24 hours.</p>
          <button class="btn btn--primary" onclick="document.getElementById('cs-modal').remove()">Done</button>
        </div>`;
      document.body.appendChild(el);
      el.onclick = (ev) => { if (ev.target === el) el.remove(); };
    }, 800);
  });

  /* ── Privacy back (handles both in-app and auth-flow contexts) ── */
  document.getElementById("privacy-back")?.addEventListener("click", (e) => {
    // If coming from auth flow (no currentUser), just go back
    if (!currentUser) { e.preventDefault(); history.back(); }
  });

  /* ── Legacy book form ── */
  document.getElementById("lb-form")?.addEventListener("submit", (e) => {
    e.preventDefault();
    window.showComingSoon("Legacy book ordering");
  });

  /* ── Invite form + copy link ── */
  document.getElementById("invite-form")?.addEventListener("submit", (e) => {
    e.preventDefault();
    window.showComingSoon("Sending family invites");
  });

  document.getElementById("btn-copy-link")?.addEventListener("click", () => {
    const link = document.getElementById("invite-link-text")?.textContent || "";
    const btn  = document.getElementById("btn-copy-link");
    navigator.clipboard?.writeText(link).then(() => {
      if (btn) { btn.textContent = "Copied!"; btn.classList.add("copied"); }
      setTimeout(() => { if (btn) { btn.textContent = "Copy"; btn.classList.remove("copied"); } }, 2500);
    }).catch(() => {
      // Fallback for browsers without clipboard API
      if (btn) btn.textContent = "Copy the link above";
    });
  });

  /* ── Pricing toggle (full pricing page) ── */
  const btnMonthly = document.getElementById("toggle-monthly");
  const btnAnnual  = document.getElementById("toggle-annual");
  if (btnMonthly && btnAnnual) {
    const pricingPrices = {
      pro:  { m: "$8",  mPer: "per month", a: "$80",  aPer: "per year" },
      fam:  { m: "$13", mPer: "per month", a: "$130", aPer: "per year" },
      plus: { m: "$20", mPer: "per month", a: "$200", aPer: "per year" },
    };
    function applyPricingBilling(annual) {
      btnMonthly.classList.toggle("is-active", !annual);
      btnAnnual.classList.toggle("is-active", annual);
      const badge = document.getElementById("toggle-save-badge");
      if (badge) badge.classList.toggle("visible", annual);
      for (const [key, vals] of Object.entries(pricingPrices)) {
        const priceEl  = document.getElementById(`${key}-price`);
        const periodEl = document.getElementById(`${key}-period`);
        if (priceEl)  priceEl.textContent  = annual ? vals.a  : vals.m;
        if (periodEl) periodEl.textContent = annual ? vals.aPer : vals.mPer;
      }
    }
    btnMonthly.onclick = () => applyPricingBilling(false);
    btnAnnual.onclick  = () => applyPricingBilling(true);
  }

  app.querySelector("[data-signout]")?.addEventListener("click", () => {
    if (!confirm("Sign out?")) return;
    clearSession(); session = null; storyDraft = {};
    currentUser = null; state = null;
    location.hash = "";
    showSplash();
  });

  /* ── Chat ── */
  setupChat();
}

function setNsStep(step) {
  route.name   = "new-story";
  route.params = { step };
  app.innerHTML = renderNewStory();
  bindEvents();
}

/* ─── Photo helpers ─── */
function readPhotos(files, cb) {
  const results = [];
  if (!files.length) return;
  files.forEach(file => {
    const reader = new FileReader();
    reader.onload = (ev) => {
      results.push(ev.target.result);
      if (results.length === files.length) cb(results);
    };
    reader.readAsDataURL(file);
  });
}

function refreshMcPhotoGrid() {
  const grid = document.getElementById("mc-photo-grid");
  if (!grid || !session) return;
  grid.innerHTML = (session.photos || []).map((src, i) => `
    <div class="photo-thumb">
      <img src="${src}" alt="Memory photo" />
      <button class="photo-remove" data-photo-idx="${i}" aria-label="Remove photo">×</button>
    </div>`).join("");
  // Re-bind remove buttons
  grid.querySelectorAll("[data-photo-idx]").forEach(btn => {
    btn.onclick = () => {
      const idx = parseInt(btn.dataset.photoIdx);
      if (session?.photos) { session.photos.splice(idx, 1); refreshMcPhotoGrid(); }
    };
  });
}

function refreshDetailPhotoGrid(memory) {
  const grid = document.getElementById("detail-photo-grid");
  if (!grid) return;
  grid.innerHTML = (memory.photos || []).map((src, i) => `
    <div class="photo-thumb">
      <img src="${src}" alt="Memory photo" />
      <button type="button" class="photo-remove" data-detail-photo="${i}" aria-label="Remove">×</button>
    </div>`).join("");
  grid.querySelectorAll("[data-detail-photo]").forEach(btn => {
    btn.onclick = () => {
      const idx = parseInt(btn.dataset.detailPhoto);
      memory.photos.splice(idx, 1);
      persist();
      refreshDetailPhotoGrid(memory);
    };
  });
}

/* ─── Chat events ─── */
function setupChat() {
  const textarea = document.getElementById("chat-textarea");
  const sendBtn  = document.getElementById("btn-send");
  const backBtn  = document.getElementById("chat-back");
  const micBtn   = document.getElementById("btn-mic");
  if (!textarea || !sendBtn) return;

  backBtn?.addEventListener("click", () => {
    const sid = session?.storyId;
    session = null;
    sid ? navigate("dashboard") : navigate("dashboard");
  });

  setTimeout(() => {
    if (!session) return;
    const pName = session.personName;
    const intro = session.firstPerson
      ? `Hello — I'm so glad you're here.\n\nI'm here to listen and help preserve your story. There are no rules, no order, no right or wrong answers. You're in complete control — share whatever feels meaningful, and stop or pause whenever you like.\n\nWhenever you're ready... ${session.prompt.question}`
      : `Hello — I'm so glad you're here.\n\nI'm here to help you capture and preserve ${pName}'s story — the memories, the moments, the details that matter. There are no rules and no order. Share whatever comes to mind, and stop whenever you like.\n\nWhenever you're ready... ${session.prompt.question}`;
    appendAI(intro);
  }, 380);

  textarea.addEventListener("input", () => {
    textarea.style.height = "auto";
    textarea.style.height = Math.min(textarea.scrollHeight, 120) + "px";
    sendBtn.disabled = !textarea.value.trim();
  });
  textarea.addEventListener("keydown", (e) => {
    if (e.key === "Enter" && !e.shiftKey) { e.preventDefault(); if (!sendBtn.disabled) send(); }
  });
  sendBtn.addEventListener("click", send);
  micBtn?.addEventListener("click", () => {
    if (!textarea.value.trim()) {
      textarea.value = DEMO_VOICES[Math.floor(Math.random()*DEMO_VOICES.length)];
      textarea.dispatchEvent(new Event("input"));
      textarea.focus();
    }
  });

  function send() {
    const text = textarea.value.trim();
    if (!text || !session || session.phase === "done" || session.phase === "card" || session.phase === "wrap-up") return;
    appendUser(text);
    textarea.value = ""; textarea.style.height = "auto";
    sendBtn.disabled = true; textarea.disabled = true;
    textarea.placeholder = "A Story is listening…";
    session.answers.push(text);

    if (session.phase === "first") {
      session.phase = "followup";
      showThinking();
      setTimeout(() => {
        hideThinking();
        appendAI(aiFollowUpResponse(text, session.prompt.followUp));
        textarea.disabled = false; textarea.placeholder = "Keep going…"; textarea.focus();
      }, 1100 + Math.random()*500);

    } else if (session.phase === "followup") {
      session.phase = "period";
      showThinking();
      setTimeout(() => {
        hideThinking();
        appendAI(aiPeriodQuestion(session.firstPerson, session.personName, session.prompt.era));
        textarea.disabled = false; textarea.placeholder = "e.g. Around 1978, mid-50s…"; textarea.focus();
      }, 900 + Math.random()*400);

    } else if (session.phase === "period") {
      session.period = text;
      session.phase = "reflecting";
      showThinking();
      setTimeout(() => {
        hideThinking();
        appendAI(aiReflection(session.answers, session.firstPerson, session.personName));
        setTimeout(() => {
          session.phase = "wrap-up";
          textarea.disabled = true;
          textarea.placeholder = "A Story is listening…";
          const wrapEl = document.createElement("div");
          wrapEl.id = "wrap-up-choices";
          wrapEl.className = "wrap-up-choices";
          wrapEl.innerHTML = `
            <p class="wrap-up-prompt">Would you like to keep sharing, or save this as a memory card?</p>
            <div class="wrap-up-btns">
              <button class="wrap-up-btn wrap-up-btn--continue" id="btn-wrapup-continue">Keep sharing</button>
              <button class="wrap-up-btn wrap-up-btn--save" id="btn-wrapup-save">Save memory card →</button>
            </div>`;
          chatEl().appendChild(wrapEl);
          scrollDn();
          document.getElementById("btn-wrapup-continue")?.addEventListener("click", () => {
            wrapEl.remove();
            session.phase = "followup";
            textarea.disabled = false;
            textarea.placeholder = "Keep going…";
            textarea.focus();
          });
          document.getElementById("btn-wrapup-save")?.addEventListener("click", () => {
            wrapEl.remove();
            session.draft  = draftMemoryFromAnswers(session.prompt, session.answers);
            session.phase  = "card";
            session.rating = 0;
            render();
          });
        }, 600);
      }, 1600 + Math.random()*600);
    }
  }
}
