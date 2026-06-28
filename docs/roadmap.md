# LearnForge Roadmap (Spring Boot + React)

This document translates the provided hackathon script into an implementation roadmap using **Spring Boot + React** (instead of MERN), while preserving all requested features and milestones.

## Problem Statement

Build an AI-powered full-stack web app that converts any user topic prompt into a complete, structured online course.

### Required output per prompt

- Course title and description
- 3-6 modules
- 3-5 lessons per module
- Per lesson:
  - objectives
  - key topics
  - suggested readings/external links
  - structured content blocks (heading/paragraph/code/video/MCQ)

### Core objectives

- End-to-end full-stack implementation
- AI/template-based generation pipeline
- Persistent storage of generated structures
- Clean responsive UI
- Cloud deployment with CI/CD

## Product Features (from brief)

1. Prompt to Course generation
2. Rich lesson rendering (text/code/video/quiz)
3. Download lesson as PDF
4. Multilingual explanations (Hinglish)
5. Secure login (Auth0)
6. Persistent user-linked courses

## User Flow

- `/` Home: topic input + course listing
- Auth flow: Auth0 login/logout redirects
- `/course/:id`: module/lesson overview
- `/lesson/:id` (or nested route): detailed lesson viewer + export/playback options

Key UI components:

- `PromptForm`
- `SidebarNavigation`
- `LessonRenderer`
- `DownloadButton` / `LessonPDFExporter`
- `LoadingSpinner`
- `ErrorMessage`

## Final Architecture (Adjusted Stack)

- Frontend: React + Vite + React Router
- Backend: Spring Boot REST API
- Database: MongoDB
- Auth: Auth0 (React SDK + Spring Security JWT validation)
- AI: Gemini/OpenAI/Hugging Face through backend service
- Video: YouTube Data API v3
- PDF: `html2canvas` + `jsPDF`
- Deployment: Render (backend), Vercel (frontend)
- CI/CD: GitHub Actions

## Milestone-by-Milestone Plan

## Milestone 1: Conception and Prototype

- Define product scope and user journey.
- Confirm core value: fast structured self-learning from free-form prompts.
- Confirm enhancements: persistence, offline PDF, multilingual accessibility.

## Milestone 2: Backend Setup (Spring Boot equivalent)

- Initialize modular backend folders/packages:
  - `config`, `controller`, `service`, `repository`, `model`, `dto`, `exception`, `security`, `util`
- Add centralized error handling and request validation.
- Add environment-based configuration.

## Milestone 3: Frontend Setup

- React + Vite bootstrap.
- Project folders:
  - `components`, `pages`, `hooks`, `context`, `utils`
- Add route-ready app shell.

## Milestone 4: Authentication (Auth0)

- Frontend:
  - Auth0 provider wrapper
  - login/logout
  - protected route gating
- Backend:
  - Validate Bearer JWT via Auth0 issuer/audience
  - attach user metadata/subject to request context
- Protect save/list user-specific course APIs.

## Milestone 5: Data Modeling

Mongo hierarchy:

- `Course`: title, description, creator(Auth0 sub), module refs, tags
- `Module`: title, course ref, lesson refs
- `Lesson`: title, objectives, structured content blocks, isEnriched, module ref

Relationship model remains:

`Course -> Module -> Lesson`

## Milestone 6: Lesson Rendering

Implement renderer and block components:

- `LessonRenderer`
- `HeadingBlock`
- `ParagraphBlock`
- `CodeBlock`
- `VideoBlock`
- `MCQBlock`

Content format supports heading/paragraph/code/video/mcq JSON blocks.

## Milestone 7: Routing + Sidebar Layout

- Route shell with sidebar/topbar and nested page rendering.
- Support course and lesson navigation paths.
- Add loading and error handling states for API workflows.

## Milestone 8: AI Prompt Design

Two-stage prompt pipeline:

1. `generateCoursePrompt(topic)`
   - Returns strict JSON for title, description, tags, modules, lesson titles.
2. `generateLessonPrompt(course, module, lesson)`
   - Returns strict JSON with objectives + content blocks.

Rules to enforce:

- raw JSON only (no markdown wrappers)
- video returned as search query block
- code block only when contextually relevant
- include 4-5 MCQs at the end with answer explanations

## Milestone 9: YouTube Integration

- Parse lesson `video` query block.
- Backend endpoint fetches embeddable top results via YouTube Data API v3.
- Frontend embeds selected video.
- Optional response caching to conserve API quota.

## Milestone 10: Hinglish Translation + TTS

- Use Gemini for:
  - translation to Hinglish
  - TTS audio generation
- Return playable/downloadable audio buffers.
- Enable via user preference/lesson toggle.

## Milestone 11: PDF Export

- Use hidden render section + React ref for consistent export style.
- Capture with `html2canvas`, convert with `jsPDF`.
- Preserve formatting for text, code, and MCQs.

## Milestone 12: Deployment + CI/CD

Backend (Render):

- env: `PORT`, `MONGO_URI`, `AUTH0_ISSUER`, `AUTH0_AUDIENCE`, `GEMINI_API_KEY`, `YOUTUBE_API_KEY`

Frontend (Vercel):

- env: `VITE_AUTH0_DOMAIN`, `VITE_AUTH0_CLIENT_ID`, `VITE_API_URL`

CI/CD:

- GitHub Actions for build/test/deploy checks
- Feature branch + PR workflow

## Milestone 13: Showcase + Documentation

- Polished README with setup, architecture, features, and demo flow.
- 5-minute demonstration script.
- Resume-ready technical bullet points.

## Planned Chunking for Clean Git History

1. Bootstrap monorepo and base docs
2. Backend modular foundation
3. Frontend routing foundation
4. Data models and repositories
5. Auth0 integration
6. Course-generation AI endpoint
7. Lesson-generation AI endpoint
8. Course persistence and retrieval APIs
9. Lesson renderer components
10. App shell, sidebar, robust states
11. YouTube integration
12. Hinglish + TTS
13. PDF export
14. Reliability and UX polish
15. Tests + CI/CD
16. Deployment configuration
17. Final showcase documentation
