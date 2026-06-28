function ErrorMessage({ message = 'Something went wrong. Please try again.', onRetry }) {
  return (
    <div className="error-state" role="alert">
      <p className="error-state-message">{message}</p>
      {onRetry ? (
        <button type="button" className="button-secondary" onClick={onRetry}>
          Retry
        </button>
      ) : null}
    </div>
  )
}

export default ErrorMessage
