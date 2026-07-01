import { Link, useLocation } from 'react-router-dom'
import useAuth from '../hooks/useAuth'

function buildBreadcrumbs(pathname) {
  const segments = pathname.split('/').filter(Boolean)
  const crumbs = [{ label: 'Home', to: '/' }]

  if (segments[0] === 'course' && segments[1]) {
    crumbs.push({ label: 'Course', to: `/course/${segments[1]}` })
  }

  if (segments[0] === 'courses' && segments[1]) {
    crumbs.push({ label: 'Course', to: `/course/${segments[1]}` })
    if (segments[2] === 'module' && segments[3] !== undefined) {
      crumbs.push({
        label: `Module ${Number(segments[3]) + 1}`,
        to: `/course/${segments[1]}`,
      })
    }
    if (segments[4] === 'lesson' && segments[5] !== undefined) {
      crumbs.push({
        label: `Lesson ${Number(segments[5]) + 1}`,
        to: pathname,
      })
    }
  }

  if (segments[0] === 'lesson' && segments[1]) {
    crumbs.push({ label: 'Lesson', to: pathname })
  }

  return crumbs
}

function firstName(user) {
  const source = user?.name || user?.email || ''
  return source.split(/[\s@]/)[0] || 'there'
}

function TopBar() {
  const location = useLocation()
  const { user } = useAuth()
  const breadcrumbs = buildBreadcrumbs(location.pathname)

  return (
    <header className="topbar">
      <nav className="breadcrumbs" aria-label="Breadcrumb">
        {breadcrumbs.map((crumb, index) => {
          const isLast = index === breadcrumbs.length - 1
          return (
            <span key={`${crumb.to}-${index}`} className="breadcrumb-item">
              {isLast ? (
                <span className="breadcrumb-current">{crumb.label}</span>
              ) : (
                <Link to={crumb.to}>{crumb.label}</Link>
              )}
              {!isLast ? <span className="breadcrumb-separator">/</span> : null}
            </span>
          )
        })}
      </nav>
      <div className="topbar-greeting">
        Hi, <strong>{firstName(user)}</strong> 👋
      </div>
    </header>
  )
}

export default TopBar
