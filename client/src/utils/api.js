const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080'

export async function fetchHealth() {
  const response = await fetch(`${API_BASE_URL}/api/health`)
  if (!response.ok) {
    throw new Error('Unable to fetch backend health')
  }
  return response.json()
}

export { API_BASE_URL }
