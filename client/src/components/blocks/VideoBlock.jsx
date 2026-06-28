import { useCallback, useEffect, useState } from 'react'
import ErrorMessage from '../ErrorMessage'
import { fetchYoutubeVideos } from '../../utils/api'

function VideoBlock({ query, url }) {
  const searchText = query || url || ''
  const [videos, setVideos] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  const loadVideos = useCallback(async () => {
    if (!searchText) {
      setVideos([])
      setError('')
      return
    }

    setLoading(true)
    setError('')
    try {
      const results = await fetchYoutubeVideos(searchText)
      setVideos(results)
    } catch (err) {
      setError(err.message || 'Failed to fetch videos')
      setVideos([])
    } finally {
      setLoading(false)
    }
  }, [searchText])

  useEffect(() => {
    loadVideos()
  }, [loadVideos])

  if (!searchText) {
    return null
  }

  const primary = videos[0]

  return (
    <div className="block-video">
      <p className="block-video-label">Recommended video</p>
      <div className="block-video-placeholder">
        <span>Video search: {searchText}</span>
        {loading ? <small>Loading YouTube suggestions...</small> : null}
        {error ? <ErrorMessage message={error} onRetry={loadVideos} /> : null}
        {!loading && !error && primary ? (
          <>
            <iframe
              className="video-embed"
              src={primary.embedUrl}
              title={primary.title || 'Lesson video'}
              allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share"
              referrerPolicy="strict-origin-when-cross-origin"
              allowFullScreen
            />
            <div className="video-meta">
              <strong>{primary.title}</strong>
              <span>{primary.channelTitle}</span>
            </div>
            {videos.length > 1 ? (
              <ul className="video-list">
                {videos.slice(1).map((video) => (
                  <li key={video.videoId}>
                    <a
                      href={video.embedUrl}
                      target="_blank"
                      rel="noreferrer"
                    >
                      {video.title || video.videoId}
                    </a>
                  </li>
                ))}
              </ul>
            ) : null}
          </>
        ) : null}
        {!loading && !error && videos.length === 0 ? (
          <small>No videos found for this query.</small>
        ) : null}
      </div>
    </div>
  )
}

export default VideoBlock
