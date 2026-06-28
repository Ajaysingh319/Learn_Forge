import ErrorMessage from './ErrorMessage'
import LoadingSpinner from './LoadingSpinner'
import { SkeletonCard, SkeletonList } from './SkeletonLoader'

function PageState({
  loading,
  error,
  onRetry,
  isEmpty,
  emptyMessage,
  skeleton = 'card',
  children,
}) {
  if (loading) {
    if (skeleton === 'list') {
      return <SkeletonList count={4} />
    }

    if (skeleton === 'cards') {
      return (
        <section className="page">
          <SkeletonCard lines={2} />
          <SkeletonCard lines={4} />
        </section>
      )
    }

    return <LoadingSpinner label="Loading..." />
  }

  if (error) {
    return <ErrorMessage message={error} onRetry={onRetry} />
  }

  if (isEmpty) {
    return (
      <section className="card empty-state">
        <p>{emptyMessage || 'No data available.'}</p>
      </section>
    )
  }

  return children
}

export default PageState
