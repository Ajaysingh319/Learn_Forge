import { useState } from 'react'
import { TOPIC_MAX_LENGTH, validateTopic } from '../utils/validation'

function PromptForm({ onSubmit, isLoading = false }) {
  const [topic, setTopic] = useState('')
  const [validationError, setValidationError] = useState('')

  const handleSubmit = (event) => {
    event.preventDefault()
    const nextError = validateTopic(topic)
    setValidationError(nextError)

    if (nextError || isLoading) {
      return
    }

    onSubmit(topic.trim())
  }

  const handleChange = (event) => {
    setTopic(event.target.value)
    if (validationError) {
      setValidationError('')
    }
  }

  const remaining = TOPIC_MAX_LENGTH - topic.length

  return (
    <form className="card prompt-form" onSubmit={handleSubmit} noValidate>
      <label htmlFor="topic">What do you want to learn?</label>
      <textarea
        id="topic"
        placeholder="Example: Intro to React Hooks"
        value={topic}
        onChange={handleChange}
        rows={3}
        maxLength={TOPIC_MAX_LENGTH}
        aria-invalid={Boolean(validationError)}
        aria-describedby="topic-help topic-counter"
      />
      <div className="prompt-form-meta">
        <p id="topic-help" className="field-hint">
          Enter a clear topic between 3 and 180 characters.
        </p>
        <p id="topic-counter" className={`field-counter ${remaining < 20 ? 'warning' : ''}`}>
          {topic.length}/{TOPIC_MAX_LENGTH}
        </p>
      </div>
      {validationError ? <p className="field-error">{validationError}</p> : null}
      <button type="submit" disabled={isLoading}>
        {isLoading ? 'Generating...' : 'Generate Course'}
      </button>
    </form>
  )
}

export default PromptForm
