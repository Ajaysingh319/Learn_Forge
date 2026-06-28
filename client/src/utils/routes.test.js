import { describe, expect, it } from 'vitest'
import { coursePath, lessonPath } from './routes'

describe('routes', () => {
  it('builds course paths', () => {
    expect(coursePath('abc123')).toBe('/course/abc123')
  })

  it('builds nested lesson paths', () => {
    expect(lessonPath('abc123', 1, 2)).toBe('/courses/abc123/module/1/lesson/2')
  })
})
