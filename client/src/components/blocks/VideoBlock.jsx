function VideoBlock({ query, url }) {
  const label = query || url || 'Educational video'

  return (
    <div className="block-video">
      <p className="block-video-label">Recommended video</p>
      <div className="block-video-placeholder">
        <span>Video search: {label}</span>
        <small>YouTube embed integration coming in a later chunk.</small>
      </div>
    </div>
  )
}

export default VideoBlock
