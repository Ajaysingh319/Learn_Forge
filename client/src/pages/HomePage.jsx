import PromptForm from '../components/PromptForm'
import { useAppContext } from '../context/AppContext'

function HomePage() {
  const { setLastPrompt, lastPrompt } = useAppContext()

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
    </section>
  )
}

export default HomePage
