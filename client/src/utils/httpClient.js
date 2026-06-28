const DEFAULT_TIMEOUT_MS = 30000
const AI_TIMEOUT_MS = 60000

function delay(ms) {
  return new Promise((resolve) => {
    setTimeout(resolve, ms)
  })
}

function isRetriableError(error) {
  if (!error) {
    return false
  }

  if (error.name === 'AbortError') {
    return true
  }

  const status = error.status
  return status === 429 || status >= 500
}

function normalizeFetchError(error) {
  if (error?.name === 'AbortError') {
    return new Error('Request timed out. Please try again.')
  }

  if (error instanceof Error) {
    return error
  }

  return new Error('Request failed')
}

async function parseErrorResponse(response) {
  try {
    const body = await response.json()
    if (body?.message) {
      if (Array.isArray(body.details) && body.details.length > 0) {
        return `${body.message}: ${body.details.join(', ')}`
      }
      return body.message
    }
  } catch {
    // Fall back to status text when body is not JSON.
  }

  return `Request failed: ${response.status}`
}

export async function fetchJson(url, options = {}) {
  const {
    timeoutMs = DEFAULT_TIMEOUT_MS,
    retries = 0,
    ...fetchOptions
  } = options

  let lastError = new Error('Request failed')

  for (let attempt = 0; attempt <= retries; attempt += 1) {
    const controller = new AbortController()
    const timer = setTimeout(() => controller.abort(), timeoutMs)

    try {
      const response = await fetch(url, {
        ...fetchOptions,
        signal: controller.signal,
      })
      clearTimeout(timer)

      if (!response.ok) {
        const message = await parseErrorResponse(response)
        const error = new Error(message)
        error.status = response.status
        throw error
      }

      if (response.status === 204) {
        return null
      }

      return response.json()
    } catch (error) {
      clearTimeout(timer)
      lastError = normalizeFetchError(error)

      if (attempt < retries && isRetriableError(error)) {
        await delay(400 * (attempt + 1))
        continue
      }

      throw lastError
    }
  }

  throw lastError
}

export { AI_TIMEOUT_MS, DEFAULT_TIMEOUT_MS }
