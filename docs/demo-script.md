# LearnForge — 5-Minute Demo Script

Use this script for hackathon judging, portfolio walkthroughs, or technical interviews.

**Duration:** ~5 minutes  
**Suggested demo topic:** `Intro to React Hooks`

---

## Before you start (30 seconds)

1. Open the deployed frontend (or `http://localhost:5173`).
2. Confirm you are logged in via Auth0.
3. Have MongoDB and the backend running (`AI_PROVIDER=template` works without API keys).

**Opening line:**

> "LearnForge is an AI-powered course generator. You type any topic and get a full structured course — modules, lessons, quizzes, videos, and even a Hinglish explanation — all saved to your account."

---

## Step 1 — Generate a course (1 minute)

**Screen:** Home page (`/`)

1. Point to the prompt form.
2. Enter: `Intro to React Hooks`
3. Click **Generate Course**.
4. Mention the toast: course saved successfully.
5. Show the course appear in **My Saved Courses**.

**Talking points:**

- Topic is validated (3–180 characters).
- Backend calls the AI pipeline (template/OpenAI/Gemini).
- Outline is parsed, validated (3–6 modules, 3–5 lessons), and persisted to MongoDB.
- Course is linked to the Auth0 user via JWT `sub`.

---

## Step 2 — Browse course structure (45 seconds)

**Screen:** Course page (`/course/:id`)

1. Click the saved course.
2. Walk through 3+ modules and lesson titles.

**Talking points:**

- MongoDB hierarchy: `Course → Module → Lesson`.
- Protected route — only authenticated users see saved content.
- Skeleton loaders and retry logic handle slow API responses.

---

## Step 3 — Interactive lesson (1 minute 30 seconds)

**Screen:** Lesson page

1. Open the first lesson.
2. Scroll through block types:
   - **Heading / Paragraph** — structured content
   - **Code block** — syntax-formatted snippet
   - **Video block** — YouTube search + embed
   - **MCQ blocks** — select answer, check, see explanation
3. Click **Download PDF** and show the generated file.

**Talking points:**

- `LessonRenderer` dispatches JSON blocks to dedicated React components.
- PDF uses a hidden print layout (html2canvas + jsPDF) independent of live theme.
- YouTube integration runs through a cached backend proxy to save API quota.

---

## Step 4 — Multilingual accessibility (45 seconds)

**Screen:** Hinglish panel on lesson page

1. Click **Generate Hinglish**.
2. Show the Roman-script Hindi explanation.
3. Click **Generate Audio** and play the narration.

**Talking points:**

- Gemini (or template fallback) translates lesson text to student-friendly Hinglish.
- TTS returns playable audio for accessibility and mobile learners.

---

## Step 5 — Architecture wrap-up (45 seconds)

**Optional:** Show architecture diagram from README or quick code tour.

**Closing points:**

| Layer | Tech |
| --- | --- |
| Frontend | React, Vite, React Router, Auth0 SDK |
| Backend | Spring Boot, Spring Security JWT, Spring Data MongoDB |
| AI | OpenAI / Gemini with schema validation + retries |
| Integrations | YouTube Data API, PDF export, Hinglish TTS |
| Deploy | Vercel (client) + Render (Docker backend) + MongoDB Atlas |

**Closing line:**

> "The full stack is production-ready — Auth0-secured APIs, persistent MongoDB storage, tested parsers, deployment configs, and a responsive UI with error handling built in."

---

## Backup talking points (if asked)

- **Why Spring Boot over MERN?** Type-safe DTOs, centralized validation, enterprise-grade security, easy MongoDB integration.
- **How is AI output trusted?** Strict JSON parsing, Jakarta Bean Validation, custom rules (MCQ count, module/lesson bounds).
- **What if AI fails?** HTTP retries, frontend timeout/retry, toast error feedback, template provider for offline demos.
- **Scaling?** Stateless API on Render, MongoDB Atlas, cached YouTube responses, CDN-hosted Vercel frontend.

---

## Troubleshooting during live demo

| Issue | Quick fix |
| --- | --- |
| Auth0 redirect error | Use `http://localhost:5173` in Auth0 callback URLs |
| Generate button does nothing | Confirm login + backend running on port 8080 |
| No YouTube video | Set `YOUTUBE_API_KEY` or explain template/demo mode |
| Slow first request on Render free tier | Mention cold start (~30s wake time) |
