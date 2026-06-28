# LearnForge — Resume Bullet Points

Copy and adapt these for resumes, LinkedIn, or portfolio descriptions.

---

## Full-stack summary (1–2 lines)

> Built **LearnForge**, an AI-powered course generator that converts free-form topic prompts into structured multi-module courses with interactive lessons, quizzes, video embeds, Hinglish accessibility, and PDF export — using **React**, **Spring Boot**, **MongoDB**, and **Auth0**.

---

## Technical bullets (pick 4–6)

- Architected a **Spring Boot REST API** with modular packages (controller/service/repository/security), centralized exception handling, and **Auth0 JWT** resource-server validation for user-scoped course APIs.

- Designed a **MongoDB document hierarchy** (`Course → Module → Lesson`) with Spring Data MongoDB repositories and ownership checks on all protected read/write endpoints.

- Implemented a **two-stage AI generation pipeline** (course outline + lesson content) with strict JSON parsing, Jakarta Bean Validation, and custom schema rules (3–6 modules, 4–5 MCQs per lesson).

- Integrated **OpenAI and Gemini** providers behind a pluggable service layer with HTTP retry logic, configurable timeouts, and a template fallback for demo/offline mode.

- Built a **React + Vite SPA** with React Router, Auth0 SDK, protected routes, skeleton loaders, toast notifications, and a block-based `LessonRenderer` for heading/paragraph/code/video/MCQ content.

- Added **YouTube Data API v3** integration with backend response caching, plus client-side iframe embedding for lesson video blocks.

- Delivered **Hinglish translation and TTS** endpoints using Gemini, with a frontend panel for accessible multilingual lesson explanations.

- Implemented **client-side PDF export** using html2canvas and jsPDF with a hidden print layout for consistent offline lesson downloads.

- Wrote **unit and integration tests** (JUnit/Mockito for backend parsers and controllers; Vitest/Testing Library for frontend utilities and forms).

- Configured **production deployment** with Dockerized Spring Boot on Render, Vite SPA on Vercel, MongoDB Atlas, and environment-driven CORS/Auth0 configuration.

---

## Skills tags (for ATS / keyword sections)

`React` · `Vite` · `JavaScript` · `Spring Boot` · `Java` · `MongoDB` · `REST APIs` · `Auth0` · `JWT` · `OpenAI API` · `Gemini` · `YouTube API` · `Docker` · `Render` · `Vercel` · `Git` · `JUnit` · `Vitest` · `Full-Stack Development` · `AI Integration`

---

## Project one-liner (for project list)

**LearnForge** — AI course generator | React, Spring Boot, MongoDB, Auth0, OpenAI/Gemini | github.com/Ajaysingh319/Learn_Forge

---

## Interview story (STAR format, ~30 seconds)

**Situation:** Hackathon required building an AI tool that turns any topic into a complete online course.

**Task:** Deliver end-to-end full-stack functionality — generation, persistence, rich UI, auth, and extras (PDF, multilingual, video).

**Action:** Built a Spring Boot + React monorepo with Auth0-secured APIs, MongoDB persistence, validated AI JSON pipelines, block-based lesson rendering, and cloud deployment configs.

**Result:** Users can log in, generate structured courses from a single prompt, browse modules/lessons, interact with MCQs, export PDFs, and access Hinglish explanations — with tested, deployable code and clean incremental git history.
