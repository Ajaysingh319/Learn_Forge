function LessonRenderer({ content = [] }) {
  if (content.length === 0) {
    return (
      <section className="card">
        <h3>No lesson content yet</h3>
        <p>Generate a lesson to render structured blocks here.</p>
      </section>
    )
  }

  return (
    <section className="card">
      {content.map((block, index) => (
        <article key={`${block.type}-${index}`} className="lesson-block">
          <strong>{block.type}</strong>
          <p>{block.text || block.question || block.url || 'Block preview'}</p>
        </article>
      ))}
    </section>
  )
}

export default LessonRenderer
