export function lessonPath(courseId, moduleIndex, lessonIndex) {
  return `/courses/${courseId}/module/${moduleIndex}/lesson/${lessonIndex}`
}

export function coursePath(courseId) {
  return `/course/${courseId}`
}
