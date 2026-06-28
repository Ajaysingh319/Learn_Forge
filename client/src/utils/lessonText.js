export function lessonToPlainText(lesson) {
  if (!lesson) {
    return ''
  }

  const parts = []
  if (lesson.title) {
    parts.push(lesson.title)
  }
  if (lesson.objectives?.length) {
    parts.push(`Objectives: ${lesson.objectives.join('; ')}`)
  }

  for (const block of lesson.content || []) {
    switch (block.type) {
      case 'heading':
      case 'paragraph':
      case 'code':
        if (block.text) {
          parts.push(block.text)
        }
        break
      case 'video':
        if (block.query) {
          parts.push(`Suggested video: ${block.query}`)
        }
        break
      case 'mcq':
        if (block.question) {
          parts.push(`Question: ${block.question}`)
        }
        if (block.explanation) {
          parts.push(`Explanation: ${block.explanation}`)
        }
        break
      default:
        break
    }
  }

  return parts.join('\n\n')
}
