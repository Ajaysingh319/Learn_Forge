import { useCallback, useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import PromptForm from '../components/PromptForm'
import ErrorMessage from '../components/ErrorMessage'
import { SkeletonList } from '../components/SkeletonLoader'
import { useAppContext } from '../context/AppContext'
import { useToast } from '../context/ToastContext'
import useAuth from '../hooks/useAuth'
import { fetchMyCourses, generateCourse, saveGeneratedOutline } from '../utils/api'

function HomePage() {
  const { setLastPrompt, lastPrompt } = useAppContext()
  const { pushToast } = useToast()
  const { isAuthenticated, user, getAccessTokenSilently } = useAuth()
  const [courses, setCourses] = useState([])
  const [loadingCourses, setLoadingCourses] = useState(false)
  const [generating, setGenerating] = useState(false)
  const [coursesError, setCoursesError] = useState('')
  const [generateError, setGenerateError] = useState('')

  const loadCourses = useCallback(async () => {
    if (!isAuthenticated) {
      setCourses([])
      return
    }

    setLoadingCourses(true)
    setCoursesError('')
    try {
      const data = await fetchMyCourses(getAccessTokenSilently)
      setCourses(data)
    } catch (err) {
      setCoursesError(err.message || 'Failed to load saved courses')
    } finally {
      setLoadingCourses(false)
    }
  }, [isAuthenticated, getAccessTokenSilently])

  useEffect(() => {
    loadCourses()
  }, [loadCourses])

  const handleGenerateAndSave = async (topic) => {
    setLastPrompt(topic)
    if (!isAuthenticated) {
      const message = 'Please login to generate and save courses.'
      setGenerateError(message)
      pushToast(message, 'error')
      return
    }

    setGenerating(true)
    setGenerateError('')
    try {
      const outline = await generateCourse(topic, getAccessTokenSilently)
      const saved = await saveGeneratedOutline(outline, getAccessTokenSilently)
      const updatedCourses = await fetchMyCourses(getAccessTokenSilently)
      setCourses(updatedCourses)
      pushToast(`Course "${saved.title || outline.title}" saved successfully.`, 'success')
    } catch (err) {
      const message = err.message || 'Failed to generate and save course'
      setGenerateError(message)
      pushToast(message, 'error')
    } finally {
      setGenerating(false)
    }
  }

  return (
    <section className="page">
      <header className="page-header">
        <h1>Text-to-Learn Course Generator</h1>
        <p>Submit any topic and generate a structured course outline.</p>
      </header>

      <PromptForm onSubmit={handleGenerateAndSave} isLoading={generating} />
      {generateError ? <ErrorMessage message={generateError} /> : null}

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
        {coursesError ? <ErrorMessage message={coursesError} onRetry={loadCourses} /> : null}
        {loadingCourses ? <SkeletonList count={3} /> : null}
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
