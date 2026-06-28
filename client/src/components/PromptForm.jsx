import { useState } from 'react'

function PromptForm({ onSubmit, isLoading = false }) {
  const [topic, setTopic] = useState('')

  const handleSubmit = (event) => {
    event.preventDefault()
    if (!topic.trim() || isLoading) {
      return
    }
    onSubmit(topic.trim())
  }

  return (
    <form className="card prompt-form" onSubmit={handleSubmit}>
      <label htmlFor="topic">What do you want to learn?</label>
      <textarea
        id="topic"
        placeholder="Example: Intro to React Hooks"
        value={topic}
        onChange={(event) => setTopic(event.target.value)}
        rows={3}
      />
      <button type="submit" disabled={isLoading}>
        {isLoading ? 'Generating...' : 'Generate Course'}
      </button>
    </form>
  )
}

export default PromptForm
