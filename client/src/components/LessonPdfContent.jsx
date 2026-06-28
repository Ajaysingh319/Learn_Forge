function renderPdfBlock(block, index) {
  const type = block?.type?.toLowerCase()

  switch (type) {
    case 'heading':
      return (
        <h2 key={`pdf-heading-${index}`} className="pdf-block-heading">
          {block.text}
        </h2>
      )
    case 'paragraph':
      return (
        <p key={`pdf-paragraph-${index}`} className="pdf-block-paragraph">
          {block.text}
        </p>
      )
    case 'code':
      return (
        <div key={`pdf-code-${index}`} className="pdf-block-code">
          {block.language ? (
            <div className="pdf-block-code-label">{block.language}</div>
          ) : null}
          <pre>
            <code>{block.text}</code>
          </pre>
        </div>
      )
    case 'video':
      return (
        <div key={`pdf-video-${index}`} className="pdf-block-video">
          <strong>Video resource</strong>
          <p>{block.query || block.url || 'Related video for this lesson topic.'}</p>
        </div>
      )
    case 'mcq': {
      const options = block.options || []
      const answerIndex = typeof block.answer === 'number' ? block.answer : -1

      return (
        <div key={`pdf-mcq-${index}`} className="pdf-block-mcq">
          <p className="pdf-block-mcq-question">{block.question}</p>
          <ol className="pdf-block-mcq-options">
            {options.map((option, optionIndex) => (
              <li
                key={`${block.question}-${optionIndex}`}
                className={optionIndex === answerIndex ? 'pdf-block-mcq-correct' : undefined}
              >
                {option}
                {optionIndex === answerIndex ? ' (Correct answer)' : ''}
              </li>
            ))}
          </ol>
          {block.explanation ? (
            <p className="pdf-block-mcq-explanation">
              <strong>Explanation:</strong> {block.explanation}
            </p>
          ) : null}
        </div>
      )
    }
    default:
      return (
        <p key={`pdf-unknown-${index}`} className="pdf-block-unknown">
          Unsupported block type: {block?.type || 'unknown'}
        </p>
      )
  }
}

function LessonPdfContent({ title, objectives = [], content = [] }) {
  return (
    <article className="lesson-pdf-document">
      <header className="lesson-pdf-header">
        <p className="lesson-pdf-brand">LearnForge</p>
        {title ? <h1 className="lesson-pdf-title">{title}</h1> : null}
      </header>

      {objectives.length > 0 ? (
        <section className="lesson-pdf-objectives">
          <h2>Objectives</h2>
          <ul>
            {objectives.map((objective) => (
              <li key={objective}>{objective}</li>
            ))}
          </ul>
        </section>
      ) : null}

      <section className="lesson-pdf-content">
        {content.map((block, index) => renderPdfBlock(block, index))}
      </section>
    </article>
  )
}

export default LessonPdfContent
