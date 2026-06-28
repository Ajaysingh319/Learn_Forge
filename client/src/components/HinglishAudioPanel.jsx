import { useMemo, useState } from 'react'
import ErrorMessage from './ErrorMessage'
import LoadingSpinner from './LoadingSpinner'
import useAuth from '../hooks/useAuth'
import { generateSpeech, translateToHinglish } from '../utils/api'
import { lessonToPlainText } from '../utils/lessonText'

function HinglishAudioPanel({ lesson }) {
  const { getAccessTokenSilently } = useAuth()
  const [hinglishText, setHinglishText] = useState('')
  const [audioUrl, setAudioUrl] = useState('')
  const [loadingTranslation, setLoadingTranslation] = useState(false)
  const [loadingAudio, setLoadingAudio] = useState(false)
  const [error, setError] = useState('')

  const lessonText = useMemo(() => lessonToPlainText(lesson), [lesson])

  const handleTranslate = async () => {
    setLoadingTranslation(true)
    setError('')
    try {
      const response = await translateToHinglish(lessonText, getAccessTokenSilently)
      setHinglishText(response.text)
    } catch (err) {
      setError(err.message || 'Failed to generate Hinglish explanation')
    } finally {
      setLoadingTranslation(false)
    }
  }

  const handleAudio = async () => {
    const textForAudio = hinglishText || lessonText
    setLoadingAudio(true)
    setError('')
    try {
      const response = await generateSpeech(textForAudio, getAccessTokenSilently)
      setAudioUrl(`data:${response.mimeType};base64,${response.base64Audio}`)
    } catch (err) {
      setError(err.message || 'Failed to generate lesson audio')
    } finally {
      setLoadingAudio(false)
    }
  }

  if (!lessonText) {
    return null
  }

  return (
    <section className="card multilingual-panel">
      <div>
        <h2>Hinglish Explanation</h2>
        <p>Generate a student-friendly Hinglish explanation and audio narration.</p>
      </div>

      <div className="multilingual-actions">
        <button type="button" onClick={handleTranslate} disabled={loadingTranslation}>
          Generate Hinglish
        </button>
        <button type="button" onClick={handleAudio} disabled={loadingAudio}>
          Generate Audio
        </button>
      </div>

      {loadingTranslation ? <LoadingSpinner label="Translating lesson..." /> : null}
      {loadingAudio ? <LoadingSpinner label="Generating audio..." /> : null}
      {error ? <ErrorMessage message={error} /> : null}

      {hinglishText ? (
        <div className="hinglish-text">
          <h3>Explanation</h3>
          <p>{hinglishText}</p>
        </div>
      ) : null}

      {audioUrl ? (
        <audio className="lesson-audio" controls src={audioUrl}>
          <track kind="captions" />
        </audio>
      ) : null}
    </section>
  )
}

export default HinglishAudioPanel
