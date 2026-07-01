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
  const { setLastPrompt } = useAppContext()
  const { pushToast } = useToast()
  const { isAuthenticated, getAccessTokenSilently } = useAuth()
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
      setCourses(Array.isArray(data) ? data.filter(Boolean) : [])
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
    setGenerating(true)
    setGenerateError('')
    try {
      const outline = await generateCourse(topic, getAccessTokenSilently)
      if (!outline?.title || !Array.isArray(outline?.modules) || outline.modules.length === 0) {
        throw new Error('Course generation returned an invalid outline. Please try again.')
      }

      const saved = await saveGeneratedOutline(outline, getAccessTokenSilently)
      if (!saved?.id && !saved?.title) {
        throw new Error('Course was generated but failed to save. Check that the backend is running.')
      }
      const updatedCourses = await fetchMyCourses(getAccessTokenSilently)
      setCourses(Array.isArray(updatedCourses) ? updatedCourses.filter(Boolean) : [])
      const courseTitle = saved?.title || outline.title || topic
      pushToast(`Course "${courseTitle}" saved successfully.`, 'success')
    } catch (err) {
      const message = err.message || 'Failed to generate and save course'
      setGenerateError(message)
      pushToast(message, 'error')
    } finally {
      setGenerating(false)
    }
  }

  const totalLessons = courses.reduce((sum, course) => sum + (course.lessonCount || 0), 0)

  return (
    <section className="page dashboard">
      <header className="dash-hero">
        <span className="dash-hero-badge">AI Course Studio</span>
        <h1>What do you want to learn today?</h1>
        <p>
          Enter any topic and LearnForge instantly builds a structured course — modules, lessons,
          quizzes, videos, and resources included.
        </p>
      </header>

      <div className="dash-grid">
        <div className="dash-primary">
          <PromptForm onSubmit={handleGenerateAndSave} isLoading={generating} />
          {generateError ? <ErrorMessage message={generateError} /> : null}
        </div>

        <aside className="dash-stats">
          <div className="stat-card">
            <span className="stat-value">{courses.length}</span>
            <span className="stat-label">Courses created</span>
          </div>
          <div className="stat-card">
            <span className="stat-value">{totalLessons}</span>
            <span className="stat-label">Total lessons</span>
          </div>
        </aside>
      </div>

      <section className="dash-section">
        <div className="dash-section-head">
          <h2>My courses</h2>
          {courses.length > 0 ? <span className="pill">{courses.length}</span> : null}
        </div>

        {coursesError ? <ErrorMessage message={coursesError} onRetry={loadCourses} /> : null}
        {loadingCourses ? <SkeletonList count={3} /> : null}

        {!loadingCourses && !coursesError && courses.length === 0 ? (
          <div className="empty-card">
            <span className="empty-emoji" aria-hidden="true">🎓</span>
            <h3>No courses yet</h3>
            <p>Generate your first course above to get started.</p>
          </div>
        ) : null}

        {!loadingCourses && courses.length > 0 ? (
          <div className="course-grid">
            {courses.map((course) => (
              <Link key={course?.id || course?.title} to={`/course/${course.id}`} className="course-card">
                <span className="course-card-icon" aria-hidden="true">📘</span>
                <h3>{course?.title || 'Untitled course'}</h3>
                <div className="course-card-meta">
                  <span>{course.moduleCount || 0} modules</span>
                  <span className="dot" aria-hidden="true">•</span>
                  <span>{course.lessonCount || 0} lessons</span>
                </div>
                <span className="course-card-cta">Open course →</span>
              </Link>
            ))}
          </div>
        ) : null}
      </section>
    </section>
  )
}

export default HomePage
