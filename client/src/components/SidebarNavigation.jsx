import { NavLink } from 'react-router-dom'

const links = [
  { to: '/', label: 'Home' },
  { to: '/course/demo-course', label: 'Course Overview' },
  { to: '/lesson/demo-lesson', label: 'Lesson Viewer' },
]

function SidebarNavigation() {
  return (
    <aside className="sidebar">
      <div className="brand">LearnForge</div>
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
