# Deployment Guide

LearnForge deploys as a split stack:

- **Backend:** Render (Dockerized Spring Boot API)
- **Frontend:** Vercel (React + Vite SPA)
- **Database:** MongoDB Atlas (recommended for production)

## Prerequisites

1. MongoDB Atlas cluster with a connection string
2. Auth0 tenant with a Single Page Application (SPA)
3. Optional API keys:
   - `OPENAI_API_KEY` or `GEMINI_API_KEY` for live AI generation
   - `YOUTUBE_API_KEY` for lesson video search

## Environment Variables

### Backend (Render)

| Variable | Required | Description |
| --- | --- | --- |
| `PORT` | Yes | Render sets this automatically; default `8080` locally |
| `MONGO_URI` | Yes | MongoDB connection string |
| `AUTH0_ISSUER` | Yes | Auth0 issuer URL, e.g. `https://YOUR_TENANT.auth0.com/` |
| `AUTH0_AUDIENCE` | Yes | Auth0 API audience, e.g. `https://learnforge-api` |
| `CORS_ALLOWED_ORIGINS` | Yes | Comma-separated frontend origins |
| `AI_PROVIDER` | No | `template`, `openai`, or `gemini` (default: `template`) |
| `OPENAI_API_KEY` | If using OpenAI | OpenAI API key |
| `GEMINI_API_KEY` | If using Gemini | Gemini API key |
| `YOUTUBE_API_KEY` | No | YouTube Data API key for video blocks |

Copy `server/.env.example` as a starting point for local development.

### Frontend (Vercel)

| Variable | Required | Description |
| --- | --- | --- |
| `VITE_API_URL` | Yes | Public backend URL, e.g. `https://learnforge-api.onrender.com` |
| `VITE_AUTH0_DOMAIN` | Yes | Auth0 tenant domain |
| `VITE_AUTH0_CLIENT_ID` | Yes | Auth0 SPA client ID |
| `VITE_AUTH0_AUDIENCE` | Yes | Same audience as backend |

Copy `client/.env.example` for local development.

## Auth0 Production Settings

In the Auth0 SPA application settings, add your deployed frontend URL:

- **Allowed Callback URLs:** `https://YOUR_VERCEL_APP.vercel.app`
- **Allowed Logout URLs:** `https://YOUR_VERCEL_APP.vercel.app`
- **Allowed Web Origins:** `https://YOUR_VERCEL_APP.vercel.app`

Keep `http://localhost:5173` entries for local development.

Create an Auth0 API with the identifier matching `AUTH0_AUDIENCE` / `VITE_AUTH0_AUDIENCE`.

## Deploy Backend (Render)

### Option A: Blueprint (`render.yaml`)

1. Push this repository to GitHub.
2. In Render, choose **New → Blueprint** and connect the repo.
3. Render reads `render.yaml` and creates the `learnforge-api` web service.
4. Set secret environment variables in the Render dashboard:
   - `MONGO_URI`
   - `AUTH0_ISSUER`
   - `AUTH0_AUDIENCE`
   - `CORS_ALLOWED_ORIGINS` → your Vercel URL, e.g. `https://learnforge.vercel.app,http://localhost:5173`
   - API keys as needed

### Option B: Manual Docker Web Service

1. Create a **Web Service** on Render.
2. Connect the GitHub repo.
3. Set **Root Directory** to `server`.
4. Choose **Docker** as the runtime (uses `server/Dockerfile`).
5. Set **Health Check Path** to `/api/health`.
6. Add the backend environment variables listed above.

After deploy, note the public URL (example: `https://learnforge-api.onrender.com`).

## Deploy Frontend (Vercel)

1. Import the GitHub repo into Vercel.
2. Set **Root Directory** to `client`.
3. Vercel detects Vite via `client/vercel.json`.
4. Add frontend environment variables:
   - `VITE_API_URL` → Render backend URL
   - `VITE_AUTH0_DOMAIN`
   - `VITE_AUTH0_CLIENT_ID`
   - `VITE_AUTH0_AUDIENCE`
5. Deploy.

The `vercel.json` rewrite rule sends all routes to `index.html` so React Router works on refresh.

## CORS

The backend reads `CORS_ALLOWED_ORIGINS` as a comma-separated list:

```text
https://learnforge.vercel.app,http://localhost:5173
```

For Vercel preview deployments, you can add a pattern-style origin if needed:

```text
https://*.vercel.app,http://localhost:5173
```

Redeploy the backend after updating CORS.

## Smoke Test Checklist

1. Open the Vercel frontend URL.
2. Confirm Auth0 login/logout works.
3. Generate a course from the home page.
4. Open a saved course and lesson.
5. Hit `GET /api/health` on the Render backend and verify `status: UP`.

## Local Docker Backend (optional)

```bash
cd server
docker build -t learnforge-api .
docker run --rm -p 8080:8080 \
  -e MONGO_URI="mongodb://host.docker.internal:27017/learnforge" \
  -e AUTH0_ISSUER="https://YOUR_TENANT.auth0.com/" \
  -e AUTH0_AUDIENCE="https://learnforge-api" \
  -e CORS_ALLOWED_ORIGINS="http://localhost:5173" \
  learnforge-api
```

## Troubleshooting

| Issue | Fix |
| --- | --- |
| Auth0 login fails on production | Verify callback/logout/web origin URLs in Auth0 |
| API calls blocked by CORS | Add Vercel URL to `CORS_ALLOWED_ORIGINS` and redeploy backend |
| 401 on protected routes | Ensure `VITE_AUTH0_AUDIENCE` matches backend `AUTH0_AUDIENCE` |
| Render service sleeps (free tier) | First request after idle may take ~30s to wake |
| YouTube blocks missing | Set `YOUTUBE_API_KEY` or expect video placeholders only |
