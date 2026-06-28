import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { BrowserRouter, Route, Routes } from 'react-router-dom'
import './index.css'
import AppLayout from './App.jsx'
import { AppProvider } from './context/AppContext'
import AuthProviderWrapper from './context/AuthProviderWrapper'
import { ToastProvider } from './context/ToastContext'
import ProtectedRoute from './components/ProtectedRoute'
import CoursePage from './pages/CoursePage'
import HomePage from './pages/HomePage'
import LessonPage from './pages/LessonPage'
import NotFoundPage from './pages/NotFoundPage'

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <AuthProviderWrapper>
      <BrowserRouter>
        <AppProvider>
          <ToastProvider>
            <Routes>
            <Route path="/" element={<AppLayout />}>
              <Route index element={<HomePage />} />
              <Route
                path="course/:courseId"
                element={
                  <ProtectedRoute>
                    <CoursePage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="courses/:courseId/module/:moduleIndex/lesson/:lessonIndex"
                element={
                  <ProtectedRoute>
                    <LessonPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="lesson/:id"
                element={
                  <ProtectedRoute>
                    <LessonPage />
                  </ProtectedRoute>
                }
              />
              <Route path="*" element={<NotFoundPage />} />
            </Route>
            </Routes>
          </ToastProvider>
        </AppProvider>
      </BrowserRouter>
    </AuthProviderWrapper>
  </StrictMode>,
)
