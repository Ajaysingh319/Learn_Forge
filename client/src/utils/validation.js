export const TOPIC_MIN_LENGTH = 3
export const TOPIC_MAX_LENGTH = 180

export function validateTopic(topic) {
  const trimmed = (topic || '').trim()

  if (!trimmed) {
    return 'Please enter a topic to generate a course.'
  }

  if (trimmed.length < TOPIC_MIN_LENGTH) {
    return `Topic must be at least ${TOPIC_MIN_LENGTH} characters.`
  }

  if (trimmed.length > TOPIC_MAX_LENGTH) {
    return `Topic must be ${TOPIC_MAX_LENGTH} characters or fewer.`
  }

  return ''
}
