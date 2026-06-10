# A Story — App

Interactive prototype of **A Story**: preserve family life stories through warm conversations, memory cards, a timeline, and a private family archive.

## Run locally

```powershell
cd "c:\Users\nguye\Desktop\a-story-app"
npm install
npm run dev
```

Open **http://localhost:5173/**

## Features

| Screen | What it does |
|--------|----------------|
| **Onboarding** | Choose path (tell my story / preserve someone / join family) + profile setup |
| **Home** | Archive dashboard, recent memories, stats |
| **Talk** | Guided interview (2 questions + reflection) → memory card |
| **Timeline** | Memories grouped by life chapter |
| **Family** | Family orbit + invite demo |
| **Settings** | Edit profile, reset local data |

Data persists in `localStorage` (browser only).

## Related

Landing page: `c:\Users\nguye\Desktop\a-story-landing`

## Build

```powershell
npm run build
```

Deploy `dist/` to any static host.
