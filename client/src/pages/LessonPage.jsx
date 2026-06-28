import { useMemo } from 'react'
import { useParams } from 'react-router-dom'
import ErrorMessage from '../components/ErrorMessage'
import HinglishAudioPanel from '../components/HinglishAudioPanel'
import LessonPDFExporter from '../components/LessonPDFExporter'
import LessonRenderer from '../components/LessonRenderer'
import LoadingSpinner from '../components/LoadingSpinner'
import useAuth from '../hooks/useAuth'
import useAsync from '../hooks/useAsync'
import { fetchCourseById, fetchMyCoursesFull } from '../utils/api'

function buildSampleLesson(label) {
  return {
    title: `Lesson: ${label}`,
    objectives: [
      'Understand the core concepts covered in this lesson',
      'Identify practical examples and use cases',
      'Apply knowledge through interactive checks',
    ],
    content: [
      { type: 'heading', text: 'Introduction' },
      {
        type: 'paragraph',
        text: 'This lesson demonstrates the structured block renderer used by LearnForge.',
      },
      {
        type: 'code',
        language: 'javascript',
        text: "const greeting = 'Hello, LearnForge!';\nconsole.log(greeting);",
      },
      { type: 'video', query: 'Introductory tutorial for this lesson topic' },
      {
        type: 'mcq',
        question: 'What is the purpose of LessonRenderer?',
        options: [
          'To store courses in MongoDB',
          'To render structured lesson JSON blocks',
          'To replace Auth0 login',
          'To deploy the frontend',
        ],
        answer: 1,
        explanation: 'LessonRenderer maps JSON block types to dedicated UI components.',
      },
      {
        type: 'mcq',
        question: 'Which block type is used for code snippets?',
        options: ['heading', 'paragraph', 'code', 'video'],
        answer: 2,
        explanation: 'The code block renders syntax-friendly snippets with a language label.',
      },
      {
        type: 'mcq',
        question: 'What field does a video block use for search?',
        options: ['url', 'query', 'embed', 'title'],
        answer: 1,
        explanation: 'Video blocks store a search query until YouTube integration is added.',
      },
      {
        type: 'mcq',
        question: 'How many MCQs should a generated lesson include?',
        options: ['1-2', '4-5', '10-12', 'None'],
        answer: 1,
        explanation: 'The AI lesson pipeline targets 4 to 5 MCQs at the end of each lesson.',
      },
    ],
  }
}

function findLessonInCourses(courses, lessonId) {
  for (const course of courses) {
    for (const module of course.modules || []) {
      for (const lesson of module.lessons || []) {
        if (lesson.id === lessonId) {
          return lesson
        }
      }
    }
  }
  return null
}

function resolveLessonFromCourse(course, moduleIndex, lessonIndex) {
  const module = course?.modules?.[Number(moduleIndex)]
  const lesson = module?.lessons?.[Number(lessonIndex)]
  if (!module || !lesson) {
    return null
  }
  return lesson
}

function LessonPage() {
  const { id, courseId, moduleIndex, lessonIndex } = useParams()
  const { getAccessTokenSilently } = useAuth()
  const isNestedRoute = courseId !== undefined

  const fallbackLesson = useMemo(
    () => buildSampleLesson(id || `${courseId}-${moduleIndex}-${lessonIndex}`),
    [id, courseId, moduleIndex, lessonIndex],
  )

  const { data: lesson, loading, error } = useAsync(async () => {
    if (isNestedRoute) {
      const course = await fetchCourseById(courseId, getAccessTokenSilently)
      const resolved = resolveLessonFromCourse(course, moduleIndex, lessonIndex)
      return resolved || fallbackLesson
    }

    if (id) {
      const courses = await fetchMyCoursesFull(getAccessTokenSilently)
      return findLessonInCourses(courses, id) || fallbackLesson
    }

    throw new Error('Lesson route parameters are missing.')
  }, [id, courseId, moduleIndex, lessonIndex, getAccessTokenSilently, isNestedRoute, fallbackLesson])

  if (loading) {
    return (
      <section className="page">
        <LoadingSpinner label="Loading lesson..." />
      </section>
    )
  }

  return (
    <section className="page">
      {error ? <ErrorMessage message={`${error}. Showing fallback lesson preview.`} /> : null}
      <div className="lesson-page-toolbar">
        <LessonPDFExporter
          title={lesson?.title}
          objectives={lesson?.objectives || []}
          content={lesson?.content || []}
        />
      </div>
      <LessonRenderer
        title={lesson?.title}
        objectives={lesson?.objectives || []}
        content={lesson?.content || []}
      />
      <HinglishAudioPanel lesson={lesson} />
    </section>
  )
}

export default LessonPage
