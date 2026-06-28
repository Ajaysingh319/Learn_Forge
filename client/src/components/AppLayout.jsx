import { Outlet } from 'react-router-dom'
import ErrorBoundary from './ErrorBoundary'
import SidebarNavigation from './SidebarNavigation'
import TopBar from './TopBar'

function AppLayout() {
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
