import { useCallback, useEffect, useMemo, useState } from 'react'
import ErrorMessage from './ErrorMessage'
import LoadingSpinner from './LoadingSpinner'
import useAuth from '../hooks/useAuth'
import { generateSpeech, translateToHinglish } from '../utils/api'
import { clampText, lessonToPlainText } from '../utils/lessonText'

// Backend caps translate/tts input; keep audio shorter so narration stays quick.
const TRANSLATE_MAX_CHARS = 7000
const AUDIO_MAX_CHARS = 3000

function HinglishAudioPanel({ lesson }) {
  const { getAccessTokenSilently } = useAuth()
  const [hinglishText, setHinglishText] = useState('')
  const [audioUrl, setAudioUrl] = useState('')
  const [loadingTranslation, setLoadingTranslation] = useState(false)
  const [loadingAudio, setLoadingAudio] = useState(false)
  const [error, setError] = useState('')
  const [lastAction, setLastAction] = useState('translate')

  const lessonText = useMemo(() => lessonToPlainText(lesson), [lesson])

  const handleTranslate = useCallback(async () => {
    setLastAction('translate')
    setLoadingTranslation(true)
    setError('')
    try {
      const response = await translateToHinglish(clampText(lessonText, TRANSLATE_MAX_CHARS), getAccessTokenSilently)
      setHinglishText(response.text)
    } catch (err) {
      setError(err.message || 'Failed to generate Hinglish explanation')
    } finally {
      setLoadingTranslation(false)
    }
  }, [lessonText, getAccessTokenSilently])

  const handleAudio = useCallback(async () => {
    setLastAction('audio')
    const textForAudio = clampText(hinglishText || lessonText, AUDIO_MAX_CHARS)
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
  }, [hinglishText, lessonText, getAccessTokenSilently])

  const handleRetry = () => {
    if (loadingTranslation || loadingAudio) {
      return
    }

    if (lastAction === 'audio') {
      handleAudio()
      return
    }

    handleTranslate()
  }

  useEffect(() => {
    setHinglishText('')
    setAudioUrl('')
    setError('')
  }, [lessonText])

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
        <button type="button" onClick={handleTranslate} disabled={loadingTranslation || loadingAudio}>
          Generate Hinglish
        </button>
        <button type="button" onClick={handleAudio} disabled={loadingAudio || loadingTranslation}>
          Generate Audio
        </button>
      </div>

      {loadingTranslation ? <LoadingSpinner label="Translating lesson..." /> : null}
      {loadingAudio ? <LoadingSpinner label="Generating audio..." /> : null}
      {error ? <ErrorMessage message={error} onRetry={handleRetry} /> : null}

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
