import { useParams } from 'react-router-dom'
import ErrorMessage from '../components/ErrorMessage'
import LoadingSpinner from '../components/LoadingSpinner'

function CoursePage() {
  const { id } = useParams()

  if (!id) {
    return <ErrorMessage message="Course id is missing." />
  }

  return (
    <section className="page">
      <header>
        <h1>Course Overview</h1>
        <p>Course ID: {id}</p>
      </header>

      <LoadingSpinner label="Course data integration is coming in next chunk." />
    </section>
  )
}

export default CoursePage
