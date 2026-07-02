import { NavLink } from 'react-router-dom'
import useAuth from '../hooks/useAuth'

const HomeIcon = () => (
  <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
    <path d="M3 10.5 12 3l9 7.5" />
    <path d="M5 9.5V21h14V9.5" />
  </svg>
)

const links = [{ to: '/', label: 'Dashboard', end: true, icon: <HomeIcon /> }]

const DISPLAY_NAME = import.meta.env.VITE_DISPLAY_NAME

function initialsFor(user) {
  const source = DISPLAY_NAME || user?.name || user?.email || 'U'
  return source.trim().slice(0, 1).toUpperCase()
}

function SidebarNavigation() {
  const { user, logout } = useAuth()

  return (
    <aside className="sidebar">
      <div className="sidebar-brand">
        <span className="logo-mark" aria-hidden="true">LF</span>
        <div>
          <div className="brand">LearnForge</div>
          <p className="brand-tagline">Text-to-Learn</p>
        </div>
      </div>

      <nav className="sidebar-nav" aria-label="Primary">
        {links.map((link) => (
          <NavLink
            key={link.to}
            to={link.to}
            end={link.end}
            className={({ isActive }) => `nav-link${isActive ? ' nav-link-active' : ''}`}
          >
            <span className="nav-link-icon">{link.icon}</span>
            {link.label}
          </NavLink>
        ))}
      </nav>

      <div className="sidebar-footer">
        <div className="user-card">
          <span className="user-avatar" aria-hidden="true">{initialsFor(user)}</span>
          <span className="user-name">{DISPLAY_NAME || user?.name || user?.email || 'Signed in'}</span>
        </div>
        <button type="button" className="btn btn-ghost-dark" onClick={logout}>
          Log out
        </button>
      </div>
    </aside>
  )
}

export default SidebarNavigation
