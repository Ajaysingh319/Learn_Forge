import { describe, expect, it } from 'vitest'
import { lessonToPlainText } from './lessonText'

describe('lessonToPlainText', () => {
  it('combines title, objectives, and supported blocks', () => {
    const text = lessonToPlainText({
      title: 'Hooks Intro',
      objectives: ['Understand useState'],
      content: [
        { type: 'heading', text: 'Overview' },
        { type: 'paragraph', text: 'Hooks add state to function components.' },
        { type: 'video', query: 'React hooks tutorial' },
        {
          type: 'mcq',
          question: 'Which hook manages state?',
          explanation: 'useState stores component state.',
        },
      ],
    })

    expect(text).toContain('Hooks Intro')
    expect(text).toContain('Objectives: Understand useState')
    expect(text).toContain('Overview')
    expect(text).toContain('Suggested video: React hooks tutorial')
    expect(text).toContain('Question: Which hook manages state?')
  })

  it('returns an empty string for missing lessons', () => {
    expect(lessonToPlainText(null)).toBe('')
  })
})
