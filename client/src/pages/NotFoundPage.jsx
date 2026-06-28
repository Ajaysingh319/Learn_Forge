import { Link } from 'react-router-dom'

function NotFoundPage() {
  return (
    <section className="page">
      <header>
        <h1>Page Not Found</h1>
        <p>The page you requested does not exist.</p>
      </header>
      <Link to="/" className="text-link">
        Back to Home
      </Link>
    </section>
  )
}

export default NotFoundPage
