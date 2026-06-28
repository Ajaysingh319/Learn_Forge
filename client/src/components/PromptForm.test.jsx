import { cleanup, fireEvent, render, screen } from '@testing-library/react'
import { afterEach, describe, expect, it, vi } from 'vitest'
import PromptForm from './PromptForm'

afterEach(() => {
  cleanup()
})

describe('PromptForm', () => {
  it('submits trimmed topics', () => {
    const onSubmit = vi.fn()
    render(<PromptForm onSubmit={onSubmit} />)

    fireEvent.change(screen.getByLabelText(/what do you want to learn/i), {
      target: { value: '  Intro to Spring Boot  ' },
    })
    fireEvent.click(screen.getByRole('button', { name: /generate course/i }))

    expect(onSubmit).toHaveBeenCalledWith('Intro to Spring Boot')
  })

  it('shows validation feedback for short topics', () => {
    render(<PromptForm onSubmit={vi.fn()} />)

    fireEvent.change(screen.getByLabelText(/what do you want to learn/i), {
      target: { value: 'AI' },
    })
    fireEvent.click(screen.getByRole('button', { name: /generate course/i }))

    expect(screen.getByText(/must be at least 3 characters/i)).toBeInTheDocument()
  })
})
