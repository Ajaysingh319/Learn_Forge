import { Link } from 'react-router-dom'

function NotFoundPage() {
  return (
    <section className="page not-found">
      <div className="not-found-card">
        <span className="not-found-code">404</span>
        <h1>Page not found</h1>
        <p>The page you requested doesn’t exist or may have moved.</p>
        <Link to="/" className="btn btn-primary">Back to dashboard</Link>
      </div>
    </section>
  )
}

export default NotFoundPage
