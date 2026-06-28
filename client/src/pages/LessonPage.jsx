import { useMemo } from 'react'
import { useParams } from 'react-router-dom'
import LessonRenderer from '../components/LessonRenderer'

function LessonPage() {
  const { id } = useParams()

  const sampleContent = useMemo(
    () => [
      { type: 'heading', text: `Lesson: ${id}` },
      { type: 'paragraph', text: 'This is a placeholder lesson renderer output.' },
      { type: 'video', text: 'React hooks tutorial for beginners' },
    ],
    [id],
  )

  return (
    <section className="page">
      <header>
        <h1>Lesson Viewer</h1>
        <p>Lesson ID: {id}</p>
      </header>
      <LessonRenderer content={sampleContent} />
    </section>
  )
}

export default LessonPage
