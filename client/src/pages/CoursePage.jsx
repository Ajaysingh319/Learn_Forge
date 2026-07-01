import { Link, useParams } from 'react-router-dom'
import PageState from '../components/PageState'
import useAuth from '../hooks/useAuth'
import useAsync from '../hooks/useAsync'
import { fetchCourseById } from '../utils/api'
import { lessonPath } from '../utils/routes'

function CoursePage() {
  const { courseId } = useParams()
  const { getAccessTokenSilently } = useAuth()

  const { data: course, loading, error, reload } = useAsync(
    () => fetchCourseById(courseId, getAccessTokenSilently),
    [courseId, getAccessTokenSilently],
    Boolean(courseId),
  )

  return (
    <PageState
      loading={loading}
      error={!courseId ? 'Course id is missing.' : error}
      onRetry={reload}
      skeleton="cards"
      isEmpty={!loading && !error && !course}
      emptyMessage="Course not found."
    >
      {course ? (
        <section className="page course-page">
          <header className="course-hero">
            {course.tags?.length ? (
              <div className="course-tags">
                {course.tags.filter(Boolean).slice(0, 5).map((tag) => (
                  <span key={tag} className="tag">{tag}</span>
                ))}
              </div>
            ) : null}
            <h1>{course.title || 'Untitled course'}</h1>
            {course.description ? <p>{course.description}</p> : null}
            <div className="course-hero-meta">
              <span>{course.modules?.length || 0} modules</span>
              <span className="dot" aria-hidden="true">•</span>
              <span>
                {course.modules?.reduce((sum, m) => sum + (m.lessons?.length || 0), 0) || 0} lessons
              </span>
            </div>
          </header>

          {course.modules?.filter(Boolean).map((module, moduleIndex) => (
            <section key={module.id || module.title} className="module-card">
              <div className="module-card-head">
                <span className="module-index">{moduleIndex + 1}</span>
                <h2>{module?.title || 'Untitled module'}</h2>
              </div>
              <ul className="lesson-list">
                {module.lessons?.filter(Boolean).map((lesson, lessonIndex) => (
                  <li key={lesson.id || `${moduleIndex}-${lessonIndex}`}>
                    <Link className="lesson-row" to={lessonPath(course.id, moduleIndex, lessonIndex)}>
                      <span className="lesson-row-index">{lessonIndex + 1}</span>
                      <span className="lesson-row-title">{lesson?.title || 'Untitled lesson'}</span>
                      <span className="lesson-row-arrow" aria-hidden="true">→</span>
                    </Link>
                  </li>
                ))}
              </ul>
            </section>
          ))}
        </section>
      ) : null}
    </PageState>
  )
}

export default CoursePage
