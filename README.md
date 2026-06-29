# LearnForge (Text-to-Learn)

AI-powered full-stack web app that turns any topic prompt into a structured online course with modules, lessons, interactive quizzes, video suggestions, Hinglish explanations, and PDF export.

**Live stack:** React + Vite · Spring Boot · MongoDB · Auth0 · OpenAI/Gemini · YouTube Data API

---

## Features

| Feature | Description |
| --- | --- |
| **Prompt → Course** | Generate a course outline (3–6 modules, 3–5 lessons each) from a free-form topic |
| **Rich lesson viewer** | Render heading, paragraph, code, video, and MCQ blocks from structured JSON |
| **Auth0 login** | Secure SPA login with JWT-protected backend APIs |
| **Persistent courses** | Save and retrieve user-linked courses in MongoDB |
| **YouTube integration** | Fetch and embed recommended videos from lesson search queries |
| **Hinglish + TTS** | Generate student-friendly Hinglish explanations and audio narration |
| **Suggested resources** | Each lesson includes a list of suggested readings / external links |
| **PDF export** | Download lessons as print-ready PDFs via html2canvas + jsPDF |
| **UX polish** | Skeleton loaders, toasts, retries, input validation, responsive layout |

---

## Architecture

```text
┌─────────────────┐     HTTPS/JWT      ┌──────────────────────┐
│  React + Vite   │ ◄────────────────► │  Spring Boot API     │
│  (Vercel)       │                    │  (Render + Docker)   │
└────────┬────────┘                    └──────────┬───────────┘
         │                                        │
    Auth0 SPA SDK                          Spring Security
         │                                        │
         ▼                                        ▼
┌─────────────────┐                    ┌──────────────────────┐
│  Auth0 Tenant   │                    │  MongoDB Atlas       │
└─────────────────┘                    └──────────────────────┘
                                                  │
                              ┌───────────────────┼───────────────────┐
                              ▼                   ▼                   ▼
                         OpenAI/Gemini      YouTube API v3      Template AI
```

### Data model

```text
Course → Module → Lesson
```

Each **Lesson** stores objectives, a JSON array of content blocks (`heading`, `paragraph`, `code`, `video`, `mcq`), and a `resources` list of suggested readings / external links (`{ title, url }`).

### Monorepo layout

```text
LearnForge/
├── client/          # React frontend (Vite)
│   ├── src/
│   │   ├── components/   # UI, blocks, PDF exporter
│   │   ├── pages/        # Home, Course, Lesson
│   │   ├── hooks/        # useAuth, useAsync
│   │   ├── context/      # App, Toast, Auth0
│   │   └── utils/        # API client, validation, routes
│   └── vercel.json
├── server/          # Spring Boot backend
│   ├── src/main/java/com/learnforge/server/
│   │   ├── controller/   # REST endpoints
│   │   ├── service/      # AI, courses, YouTube, multilingual
│   │   ├── repository/   # MongoDB repositories
│   │   ├── security/     # Auth0 JWT validation
│   │   └── dto/          # Request/response models
│   └── Dockerfile
├── docs/
│   ├── roadmap.md
│   ├── deployment.md
│   ├── demo-script.md
│   └── resume-bullets.md
└── render.yaml
```

---

## Quick start (local)

### Prerequisites

- Node.js 20+
- Java 21+
- MongoDB (local or Atlas)
- Auth0 tenant with SPA + API configured

### 1. Clone and configure env

```bash
git clone https://github.com/Ajaysingh319/Learn_Forge.git
cd Learn_Forge

cp server/.env.example server/.env    # fill in values
cp client/.env.example client/.env    # fill in values
```

### 2. Start the backend

```bash
cd server
./mvnw spring-boot:run
```

API runs at `http://localhost:8080`. Health check: `GET /api/health`.

### 3. Start the frontend

```bash
cd client
npm install
npm run dev
```

App runs at `http://localhost:5173`.

### 4. Run tests

```bash
# Frontend
cd client && npm test && npm run lint && npm run build

# Backend (requires Java 21 + MongoDB)
cd server && ./mvnw test
```

---

## Environment variables

### Backend (`server/.env`)

| Variable | Description |
| --- | --- |
| `MONGO_URI` | MongoDB connection string |
| `AUTH0_ISSUER` | Auth0 issuer URL |
| `AUTH0_AUDIENCE` | Auth0 API audience |
| `CORS_ALLOWED_ORIGINS` | Frontend origin(s), comma-separated |
| `AI_PROVIDER` | `template`, `openai`, or `gemini` |
| `OPENAI_API_KEY` | Required when `AI_PROVIDER=openai` |
| `GEMINI_API_KEY` | Required when `AI_PROVIDER=gemini` |
| `YOUTUBE_API_KEY` | YouTube Data API key for video blocks |

See `server/.env.example` for the full list.

### Frontend (`client/.env`)

| Variable | Description |
| --- | --- |
| `VITE_API_URL` | Backend base URL |
| `VITE_AUTH0_DOMAIN` | Auth0 tenant domain |
| `VITE_AUTH0_CLIENT_ID` | Auth0 SPA client ID |
| `VITE_AUTH0_AUDIENCE` | Must match backend audience |

---

## API overview

| Method | Endpoint | Auth | Description |
| --- | --- | --- | --- |
| `GET` | `/api/health` | Public | Service health |
| `GET` | `/api/auth/me` | JWT | Current user claims |
| `POST` | `/api/ai/generate-course` | JWT | Generate course outline |
| `POST` | `/api/ai/generate-lesson` | JWT | Generate lesson content |
| `POST` | `/api/ai/translate-hinglish` | JWT | Hinglish translation |
| `POST` | `/api/ai/tts` | JWT | Text-to-speech audio |
| `POST` | `/api/courses/save-outline` | JWT | Persist generated outline |
| `GET` | `/api/courses/my` | JWT | List user's course summaries |
| `GET` | `/api/courses/{id}` | JWT | Fetch full course |
| `GET` | `/api/youtube?query=...` | Public | YouTube search results |

---

## User flow

1. **Home (`/`)** — Enter a topic, generate and save a course (login required).
2. **Course (`/course/:id`)** — Browse modules and lesson links.
3. **Lesson (`/courses/:courseId/module/:m/lesson/:l`)** — Read content, take MCQs, watch videos, export PDF, generate Hinglish/audio.

---

## Deployment

Production deployment uses **Render** (backend) + **Vercel** (frontend) + **MongoDB Atlas**.

Full instructions: [`docs/deployment.md`](docs/deployment.md)

### CI/CD

GitHub Actions ([`.github/workflows/ci.yml`](.github/workflows/ci.yml)) runs on every push and pull request to `main`:

- **Client** — `npm ci` → lint → test → build
- **Server** — JDK 21 → `./mvnw verify` (compile + tests)

Render (backend) and Vercel (frontend) auto-deploy from `main` once CI passes.

---

## Demo script

5-minute walkthrough for interviews and hackathon demos: [`docs/demo-script.md`](docs/demo-script.md)

---

## Resume bullets

Copy-ready technical highlights: [`docs/resume-bullets.md`](docs/resume-bullets.md)

---

## Development history

The project was built in 17 incremental chunks for clean git history. See [`docs/roadmap.md`](docs/roadmap.md) for the full milestone plan.

---

## License

MIT (or update as needed for your submission).
