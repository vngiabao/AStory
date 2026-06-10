const stateKey = (email) => `astory-state-${email.toLowerCase()}`;

const DEFAULT_STATE = {
  onboardingComplete: false,
  activeStoryId: null,
  stories: [],
};

export function loadState(email) {
  try {
    const raw = localStorage.getItem(stateKey(email));
    if (!raw) return structuredClone(DEFAULT_STATE);
    return { ...structuredClone(DEFAULT_STATE), ...JSON.parse(raw) };
  } catch {
    return structuredClone(DEFAULT_STATE);
  }
}

export function saveState(email, state) {
  localStorage.setItem(stateKey(email), JSON.stringify(state));
}

export function resetState(email) {
  localStorage.removeItem(stateKey(email));
  return structuredClone(DEFAULT_STATE);
}

export function createStory({
  name, lastName, relationship, relationshipOther,
  birthDay, birthMonth, birthYear, hasAccount, connectedEmail,
}) {
  return {
    id: crypto.randomUUID(),
    name: name || "",
    lastName: lastName || "",
    relationship: relationship || "Relative",
    relationshipOther: relationshipOther || "",
    birthDay: birthDay || "",
    birthMonth: birthMonth || "",
    birthYear: birthYear || "",
    hasAccount: hasAccount || null,   // 'yes' | 'no' | 'cannot' | null
    connectedEmail: connectedEmail || "",
    photo: null,
    intro: "",
    memories: [],
    sessionsCompleted: 0,
    doneTopicsToday: { date: "", eras: [] },
    createdAt: new Date().toISOString(),
  };
}

export function createMemory({ title, body, era, excerpt, rating, ratingNote, period, privacy }) {
  return {
    id: crypto.randomUUID(),
    title,
    body,
    era: era || "Life",
    excerpt: excerpt || body.slice(0, 140),
    rating: rating || 0,
    ratingNote: ratingNote || "",
    period: period || "",
    privacy: privacy || "shared",
    photos: [],
    createdAt: new Date().toISOString(),
  };
}

export const ERAS = [
  "Childhood",
  "School years",
  "Early adulthood",
  "Career",
  "Love & family",
  "Later years",
  "Wisdom",
];

export const RELATIONSHIPS = [
  "Grandmother",
  "Grandfather",
  "Mother",
  "Father",
  "Spouse",
  "Sibling",
  "Relative",
  "Friend",
  "Myself",
  "Other",
];

export const REL_COLOR = {
  Grandmother:  "#c9915a",
  Grandfather:  "#c9915a",
  Mother:       "#6aac88",
  Father:       "#6aac88",
  Spouse:       "#b3709e",
  Sibling:      "#6d8dc2",
  Relative:     "#b89060",
  Friend:       "#8d78c4",
  Myself:       "#d4a574",
  Other:        "#a09070",
};
