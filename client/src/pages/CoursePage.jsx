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
        <section className="page">
          <header className="page-header">
            <h1>{course.title || 'Untitled course'}</h1>
            <p>{course.description || ''}</p>
          </header>

          {course.modules?.filter(Boolean).map((module, moduleIndex) => (
            <section key={module.id || module.title} className="card">
              <h2>
                Module {moduleIndex + 1}: {module?.title || 'Untitled module'}
              </h2>
              <ul className="course-list">
                {module.lessons?.filter(Boolean).map((lesson, lessonIndex) => (
                  <li key={lesson.id || `${moduleIndex}-${lessonIndex}`}>
                    <Link to={lessonPath(course.id, moduleIndex, lessonIndex)}>
                      Lesson {lessonIndex + 1}: {lesson?.title || 'Untitled lesson'}
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
