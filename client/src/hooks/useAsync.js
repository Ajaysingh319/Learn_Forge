import { useCallback, useEffect, useState } from 'react'

function useAsync(asyncFn, deps = [], enabled = true) {
  const [data, setData] = useState(null)
  const [loading, setLoading] = useState(Boolean(enabled))
  const [error, setError] = useState('')

  const execute = useCallback(async () => {
    setLoading(true)
    setError('')
    try {
      const result = await asyncFn()
      setData(result)
      return result
    } catch (err) {
      setError(err.message || 'Request failed')
      setData(null)
      throw err
    } finally {
      setLoading(false)
    }
  }, deps)

  useEffect(() => {
    if (!enabled) {
      setLoading(false)
      return
    }
    execute().catch(() => {})
  }, [enabled, execute])

  return { data, loading, error, reload: execute, setData }
}

export default useAsync
