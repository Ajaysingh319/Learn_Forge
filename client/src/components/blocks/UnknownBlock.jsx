function UnknownBlock({ block }) {
  return (
    <div className="block-unknown">
      <strong>Unsupported block type:</strong> {block?.type || 'unknown'}
    </div>
  )
}

export default UnknownBlock
