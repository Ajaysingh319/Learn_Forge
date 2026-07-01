/**
 * Trims text to a maximum length, cutting at the last sentence/space boundary
 * so requests stay within backend validation limits and TTS stays responsive.
 */
export function clampText(text, maxChars) {
  const value = (text || '').trim()
  if (value.length <= maxChars) {
    return value
  }
  const slice = value.slice(0, maxChars)
  const boundary = Math.max(slice.lastIndexOf('. '), slice.lastIndexOf('\n'), slice.lastIndexOf(' '))
  return (boundary > maxChars * 0.5 ? slice.slice(0, boundary + 1) : slice).trim()
}

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
