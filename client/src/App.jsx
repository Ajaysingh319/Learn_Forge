import { Outlet } from 'react-router-dom'
import SidebarNavigation from './components/SidebarNavigation'

function App() {
  return (
    <div className="app-layout">
      <SidebarNavigation />
      <main className="app-content">
        <Outlet />
      </main>
    </div>
  )
}

export default App
