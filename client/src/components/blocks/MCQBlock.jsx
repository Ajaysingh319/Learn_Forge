import { useState } from 'react'

function MCQBlock({ question, options = [], answer, explanation }) {
  const [selected, setSelected] = useState(null)
  const [submitted, setSubmitted] = useState(false)

  const handleSubmit = () => {
    if (selected === null) {
      return
    }
    setSubmitted(true)
  }

  const isCorrect = submitted && selected === answer

  return (
    <div className="block-mcq">
      <p className="block-mcq-question">{question}</p>
      <div className="block-mcq-options">
        {options.map((option, index) => (
          <label key={`${question}-${index}`} className="block-mcq-option">
            <input
              type="radio"
              name={question}
              value={index}
              checked={selected === index}
              disabled={submitted}
              onChange={() => setSelected(index)}
            />
            <span>{option}</span>
          </label>
        ))}
      </div>

      {!submitted ? (
        <button type="button" onClick={handleSubmit} disabled={selected === null}>
          Check Answer
        </button>
      ) : (
        <div className={`block-mcq-result ${isCorrect ? 'correct' : 'incorrect'}`}>
          <strong>{isCorrect ? 'Correct!' : 'Not quite.'}</strong>
          <p>{explanation || 'Review the lesson content and try again.'}</p>
        </div>
      )}
    </div>
  )
}

export default MCQBlock
