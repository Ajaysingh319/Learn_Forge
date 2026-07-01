import { Outlet } from 'react-router-dom'
import ErrorBoundary from './ErrorBoundary'
import SidebarNavigation from './SidebarNavigation'
import TopBar from './TopBar'
import LandingPage from '../pages/LandingPage'
import useAuth from '../hooks/useAuth'

function Splash() {
  return (
    <div className="splash">
      <div className="splash-logo">
        <span className="logo-mark logo-mark-lg">LF</span>
        <span className="splash-name">LearnForge</span>
      </div>
      <div className="splash-bar"><span /></div>
      <p className="splash-text">Preparing your learning space…</p>
    </div>
  )
}

function AppLayout() {
  const { isLoading, isAuthenticated } = useAuth()

  if (isLoading) {
    return <Splash />
  }

  if (!isAuthenticated) {
    return <LandingPage />
  }

  return (
    <div className="app-layout">
      <SidebarNavigation />
      <div className="app-main">
        <TopBar />
        <main className="app-content">
          <ErrorBoundary>
            <Outlet />
          </ErrorBoundary>
        </main>
      </div>
    </div>
  )
}

export default AppLayout
