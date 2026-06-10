const SESSION_KEY  = "astory-session-v1";
const ACCOUNTS_KEY = "astory-accounts-v1";

export const DEMO_EMAIL    = "demo@astory.com";
export const DEMO_PASSWORD = "demo1234";

const DEMO_MEMORIES = [
  {
    id: "dm-1",
    title: "The house on Maple Street",
    body: `We had a white clapboard house with a blue front porch. In the summer, my mother hung wind chimes from the rafters — little copper ones that sounded like rain when the breeze came through. I spent whole afternoons on that porch with a library book, listening to them sing.\n\nThe yard had a linden tree that bloomed every June. The smell of it would drift through my bedroom window at night, and to this day, if I catch that scent somewhere, I'm nine years old again.`,
    excerpt: "White clapboard, a blue porch, copper wind chimes that sounded like rain.",
    era: "Childhood",
    rating: 5,
    ratingNote: "This one always makes me emotional.",
    photos: [],
    createdAt: new Date(Date.now() - 12 * 24 * 60 * 60 * 1000).toISOString(),
  },
  {
    id: "dm-2",
    title: "Mrs. Calloway believed in me",
    body: `My eighth-grade English teacher, Mrs. Calloway. She pulled me aside after class one Thursday and said, "You have a voice. Don't waste it."\n\nI was fourteen and more interested in baseball. But I've thought about that sentence nearly every day since. She saw something I couldn't see in myself yet. I don't think she ever knew how much it mattered.`,
    excerpt: "She pulled me aside and said: 'You have a voice. Don't waste it.'",
    era: "Childhood",
    rating: 4,
    ratingNote: "",
    photos: [],
    createdAt: new Date(Date.now() - 19 * 24 * 60 * 60 * 1000).toISOString(),
  },
  {
    id: "dm-3",
    title: "New Mexico, the summer of '87",
    body: `My college friend Pete and I drove from Chicago with nothing but a cooler, a road atlas, and three hundred dollars between us. We got lost outside Taos at dusk and ended up sleeping on the hood of the car under more stars than either of us had ever seen.\n\nI remember lying there thinking: this is what freedom feels like. I was twenty-three. I didn't know yet how rarely you'd feel that way again.`,
    excerpt: "Lost outside Taos, sleeping under more stars than I'd ever seen.",
    era: "Early adulthood",
    rating: 5,
    ratingNote: "",
    photos: [],
    createdAt: new Date(Date.now() - 26 * 24 * 60 * 60 * 1000).toISOString(),
  },
];

export const DEMO_STATE = {
  onboardingComplete: true,
  activeStoryId: "demo-story-walter",
  stories: [
    {
      id: "demo-story-walter",
      name: "Walter",
      lastName: "Morrison",
      relationship: "Grandfather",
      relationshipOther: "",
      birthMonth: "March",
      birthYear: "1945",
      hasAccount: "cannot",
      connectedEmail: "",
      photo: null,
      intro: "A man of few words but countless stories.",
      memories: DEMO_MEMORIES,
      sessionsCompleted: 3,
      createdAt: new Date(Date.now() - 30 * 24 * 60 * 60 * 1000).toISOString(),
    },
  ],
};

/* ─── Session ─── */
export function getSession() {
  try {
    const raw = localStorage.getItem(SESSION_KEY);
    return raw ? JSON.parse(raw) : null;
  } catch { return null; }
}

export function setSession(data) {
  localStorage.setItem(SESSION_KEY, JSON.stringify(data));
}

export function clearSession() {
  localStorage.removeItem(SESSION_KEY);
}

/* ─── Accounts ─── */
function readAccounts() {
  try {
    const raw = localStorage.getItem(ACCOUNTS_KEY);
    return raw ? JSON.parse(raw) : {};
  } catch { return {}; }
}

function writeAccounts(accs) {
  localStorage.setItem(ACCOUNTS_KEY, JSON.stringify(accs));
}

export function createAccount(firstName, lastName, email, password) {
  const norm = email.toLowerCase().trim();
  if (norm === DEMO_EMAIL) return { error: "That email is already taken." };
  const accs = readAccounts();
  if (accs[norm]) return { error: "An account with this email already exists." };
  accs[norm] = { firstName: firstName.trim(), lastName: lastName.trim(), password };
  writeAccounts(accs);
  return { ok: true, firstName: firstName.trim(), lastName: lastName.trim() };
}

export function verifyLogin(email, password) {
  const norm = email.toLowerCase().trim();
  if (norm === DEMO_EMAIL) {
    if (password === DEMO_PASSWORD) {
      return { ok: true, firstName: "Sarah", lastName: "Morrison", isDemo: true };
    }
    return { error: "Incorrect password." };
  }
  const accs = readAccounts();
  const acc  = accs[norm];
  if (!acc) return { error: "No account found with that email." };
  if (acc.password !== password) return { error: "Incorrect password." };
  return { ok: true, firstName: acc.firstName, lastName: acc.lastName };
}

export function ensureDemoState() {
  const key = `astory-state-${DEMO_EMAIL}`;
  if (!localStorage.getItem(key)) {
    localStorage.setItem(key, JSON.stringify(DEMO_STATE));
  }
}
