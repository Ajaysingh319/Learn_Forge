import ErrorMessage from './ErrorMessage'
import LoadingSpinner from './LoadingSpinner'

function PageState({ loading, error, isEmpty, emptyMessage, children }) {
  if (loading) {
    return <LoadingSpinner label="Loading..." />
  }

  if (error) {
    return <ErrorMessage message={error} />
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
