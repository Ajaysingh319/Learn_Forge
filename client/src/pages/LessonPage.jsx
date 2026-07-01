import { useCallback, useState } from 'react'
import { useParams } from 'react-router-dom'
import ErrorMessage from '../components/ErrorMessage'
import HinglishAudioPanel from '../components/HinglishAudioPanel'
import LessonPDFExporter from '../components/LessonPDFExporter'
import LessonRenderer from '../components/LessonRenderer'
import { LessonSkeleton } from '../components/SkeletonLoader'
import useAuth from '../hooks/useAuth'
import useAsync from '../hooks/useAsync'
import { fetchCourseById, fetchMyCoursesFull, generateLessonContent } from '../utils/api'

function hasContent(lesson) {
  return Boolean(lesson) && Array.isArray(lesson.content) && lesson.content.length > 0
}

function locateNested(course, moduleIndex, lessonIndex) {
  const module = course?.modules?.[Number(moduleIndex)]
  const lesson = module?.lessons?.[Number(lessonIndex)]
  if (!course || !lesson) {
    return null
  }
  return { courseId: course.id, lesson }
}

function locateById(courses, lessonId) {
  for (const course of courses) {
    for (const module of course.modules || []) {
      for (const lesson of module.lessons || []) {
        if (lesson.id === lessonId) {
          return { courseId: course.id, lesson }
        }
      }
    }
  }
  return null
}

function LessonPage() {
  const { id, courseId, moduleIndex, lessonIndex } = useParams()
  const { getAccessTokenSilently } = useAuth()
  const isNestedRoute = courseId !== undefined
  const [regenerating, setRegenerating] = useState(false)

  const load = useCallback(async () => {
    let located = null
    if (isNestedRoute) {
      const course = await fetchCourseById(courseId, getAccessTokenSilently)
      located = locateNested(course, moduleIndex, lessonIndex)
    } else if (id) {
      const courses = await fetchMyCoursesFull(getAccessTokenSilently)
      located = locateById(Array.isArray(courses) ? courses.filter(Boolean) : [], id)
    } else {
      throw new Error('Lesson route parameters are missing.')
    }

    if (!located) {
      throw new Error('Lesson not found. Open it from one of your saved courses.')
    }

    // Lessons are saved as titles only. Generate the real content with AI on first
    // view and persist it, so revisiting the lesson is instant.
    const lesson = hasContent(located.lesson)
      ? located.lesson
      : await generateLessonContent(located.courseId, located.lesson.id, getAccessTokenSilently)

    return { courseId: located.courseId, lessonId: located.lesson.id, lesson }
  }, [id, courseId, moduleIndex, lessonIndex, getAccessTokenSilently, isNestedRoute])

  const { data, loading, error, reload } = useAsync(load, [load])
  const lesson = data?.lesson

  const handleRegenerate = async () => {
    if (!data || regenerating) {
      return
    }
    setRegenerating(true)
    try {
      await generateLessonContent(data.courseId, data.lessonId, getAccessTokenSilently, true)
      await reload()
    } catch {
      // reload() surfaces its own error state; regenerate failure is non-fatal.
    } finally {
      setRegenerating(false)
    }
  }

  if (loading || regenerating) {
    return (
      <section className="page">
        <p className="lesson-generating-note">
          Generating lesson content with AI — this can take a few seconds…
        </p>
        <LessonSkeleton />
      </section>
    )
  }

  if (error) {
    return (
      <section className="page">
        <ErrorMessage message={error} onRetry={reload} />
      </section>
    )
  }

  return (
    <section className="page">
      <div className="lesson-page-toolbar">
        <button
          type="button"
          className="button-secondary"
          onClick={handleRegenerate}
          disabled={regenerating}
        >
          Regenerate with AI
        </button>
        <LessonPDFExporter
          title={lesson?.title}
          objectives={lesson?.objectives || []}
          content={lesson?.content || []}
          resources={lesson?.resources || []}
        />
      </div>
      <LessonRenderer
        title={lesson?.title}
        objectives={lesson?.objectives || []}
        content={lesson?.content || []}
        resources={lesson?.resources || []}
      />
      <HinglishAudioPanel lesson={lesson} />
    </section>
  )
}

export default LessonPage
