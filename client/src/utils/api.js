import { AI_TIMEOUT_MS, DEFAULT_TIMEOUT_MS, fetchJson } from './httpClient'

const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080'

async function authFetch(path, getAccessTokenSilently, options = {}) {
  const token = await getAccessTokenSilently()
  return fetchJson(`${API_BASE_URL}${path}`, {
    ...options,
    headers: {
      ...(options.headers || {}),
      Authorization: `Bearer ${token}`,
    },
  })
}

export async function fetchHealth() {
  return fetchJson(`${API_BASE_URL}/api/health`, {
    timeoutMs: DEFAULT_TIMEOUT_MS,
    retries: 1,
  })
}

export async function fetchYoutubeVideos(query) {
  return fetchJson(`${API_BASE_URL}/api/youtube?query=${encodeURIComponent(query)}`, {
    timeoutMs: DEFAULT_TIMEOUT_MS,
    retries: 2,
  })
}

export async function generateCourse(topic, getAccessTokenSilently) {
  return authFetch('/api/ai/generate-course', getAccessTokenSilently, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ topic }),
    timeoutMs: AI_TIMEOUT_MS,
    retries: 2,
  })
}

export async function translateToHinglish(text, getAccessTokenSilently) {
  return authFetch('/api/ai/translate-hinglish', getAccessTokenSilently, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ text }),
    timeoutMs: AI_TIMEOUT_MS,
    retries: 2,
  })
}

export async function generateSpeech(text, getAccessTokenSilently, voiceName) {
  return authFetch('/api/ai/tts', getAccessTokenSilently, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ text, voiceName }),
    timeoutMs: AI_TIMEOUT_MS,
    retries: 1,
  })
}

export async function saveGeneratedOutline(outline, getAccessTokenSilently) {
  return authFetch('/api/courses/save-outline', getAccessTokenSilently, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(outline),
    timeoutMs: DEFAULT_TIMEOUT_MS,
    retries: 1,
  })
}

export async function fetchMyCourses(getAccessTokenSilently) {
  return authFetch('/api/courses/my', getAccessTokenSilently, {
    timeoutMs: DEFAULT_TIMEOUT_MS,
    retries: 2,
  })
}

export async function fetchMyCoursesFull(getAccessTokenSilently) {
  return authFetch('/api/courses/my/full', getAccessTokenSilently, {
    timeoutMs: DEFAULT_TIMEOUT_MS,
    retries: 2,
  })
}

export async function fetchCourseById(courseId, getAccessTokenSilently) {
  return authFetch(`/api/courses/${courseId}`, getAccessTokenSilently, {
    timeoutMs: DEFAULT_TIMEOUT_MS,
    retries: 2,
  })
}

export async function createCourse(payload, getAccessTokenSilently) {
  return authFetch('/api/courses', getAccessTokenSilently, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
    timeoutMs: DEFAULT_TIMEOUT_MS,
    retries: 1,
  })
}

export { API_BASE_URL }
