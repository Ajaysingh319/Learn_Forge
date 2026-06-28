function ErrorMessage({ message = 'Something went wrong. Please try again.' }) {
  return (
    <div className="error-state" role="alert">
      {message}
    </div>
  )
}

export default ErrorMessage
