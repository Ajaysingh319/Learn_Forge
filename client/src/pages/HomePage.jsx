import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import PromptForm from '../components/PromptForm'
import ErrorMessage from '../components/ErrorMessage'
import LoadingSpinner from '../components/LoadingSpinner'
import { useAppContext } from '../context/AppContext'
import useAuth from '../hooks/useAuth'
import { fetchMyCourses, generateCourse, saveGeneratedOutline } from '../utils/api'

function HomePage() {
  const { setLastPrompt, lastPrompt } = useAppContext()
  const { isAuthenticated, user, getAccessTokenSilently } = useAuth()
  const [courses, setCourses] = useState([])
  const [loadingCourses, setLoadingCourses] = useState(false)
  const [generating, setGenerating] = useState(false)
  const [error, setError] = useState('')

  useEffect(() => {
    if (!isAuthenticated) {
      setCourses([])
      return
    }

    async function loadCourses() {
      setLoadingCourses(true)
      setError('')
      try {
        const data = await fetchMyCourses(getAccessTokenSilently)
        setCourses(data)
      } catch (err) {
        setError(err.message || 'Failed to load saved courses')
      } finally {
        setLoadingCourses(false)
      }
    }

    loadCourses()
  }, [isAuthenticated, getAccessTokenSilently])

  const handleGenerateAndSave = async (topic) => {
    setLastPrompt(topic)
    if (!isAuthenticated) {
      setError('Please login to generate and save courses.')
      return
    }

    setGenerating(true)
    setError('')
    try {
      const outline = await generateCourse(topic, getAccessTokenSilently)
      await saveGeneratedOutline(outline, getAccessTokenSilently)
      const updatedCourses = await fetchMyCourses(getAccessTokenSilently)
      setCourses(updatedCourses)
    } catch (err) {
      setError(err.message || 'Failed to generate and save course')
    } finally {
      setGenerating(false)
    }
  }

  return (
    <section className="page">
      <header>
        <h1>Text-to-Learn Course Generator</h1>
        <p>Submit any topic and generate a structured course outline.</p>
      </header>

      <PromptForm onSubmit={handleGenerateAndSave} isLoading={generating} />

      {error ? <ErrorMessage message={error} /> : null}

      <section className="card">
        <h2>Latest Prompt</h2>
        <p>{lastPrompt || 'No prompt submitted in this session yet.'}</p>
      </section>

      <section className="card">
        <h2>Auth Status</h2>
        <p>
          {isAuthenticated
            ? `Signed in as ${user?.email || user?.name || user?.sub}`
            : 'Not signed in yet. Login to access protected routes and save courses.'}
        </p>
      </section>

      <section className="card">
        <h2>My Saved Courses</h2>
        {loadingCourses ? <LoadingSpinner label="Loading saved courses..." /> : null}
        {!loadingCourses && courses.length === 0 ? (
          <p>No saved courses yet. Generate one to get started.</p>
        ) : null}
        {!loadingCourses && courses.length > 0 ? (
          <ul className="course-list">
            {courses.map((course) => (
              <li key={course.id}>
                <Link to={`/course/${course.id}`}>{course.title}</Link>
                <span>
                  {course.moduleCount} modules · {course.lessonCount} lessons
                </span>
              </li>
            ))}
          </ul>
        ) : null}
      </section>
    </section>
  )
}

export default HomePage
