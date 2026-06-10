// Prompts have two forms: first-person (storyteller IS the subject)
// and third-person (family organizer capturing someone else's story).
const RAW_PROMPTS = [
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
    first: "Describe a day you felt completely alive.",
    third: "Describe a day {name} has talked about feeling most alive.",
    followUp: "Where were you, and who else was there?",
    era: "Early adulthood",
  },
  {
    first: "What was falling in love like the first time?",
    third: "What do you know about {name}'s first experience of falling in love?",
    followUp: "What do you remember most about that feeling?",
    era: "Love & family",
  },
  {
    first: "Tell me about work that genuinely mattered to you.",
    third: "Tell me about work that genuinely mattered to {name}.",
    followUp: "What made you proud of it, even on the hard days?",
    era: "Career",
  },
  {
    first: "What's a lesson you learned the hard way?",
    third: "What's a hard lesson {name} has shared with you?",
    followUp: "Looking back now — what would you tell someone going through the same thing?",
    era: "Wisdom",
  },
  {
    first: "Tell me about a place that shaped who you are.",
    third: "Tell me about a place that shaped who {name} is.",
    followUp: "What did being there teach you?",
    era: "Childhood",
  },
  {
    first: "What's something you did that surprised even yourself?",
    third: "What's something {name} did that surprised the people around them?",
    followUp: "Where did that courage or clarity come from?",
    era: "Early adulthood",
  },
];

export function getPrompts(isFirstPerson, personName) {
  return RAW_PROMPTS.map((p) => ({
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

export function draftMemoryFromAnswers(prompt, answers) {
  const combined = answers.filter(Boolean).join("\n\n");
  const rawTitle = prompt.question.replace(/\?$/, "");
  const title = rawTitle.length > 50 ? `${rawTitle.slice(0, 47)}…` : rawTitle;
  const body = combined.trim() || "A memory waiting to be filled in.";
  const excerpt = body.length > 160 ? `${body.slice(0, 157)}…` : body;
  return { title, body, excerpt, era: prompt.era };
}
