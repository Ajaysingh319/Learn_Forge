import { NavLink } from 'react-router-dom'
import useAuth from '../hooks/useAuth'

const links = [
  { to: '/', label: 'Home' },
  { to: '/course/demo-course', label: 'Course Overview' },
  { to: '/lesson/demo-lesson', label: 'Lesson Viewer' },
]

function SidebarNavigation() {
  const { isAuthenticated, user, login, logout, isLoading } = useAuth()

  return (
    <aside className="sidebar">
      <div className="brand">LearnForge</div>
      <div className="auth-summary">
        {isAuthenticated ? (
          <>
            <p className="auth-user">{user?.name || user?.email || 'Authenticated user'}</p>
            <button type="button" className="auth-btn" onClick={logout}>
              Logout
            </button>
          </>
        ) : (
          <button type="button" className="auth-btn" onClick={login} disabled={isLoading}>
            {isLoading ? 'Loading...' : 'Login'}
          </button>
        )}
      </div>
      <nav className="sidebar-nav">
        {links.map((link) => (
          <NavLink
            key={link.to}
            to={link.to}
            className={({ isActive }) =>
              `nav-link${isActive ? ' nav-link-active' : ''}`
            }
          >
            {link.label}
          </NavLink>
        ))}
      </nav>
    </aside>
  )
}

export default SidebarNavigation
