import { describe, expect, it } from 'vitest'
import { TOPIC_MAX_LENGTH, TOPIC_MIN_LENGTH, validateTopic } from './validation'

describe('validateTopic', () => {
  it('accepts a valid topic', () => {
    expect(validateTopic('Intro to React Hooks')).toBe('')
  })

  it('rejects empty topics', () => {
    expect(validateTopic('   ')).toBe('Please enter a topic to generate a course.')
  })

  it('rejects topics that are too short', () => {
    expect(validateTopic('AI')).toBe(`Topic must be at least ${TOPIC_MIN_LENGTH} characters.`)
  })

  it('rejects topics that are too long', () => {
    expect(validateTopic('a'.repeat(TOPIC_MAX_LENGTH + 1))).toBe(
      `Topic must be ${TOPIC_MAX_LENGTH} characters or fewer.`,
    )
  })
})
