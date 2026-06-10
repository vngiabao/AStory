import { ERAS } from "./store.js";

function escapeHtml(str) {
  return str
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;");
}

function formatDate(iso) {
  return new Date(iso).toLocaleDateString(undefined, {
    month: "long",
    day: "numeric",
    year: "numeric",
  });
}

export function printLegacy(state) {
  const name =
    state.profile.path === "preserve" && state.profile.subjectName
      ? state.profile.subjectName
      : state.profile.name || "A Story";

  const grouped = ERAS.map((era) => ({
    era,
    items: state.memories.filter((m) => m.era === era),
  })).filter((g) => g.items.length);

  const uncategorized = state.memories.filter((m) => !ERAS.includes(m.era));
  if (uncategorized.length) grouped.push({ era: "Life", items: uncategorized });

  const chaptersHtml = grouped.length
    ? grouped
        .map(
          (g) => `
        <section class="print-chapter">
          <h2>${escapeHtml(g.era)}</h2>
          ${g.items
            .map(
              (m) => `
            <article class="print-memory">
              <h3>${escapeHtml(m.title)}</h3>
              <p class="print-date">${formatDate(m.createdAt)}</p>
              <div class="print-body">${escapeHtml(m.body).replace(/\n/g, "<br>")}</div>
            </article>`
            )
            .join("")}
        </section>`
        )
        .join("")
    : `<p class="print-empty">No memories yet. Return to the app and share your story — then print again.</p>`;

  const win = window.open("", "_blank", "noopener,noreferrer");
  if (!win) {
    alert("Please allow pop-ups to print your legacy, or try again.");
    return;
  }

  win.document.write(`<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <title>${escapeHtml(name)} — Legacy</title>
  <style>
    * { box-sizing: border-box; margin: 0; padding: 0; }
    body {
      font-family: Georgia, "Times New Roman", serif;
      font-size: 14pt;
      line-height: 1.65;
      color: #1c1b18;
      padding: 0.75in;
      max-width: 7in;
      margin: 0 auto;
    }
    .print-header {
      text-align: center;
      margin-bottom: 2rem;
      padding-bottom: 1.5rem;
      border-bottom: 2px solid #1f6b5c;
    }
    .print-header h1 {
      font-size: 28pt;
      font-weight: normal;
      color: #1f6b5c;
      margin-bottom: 0.35rem;
    }
    .print-header .subtitle {
      font-size: 12pt;
      color: #4a4843;
      font-style: italic;
    }
    .print-tagline {
      margin-top: 1rem;
      font-size: 11pt;
      color: #5c5650;
    }
    .print-chapter { margin-bottom: 2rem; page-break-inside: avoid; }
    .print-chapter h2 {
      font-size: 18pt;
      color: #1f6b5c;
      margin-bottom: 1rem;
      border-bottom: 1px solid #ccc;
      padding-bottom: 0.35rem;
    }
    .print-memory {
      margin-bottom: 1.5rem;
      page-break-inside: avoid;
    }
    .print-memory h3 { font-size: 14pt; margin-bottom: 0.25rem; }
    .print-date { font-size: 10pt; color: #5c5650; margin-bottom: 0.75rem; }
    .print-body { font-size: 12pt; }
    .print-footer {
      margin-top: 3rem;
      padding-top: 1rem;
      border-top: 1px solid #ccc;
      font-size: 10pt;
      color: #5c5650;
      text-align: center;
    }
    .print-empty { font-style: italic; color: #5c5650; }
    @media print {
      body { padding: 0.5in; }
    }
  </style>
</head>
<body>
  <header class="print-header">
    <h1>${escapeHtml(name)}</h1>
    <p class="subtitle">A Story — Life legacy for your family</p>
    <p class="print-tagline">Printed ${formatDate(new Date().toISOString())}</p>
  </header>
  ${chaptersHtml}
  <footer class="print-footer">
    <p>Every life holds a story worth preserving. — A Story</p>
  </footer>
  <script>window.onload = function() { window.print(); }<\/script>
</body>
</html>`);
  win.document.close();
}
