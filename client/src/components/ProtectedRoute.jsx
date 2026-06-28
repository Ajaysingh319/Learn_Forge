import LoadingSpinner from './LoadingSpinner'
import useAuth from '../hooks/useAuth'

function ProtectedRoute({ children }) {
  const { isAuthenticated, isLoading, login } = useAuth()

  if (isLoading) {
    return <LoadingSpinner label="Checking authentication..." />
  }

  if (!isAuthenticated) {
    return (
      <section className="card">
        <h2>Login Required</h2>
        <p>Please sign in with Auth0 to continue.</p>
        <button type="button" onClick={login}>
          Login with Auth0
        </button>
      </section>
    )
  }

  return children
}

export default ProtectedRoute
