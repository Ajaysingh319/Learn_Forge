import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import ErrorMessage from '../components/ErrorMessage'
import LoadingSpinner from '../components/LoadingSpinner'
import useAuth from '../hooks/useAuth'
import { fetchCourseById } from '../utils/api'

function CoursePage() {
  const { id } = useParams()
  const { getAccessTokenSilently } = useAuth()
  const [course, setCourse] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    if (!id) {
      return
    }

    async function loadCourse() {
      setLoading(true)
      setError('')
      try {
        const data = await fetchCourseById(id, getAccessTokenSilently)
        setCourse(data)
      } catch (err) {
        setError(err.message || 'Failed to load course')
      } finally {
        setLoading(false)
      }
    }

    loadCourse()
  }, [id, getAccessTokenSilently])

  if (!id) {
    return <ErrorMessage message="Course id is missing." />
  }

  if (loading) {
    return <LoadingSpinner label="Loading course..." />
  }

  if (error) {
    return <ErrorMessage message={error} />
  }

  if (!course) {
    return <ErrorMessage message="Course not found." />
  }

  return (
    <section className="page">
      <header>
        <h1>{course.title}</h1>
        <p>{course.description}</p>
      </header>

      {course.modules?.map((module) => (
        <section key={module.id} className="card">
          <h2>{module.title}</h2>
          <ul className="course-list">
            {module.lessons?.map((lesson) => (
              <li key={lesson.id}>
                <Link to={`/lesson/${lesson.id}`}>{lesson.title}</Link>
              </li>
            ))}
          </ul>
        </section>
      ))}
    </section>
  )
}

export default CoursePage
