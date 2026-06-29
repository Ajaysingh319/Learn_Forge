import { useRef, useState } from 'react'
import { exportLessonPdf } from '../utils/exportLessonPdf'
import LessonPdfContent from './LessonPdfContent'

function LessonPDFExporter({ title, objectives = [], content = [], resources = [] }) {
  const captureRef = useRef(null)
  const [exporting, setExporting] = useState(false)
  const [error, setError] = useState('')

  const canExport = content.length > 0

  const handleDownload = async () => {
    if (!canExport || !captureRef.current) {
      return
    }

    setExporting(true)
    setError('')

    try {
      await exportLessonPdf(captureRef.current, title)
    } catch (exportError) {
      setError(exportError.message || 'Failed to generate PDF.')
    } finally {
      setExporting(false)
    }
  }

  return (
    <div className="lesson-pdf-exporter">
      <button type="button" onClick={handleDownload} disabled={!canExport || exporting}>
        {exporting ? 'Generating PDF...' : 'Download PDF'}
      </button>
      {error ? <p className="lesson-pdf-error">{error}</p> : null}

      <div className="lesson-pdf-capture" aria-hidden="true">
        <div ref={captureRef}>
          <LessonPdfContent
            title={title}
            objectives={objectives}
            content={content}
            resources={resources}
          />
        </div>
      </div>
    </div>
  )
}

export default LessonPDFExporter
