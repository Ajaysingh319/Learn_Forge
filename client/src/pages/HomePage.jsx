import PromptForm from '../components/PromptForm'
import { useAppContext } from '../context/AppContext'
import useAuth from '../hooks/useAuth'

function HomePage() {
  const { setLastPrompt, lastPrompt } = useAppContext()
  const { isAuthenticated, user } = useAuth()

  return (
    <section className="page">
      <header>
        <h1>Text-to-Learn Course Generator</h1>
        <p>Submit any topic and generate a structured course outline.</p>
      </header>

      <PromptForm onSubmit={setLastPrompt} />

      <section className="card">
        <h2>Latest Prompt</h2>
        <p>{lastPrompt || 'No prompt submitted in this session yet.'}</p>
      </section>

      <section className="card">
        <h2>Auth Status</h2>
        <p>
          {isAuthenticated
            ? `Signed in as ${user?.email || user?.name || user?.sub}`
            : 'Not signed in yet. Login to access protected routes and save courses.'}
        </p>
      </section>
    </section>
  )
}

export default HomePage
