import CodeBlock from './blocks/CodeBlock'
import HeadingBlock from './blocks/HeadingBlock'
import MCQBlock from './blocks/MCQBlock'
import ParagraphBlock from './blocks/ParagraphBlock'
import UnknownBlock from './blocks/UnknownBlock'
import VideoBlock from './blocks/VideoBlock'

function renderBlock(block, index) {
  const type = block?.type?.toLowerCase()

  switch (type) {
    case 'heading':
      return <HeadingBlock key={`heading-${index}`} text={block.text} />
    case 'paragraph':
      return <ParagraphBlock key={`paragraph-${index}`} text={block.text} />
    case 'code':
      return (
        <CodeBlock
          key={`code-${index}`}
          language={block.language}
          text={block.text}
        />
      )
    case 'video':
      return (
        <VideoBlock
          key={`video-${index}`}
          query={block.query}
          url={block.url}
        />
      )
    case 'mcq':
      return (
        <MCQBlock
          key={`mcq-${index}`}
          question={block.question}
          options={block.options}
          answer={block.answer}
          explanation={block.explanation}
        />
      )
    default:
      return <UnknownBlock key={`unknown-${index}`} block={block} />
  }
}

function ResourcesSection({ resources }) {
  if (!resources || resources.length === 0) {
    return null
  }

  return (
    <div className="lesson-resources">
      <h3>Suggested readings &amp; resources</h3>
      <ul>
        {resources.map((resource, index) => {
          const label = resource?.title || resource?.url || 'Resource'
          return (
            <li key={`resource-${index}`}>
              {resource?.url ? (
                <a href={resource.url} target="_blank" rel="noreferrer">
                  {label}
                </a>
              ) : (
                label
              )}
            </li>
          )
        })}
      </ul>
    </div>
  )
}

function LessonRenderer({ title, objectives = [], content = [], resources = [] }) {
  if (content.length === 0) {
    return (
      <section className="card">
        <h3>No lesson content yet</h3>
        <p>Generate a lesson to render structured blocks here.</p>
      </section>
    )
  }

  return (
    <section className="card lesson-renderer">
      {title ? <h1 className="lesson-title">{title}</h1> : null}

      {objectives.length > 0 ? (
        <div className="lesson-objectives">
          <h3>Objectives</h3>
          <ul>
            {objectives.map((objective) => (
              <li key={objective}>{objective}</li>
            ))}
          </ul>
        </div>
      ) : null}

      <div className="lesson-content">
        {content.map((block, index) => (
          <article key={`${block.type}-${index}`} className="lesson-block">
            {renderBlock(block, index)}
          </article>
        ))}
      </div>

      <ResourcesSection resources={resources} />
    </section>
  )
}

export default LessonRenderer
