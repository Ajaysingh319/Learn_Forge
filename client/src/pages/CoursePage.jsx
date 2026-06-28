import { Link, useParams } from 'react-router-dom'
import PageState from '../components/PageState'
import useAuth from '../hooks/useAuth'
import useAsync from '../hooks/useAsync'
import { fetchCourseById } from '../utils/api'
import { lessonPath } from '../utils/routes'

function CoursePage() {
  const { courseId } = useParams()
  const { getAccessTokenSilently } = useAuth()

  const { data: course, loading, error } = useAsync(
    () => fetchCourseById(courseId, getAccessTokenSilently),
    [courseId, getAccessTokenSilently],
    Boolean(courseId),
  )

  return (
    <PageState
      loading={loading}
      error={!courseId ? 'Course id is missing.' : error}
      isEmpty={!loading && !error && !course}
      emptyMessage="Course not found."
    >
      <section className="page">
        <header>
          <h1>{course.title}</h1>
          <p>{course.description}</p>
        </header>

        {course.modules?.map((module, moduleIndex) => (
          <section key={module.id} className="card">
            <h2>
              Module {moduleIndex + 1}: {module.title}
            </h2>
            <ul className="course-list">
              {module.lessons?.map((lesson, lessonIndex) => (
                <li key={lesson.id}>
                  <Link to={lessonPath(course.id, moduleIndex, lessonIndex)}>
                    Lesson {lessonIndex + 1}: {lesson.title}
                  </Link>
                </li>
              ))}
            </ul>
          </section>
        ))}
      </section>
    </PageState>
  )
}

export default CoursePage
