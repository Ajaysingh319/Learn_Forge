const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080'

export async function fetchHealth() {
  const response = await fetch(`${API_BASE_URL}/api/health`)
  if (!response.ok) {
    throw new Error('Unable to fetch backend health')
  }
  return response.json()
}

export async function fetchMyCourses(getAccessTokenSilently) {
  const token = await getAccessTokenSilently()
  const response = await fetch(`${API_BASE_URL}/api/courses/my`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  })
  if (!response.ok) {
    throw new Error('Unable to fetch user courses')
  }
  return response.json()
}

export async function createCourse(payload, getAccessTokenSilently) {
  const token = await getAccessTokenSilently()
  const response = await fetch(`${API_BASE_URL}/api/courses`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`,
    },
    body: JSON.stringify(payload),
  })
  if (!response.ok) {
    throw new Error('Unable to create course')
  }
  return response.json()
}

export { API_BASE_URL }
