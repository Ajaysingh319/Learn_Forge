const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080'

async function authFetch(path, getAccessTokenSilently, options = {}) {
  const token = await getAccessTokenSilently()
  const response = await fetch(`${API_BASE_URL}${path}`, {
    ...options,
    headers: {
      ...(options.headers || {}),
      Authorization: `Bearer ${token}`,
    },
  })
  if (!response.ok) {
    throw new Error(`Request failed: ${response.status}`)
  }
  return response.json()
}

export async function fetchHealth() {
  const response = await fetch(`${API_BASE_URL}/api/health`)
  if (!response.ok) {
    throw new Error('Unable to fetch backend health')
  }
  return response.json()
}

export async function fetchYoutubeVideos(query) {
  const response = await fetch(
    `${API_BASE_URL}/api/youtube?query=${encodeURIComponent(query)}`,
  )
  if (!response.ok) {
    throw new Error(`YouTube request failed: ${response.status}`)
  }
  return response.json()
}

export async function generateCourse(topic, getAccessTokenSilently) {
  return authFetch('/api/ai/generate-course', getAccessTokenSilently, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ topic }),
  })
}

export async function translateToHinglish(text, getAccessTokenSilently) {
  return authFetch('/api/ai/translate-hinglish', getAccessTokenSilently, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ text }),
  })
}

export async function generateSpeech(text, getAccessTokenSilently, voiceName) {
  return authFetch('/api/ai/tts', getAccessTokenSilently, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ text, voiceName }),
  })
}

export async function saveGeneratedOutline(outline, getAccessTokenSilently) {
  return authFetch('/api/courses/save-outline', getAccessTokenSilently, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(outline),
  })
}

export async function fetchMyCourses(getAccessTokenSilently) {
  return authFetch('/api/courses/my', getAccessTokenSilently)
}

export async function fetchMyCoursesFull(getAccessTokenSilently) {
  return authFetch('/api/courses/my/full', getAccessTokenSilently)
}

export async function fetchCourseById(courseId, getAccessTokenSilently) {
  return authFetch(`/api/courses/${courseId}`, getAccessTokenSilently)
}

export async function createCourse(payload, getAccessTokenSilently) {
  return authFetch('/api/courses', getAccessTokenSilently, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  })
}

export { API_BASE_URL }
