import { useEffect, useMemo, useState } from 'react'
import { useParams } from 'react-router-dom'
import ErrorMessage from '../components/ErrorMessage'
import LessonRenderer from '../components/LessonRenderer'
import LoadingSpinner from '../components/LoadingSpinner'
import useAuth from '../hooks/useAuth'
import { fetchMyCoursesFull } from '../utils/api'

function buildSampleLesson(lessonId) {
  return {
    title: `Lesson: ${lessonId}`,
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
      {
        type: 'video',
        query: 'Introductory tutorial for this lesson topic',
      },
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

function LessonPage() {
  const { id } = useParams()
  const { getAccessTokenSilently } = useAuth()
  const [lesson, setLesson] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const fallbackLesson = useMemo(() => buildSampleLesson(id), [id])

  useEffect(() => {
    if (!id) {
      return
    }

    async function loadLesson() {
      setLoading(true)
      setError('')
      try {
        const courses = await fetchMyCoursesFull(getAccessTokenSilently)
        const foundLesson = findLessonInCourses(courses, id)
        setLesson(foundLesson || fallbackLesson)
      } catch (err) {
        setLesson(fallbackLesson)
        setError(err.message || 'Showing demo lesson content')
      } finally {
        setLoading(false)
      }
    }

    loadLesson()
  }, [id, getAccessTokenSilently, fallbackLesson])

  if (!id) {
    return <ErrorMessage message="Lesson id is missing." />
  }

  if (loading) {
    return <LoadingSpinner label="Loading lesson..." />
  }

  return (
    <section className="page">
      {error ? <ErrorMessage message={`${error}. Displaying fallback lesson preview.`} /> : null}
      <LessonRenderer
        title={lesson?.title}
        objectives={lesson?.objectives || []}
        content={lesson?.content || []}
      />
    </section>
  )
}

export default LessonPage
