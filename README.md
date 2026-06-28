# Text-to-Learn (LearnForge)

AI-powered course generator that turns a free-form topic prompt into a structured learning path with modules, lessons, rich content blocks, video suggestions, multilingual support, and PDF export.

## Monorepo Layout

```text
LearnForge/
├── client/   # React + Vite frontend
├── server/   # Spring Boot backend
└── docs/     # Planning and execution roadmap
```

## Tech Stack (Spring Boot + React)

- Frontend: React, Vite, React Router (UI framework to be added in later chunks)
- Backend: Spring Boot, Spring Security (Auth0 JWT), Spring Data MongoDB
- AI/Text Generation: Gemini/OpenAI/Hugging Face via backend service layer
- Integrations: YouTube Data API, PDF generation, multilingual translation + TTS
- Deployment: Vercel (client) + Render (server)
- Workflow: GitHub feature branches, PR-first commits, CI/CD via GitHub Actions

## Hackathon Core Goal

Input a topic such as `Intro to React Hooks` and generate:

- Course title and description
- 3-6 modules with 3-5 lessons each
- Per-lesson objectives, key topics, references, MCQs, and optional code/video blocks
- Persistent storage for generated content
- A navigable frontend experience for browsing and learning

## Chunked Delivery Plan

The full implementation is split into clean development chunks to keep commit history readable and interview-friendly.

- Chunk 1 (current): monorepo bootstrap + initial docs + base scaffolding
- Chunk 2+: backend foundation, frontend routing, Auth0, AI pipelines, rendering, integrations, deployment, and final showcase docs

See `docs/roadmap.md` for the complete milestone-to-implementation plan.

## Local Setup (after Chunk 1)

### Client

```bash
cd client
npm install
npm run dev
```

### Server

```bash
cd server
./mvnw spring-boot:run
```

## Notes

- This repository intentionally follows incremental chunk delivery.
- No commits are auto-created by the setup process; commits should be done chunk-by-chunk manually.
