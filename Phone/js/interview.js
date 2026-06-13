// Prompts have two forms: first-person (storyteller IS the subject)
// and third-person (family organizer capturing someone else's story).
const RAW_PROMPTS = [
  // === Childhood ===
  {
    first: "Tell me about your childhood home.",
    third: "Tell me about {name}'s childhood home.",
    followUp: "What sounds or smells come back to you when you think of it?",
    era: "Childhood",
  },
  {
    first: "Who was the first person who truly believed in you?",
    third: "Who was the first person who truly believed in {name}?",
    followUp: "What did they do or say that still stays with you?",
    era: "Childhood",
  },
  {
    first: "What was your neighborhood like growing up?",
    third: "What was {name}'s neighborhood like growing up?",
    followUp: "Who were the kids you ran around with, and what did you all get up to?",
    era: "Childhood",
  },
  {
    first: "What game or toy meant everything to you as a child?",
    third: "What game or toy meant everything to {name} as a child?",
    followUp: "Do you remember a specific afternoon playing it — where you were, who was there?",
    era: "Childhood",
  },
  {
    first: "Tell me about your mother or the person who raised you.",
    third: "Tell me about {name}'s mother or the person who raised them.",
    followUp: "What's one thing she did that you carry with you to this day?",
    era: "Childhood",
  },
  {
    first: "What's the most vivid memory you have from before age ten?",
    third: "What's the most vivid memory {name} has shared from before age ten?",
    followUp: "What made it stay with you all these years?",
    era: "Childhood",
  },
  {
    first: "Tell me about a place that felt like magic when you were young.",
    third: "Tell me about a place that felt like magic to {name} when they were young.",
    followUp: "What did being there feel like — what did you see, hear, smell?",
    era: "Childhood",
  },
  {
    first: "What was a family tradition you looked forward to every year?",
    third: "What was a family tradition {name} looked forward to every year?",
    followUp: "Who started it, and does it still happen today?",
    era: "Childhood",
  },

  // === School years ===
  {
    first: "Tell me about a teacher who changed you.",
    third: "Tell me about a teacher who changed {name}.",
    followUp: "What exactly did they do or say that you never forgot?",
    era: "School years",
  },
  {
    first: "What were you like in school — were you a good student?",
    third: "What was {name} like in school — were they a good student?",
    followUp: "What subject lit you up, and what did you dread?",
    era: "School years",
  },
  {
    first: "Who was your best friend when you were young, and what were you like together?",
    third: "Who was {name}'s best friend when they were young?",
    followUp: "What did you spend most of your time doing together?",
    era: "School years",
  },
  {
    first: "Tell me about a moment in your school years when you first felt different — or found where you belonged.",
    third: "Tell me about a moment in {name}'s school years when they first felt different — or found where they belonged.",
    followUp: "How did that shape who you became?",
    era: "School years",
  },
  {
    first: "What did you dream about becoming when you grew up?",
    third: "What did {name} dream about becoming when they grew up?",
    followUp: "Where did that dream come from — a person, a book, a moment?",
    era: "School years",
  },
  {
    first: "Tell me about a summer from your teenage years that you still think about.",
    third: "Tell me about a summer from {name}'s teenage years that they still talk about.",
    followUp: "What made that summer different from the others?",
    era: "School years",
  },

  // === Early adulthood ===
  {
    first: "Describe a day you felt completely alive.",
    third: "Describe a day {name} has talked about feeling most alive.",
    followUp: "Where were you, and who else was there?",
    era: "Early adulthood",
  },
  {
    first: "What was it like leaving home for the first time?",
    third: "What was it like when {name} left home for the first time?",
    followUp: "What surprised you most about being on your own?",
    era: "Early adulthood",
  },
  {
    first: "What's something you did in your twenties that surprised even yourself?",
    third: "What's something {name} did in their twenties that surprised the people around them?",
    followUp: "Where did that courage or clarity come from?",
    era: "Early adulthood",
  },
  {
    first: "Tell me about your closest friends when you were young — who were your people?",
    third: "Tell me about {name}'s closest friends when they were young — who were their people?",
    followUp: "What did you all have in common, and do you still keep in touch?",
    era: "Early adulthood",
  },
  {
    first: "What was the best adventure you ever had?",
    third: "What was the best adventure {name} ever had?",
    followUp: "What made it an adventure — what went wrong, or unexpectedly right?",
    era: "Early adulthood",
  },
  {
    first: "Tell me about the moment you first felt like an adult.",
    third: "Tell me about the moment {name} first felt like an adult.",
    followUp: "Was it something that happened, or more of a slow realization?",
    era: "Early adulthood",
  },

  // === Career ===
  {
    first: "Tell me about work that genuinely mattered to you.",
    third: "Tell me about work that genuinely mattered to {name}.",
    followUp: "What made you proud of it, even on the hard days?",
    era: "Career",
  },
  {
    first: "How did you end up in the work you did — was it a plan, or did life lead you there?",
    third: "How did {name} end up in the work they did — was it a plan, or did life lead them there?",
    followUp: "If you could go back, would you choose the same path?",
    era: "Career",
  },
  {
    first: "Tell me about a mentor or boss who changed how you thought about your work.",
    third: "Tell me about a mentor or boss who changed how {name} thought about their work.",
    followUp: "What's the one thing they taught you that you still carry?",
    era: "Career",
  },
  {
    first: "What's your proudest professional moment — something you built, solved, or led?",
    third: "What's {name}'s proudest professional moment?",
    followUp: "Why does that one stand out above the rest?",
    era: "Career",
  },
  {
    first: "Tell me about the hardest thing you ever faced in your work life.",
    third: "Tell me about the hardest thing {name} ever faced in their work life.",
    followUp: "What got you through it, and what did you learn?",
    era: "Career",
  },

  // === Love & family ===
  {
    first: "What was falling in love like the first time?",
    third: "What do you know about {name}'s first experience of falling in love?",
    followUp: "What do you remember most about that feeling?",
    era: "Love & family",
  },
  {
    first: "Tell me about the day you knew your partner was the one.",
    third: "Tell me about the day {name} knew their partner was the one.",
    followUp: "What was it — a moment, a feeling, something they said?",
    era: "Love & family",
  },
  {
    first: "Tell me about becoming a parent for the first time.",
    third: "Tell me about {name} becoming a parent for the first time.",
    followUp: "What changed in you — what surprised you most?",
    era: "Love & family",
  },
  {
    first: "What's something your family did together that you'll never forget?",
    third: "What's something {name}'s family did together that they'll never forget?",
    followUp: "Who made it happen, and what made it special?",
    era: "Love & family",
  },
  {
    first: "Tell me about a rough patch in a relationship that actually made you stronger.",
    third: "Tell me about a challenge in {name}'s most important relationship that made them stronger.",
    followUp: "What did you learn about the other person — and about yourself?",
    era: "Love & family",
  },
  {
    first: "What did love teach you that you didn't expect?",
    third: "What did love teach {name} that they didn't expect?",
    followUp: "Has your idea of what love means changed over the years?",
    era: "Love & family",
  },

  // === Later years ===
  {
    first: "What do you appreciate now that you completely took for granted when you were younger?",
    third: "What does {name} appreciate now that they took for granted when younger?",
    followUp: "Was there a specific moment you realized its value?",
    era: "Later years",
  },
  {
    first: "Tell me about the best decade of your life so far — and why.",
    third: "Tell me about the best decade of {name}'s life so far — and why.",
    followUp: "What was it about that time that made everything feel right?",
    era: "Later years",
  },
  {
    first: "Is there a regret you've made peace with? Tell me about it.",
    third: "Is there a regret {name} has made peace with? Tell me about it.",
    followUp: "How did you come to peace with it — what shifted?",
    era: "Later years",
  },
  {
    first: "What does a really good day look like for you now?",
    third: "What does a really good day look like for {name} now?",
    followUp: "Is that different from what a good day looked like twenty years ago?",
    era: "Later years",
  },
  {
    first: "Looking back on your life, what are you most proud of?",
    third: "Looking back on {name}'s life, what do you think they're most proud of?",
    followUp: "Is it what you expected to be proud of, back when you were starting out?",
    era: "Later years",
  },

  // === Wisdom ===
  {
    first: "What's a lesson you learned the hard way?",
    third: "What's a hard lesson {name} has shared with you?",
    followUp: "Looking back now — what would you tell someone going through the same thing?",
    era: "Wisdom",
  },
  {
    first: "What do you believe now that you didn't believe at thirty?",
    third: "What does {name} believe now that they didn't at thirty?",
    followUp: "What changed your mind — a person, an experience, just time?",
    era: "Wisdom",
  },
  {
    first: "What's the one thing you wish young people understood?",
    third: "What's the one thing {name} wishes young people understood?",
    followUp: "Did someone try to tell you this when you were young? Would you have listened?",
    era: "Wisdom",
  },
  {
    first: "What gets easier with age — and what never does?",
    third: "What does {name} say gets easier with age — and what never does?",
    followUp: "When did you first notice the things getting easier starting to shift?",
    era: "Wisdom",
  },
  {
    first: "If you could sit down with your younger self for an hour, what would you say?",
    third: "If {name} could sit down with their younger self for an hour, what would they say?",
    followUp: "Do you think your younger self would have listened?",
    era: "Wisdom",
  },
  {
    first: "What does a good life look like — not success, not achievement, just a good life?",
    third: "What does {name} think a good life looks like — not success, just a good life?",
    followUp: "Has your answer to that question changed over the years?",
    era: "Wisdom",
  },

  // === Other ===
  {
    first: "Tell me about a place that shaped who you are.",
    third: "Tell me about a place that shaped who {name} is.",
    followUp: "What did being there teach you?",
    era: "Other",
  },
  {
    first: "Is there a story about your name — your first name, your family name, or a nickname?",
    third: "Is there a story about {name}'s name — their given name, their family name, or a nickname they had?",
    followUp: "What does that name mean to you now?",
    era: "Other",
  },
  {
    first: "What's something about you that would surprise most people?",
    third: "What's something about {name} that would surprise most people?",
    followUp: "Is that a part of yourself you've always known was there, or did you discover it later?",
    era: "Other",
  },
];

export function getPrompts(isFirstPerson, personName, { era } = {}) {
  const pool = era
    ? RAW_PROMPTS.filter(p => p.era === era)
    : RAW_PROMPTS;
  const source = pool.length ? pool : RAW_PROMPTS;
  return source.map((p) => ({
    question: isFirstPerson
      ? p.first
      : p.third.replace(/\{name\}/g, personName || "them"),
    followUp: p.followUp,
    era: p.era,
  }));
}

const AI_BRIDGES = [
  "That's such a vivid picture.",
  "I love how you describe that.",
  "What a detail to hold onto.",
  "You paint it so clearly.",
  "There's something so alive in what you shared.",
  "That stays with you, doesn't it.",
  "I can almost picture it.",
];

const SHORT_RESPONSES = [
  "Take your time — even a single image or word is worth keeping.",
  "Whatever comes to mind first is just fine. There's no right answer.",
  "There's no rush here. Just whatever feels natural.",
];

export function aiFollowUpResponse(firstAnswer, followUpQuestion) {
  const len = firstAnswer.trim().length;
  if (len < 25) {
    const resp = SHORT_RESPONSES[Math.floor(Math.random() * SHORT_RESPONSES.length)];
    return `${resp}\n\n${followUpQuestion}`;
  }
  const bridge = AI_BRIDGES[Math.floor(Math.random() * AI_BRIDGES.length)];
  return `${bridge}\n\n${followUpQuestion}`;
}

export function aiReflection(answers, isFirstPerson, personName) {
  const combined = answers.join(" ").trim();
  const len = combined.length;
  const possessive = isFirstPerson ? "your" : `${personName || "their"}'s`;

  if (len < 50) {
    return `Even these few words carry something real. ${isFirstPerson ? "Your" : `${personName}'s`} family will be so glad this was written down.`;
  }
  if (len < 180) {
    return `There's genuine warmth in what you've shared. These are the kinds of details that stay with families — the ones that get passed down for generations.`;
  }
  return `Thank you for trusting this with me. The texture of what you've described — the specific details, the feeling — that's exactly what ${possessive} family will treasure most.`;
}

const EVENT_ERAS = ["Childhood", "School years", "Early adulthood", "Career", "Love & family", "Later years"];

export function aiPeriodQuestion(isFirstPerson, personName, era) {
  const possessive = isFirstPerson ? "your" : `${personName}'s`;
  if (EVENT_ERAS.includes(era)) {
    return `Do you remember roughly when this happened — the year, or what stage of ${possessive} life it was?`;
  }
  return `What period of ${possessive} life does this wisdom come from most?`;
}

function extractTitleFromAnswer(text) {
  const cleaned = text.trim().replace(/^(well[,.]?\s*|so[,.]?\s*|um+[,.]?\s*|uh+[,.]?\s*|oh[,.]?\s*|you know[,.]?\s*)/i, "");
  const firstSentence = cleaned.split(/[.!?]/)[0].trim();
  if (firstSentence.length >= 6 && firstSentence.length <= 65) return firstSentence;
  const words = firstSentence.split(/\s+/).slice(0, 9).join(" ");
  return words.length >= 4 ? words + "…" : "";
}

export function draftMemoryFromAnswers(prompt, answers) {
  const combined = answers.filter(Boolean).join("\n\n");
  const body = combined.trim() || "A memory waiting to be filled in.";
  const excerpt = body.length > 160 ? `${body.slice(0, 157)}…` : body;

  // Prefer a title extracted from the first substantive answer over the prompt question
  const firstAnswer = answers.find(a => a && a.trim().length > 15) || "";
  const fromAnswer  = firstAnswer ? extractTitleFromAnswer(firstAnswer) : "";
  const fromPrompt  = (() => {
    const raw = prompt.question.replace(/\?$/, "");
    return raw.length > 50 ? `${raw.slice(0, 47)}…` : raw;
  })();
  const title = fromAnswer || fromPrompt;

  return { title, body, excerpt, era: prompt.era };
}
