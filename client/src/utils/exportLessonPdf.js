import html2canvas from 'html2canvas'
import { jsPDF } from 'jspdf'

function sanitizeFilename(title) {
  const base = (title || 'lesson').trim().toLowerCase()
  const cleaned = base.replace(/[^\w\s-]/g, '').replace(/\s+/g, '-')
  return cleaned || 'lesson'
}

export async function exportLessonPdf(element, title) {
  if (!element) {
    throw new Error('PDF export target is missing.')
  }

  const canvas = await html2canvas(element, {
    scale: 2,
    useCORS: true,
    backgroundColor: '#ffffff',
    logging: false,
  })

  const imgData = canvas.toDataURL('image/png')
  const pdf = new jsPDF('p', 'pt', 'a4')
  const pageWidth = pdf.internal.pageSize.getWidth()
  const pageHeight = pdf.internal.pageSize.getHeight()
  const imgWidth = pageWidth
  const imgHeight = (canvas.height * imgWidth) / canvas.width

  let heightLeft = imgHeight
  let position = 0

  pdf.addImage(imgData, 'PNG', 0, position, imgWidth, imgHeight)
  heightLeft -= pageHeight

  while (heightLeft > 0) {
    position = heightLeft - imgHeight
    pdf.addPage()
    pdf.addImage(imgData, 'PNG', 0, position, imgWidth, imgHeight)
    heightLeft -= pageHeight
  }

  pdf.save(`${sanitizeFilename(title)}.pdf`)
}
