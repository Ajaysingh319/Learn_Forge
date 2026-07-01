import useAuth from '../hooks/useAuth'

const FEATURES = [
  {
    icon: '✨',
    title: 'Prompt → Course',
    description: 'Type any topic and get a structured multi-module course in seconds.',
  },
  {
    icon: '🧠',
    title: 'Rich AI lessons',
    description: 'Objectives, explanations, code, quizzes, and curated resources.',
  },
  {
    icon: '🎬',
    title: 'Video + Hinglish audio',
    description: 'Embedded YouTube lessons and Hinglish narration for accessibility.',
  },
  {
    icon: '📄',
    title: 'Export to PDF',
    description: 'Download any lesson as a polished, offline-ready PDF.',
  },
]

function LandingPage() {
  const { login, isLoading } = useAuth()

  return (
    <div className="landing">
      <div className="landing-orb landing-orb-1" aria-hidden="true" />
      <div className="landing-orb landing-orb-2" aria-hidden="true" />

      <header className="landing-topbar">
        <div className="landing-logo">
          <span className="logo-mark" aria-hidden="true">LF</span>
          <span className="logo-word">LearnForge</span>
        </div>
        <button type="button" className="btn btn-ghost-light" onClick={login} disabled={isLoading}>
          {isLoading ? 'Loading…' : 'Sign in'}
        </button>
      </header>

      <main className="landing-hero">
        <section className="landing-copy">
          <span className="landing-badge">⚡ AI-powered learning</span>
          <h1 className="landing-title">
            Turn any topic into a<br />
            <span className="gradient-text">complete online course</span>
          </h1>
          <p className="landing-subtitle">
            LearnForge transforms a single prompt — like “Intro to React Hooks” or “Basics of
            Copyright Law” — into a structured course with modules, lessons, quizzes, videos, and
            Hinglish narration. Learn anything, instantly.
          </p>

          <div className="landing-cta">
            <button type="button" className="btn btn-primary btn-lg" onClick={login} disabled={isLoading}>
              {isLoading ? 'Loading…' : 'Get started — it’s free'}
            </button>
            <span className="landing-cta-note">Secure sign-in with Auth0</span>
          </div>

          <div className="landing-features">
            {FEATURES.map((feature) => (
              <div key={feature.title} className="landing-feature">
                <span className="landing-feature-icon" aria-hidden="true">{feature.icon}</span>
                <div>
                  <h3>{feature.title}</h3>
                  <p>{feature.description}</p>
                </div>
              </div>
            ))}
          </div>
        </section>

        <section className="landing-preview" aria-hidden="true">
          <div className="preview-card">
            <div className="preview-card-head">
              <span className="preview-dot" />
              <span className="preview-dot" />
              <span className="preview-dot" />
            </div>
            <div className="preview-prompt">
              <span className="preview-prompt-label">Topic</span>
              <span className="preview-prompt-text">Machine Learning Basics</span>
            </div>
            <div className="preview-modules">
              <div className="preview-module">
                <span className="preview-module-index">1</span>
                <div>
                  <strong>Foundations of ML</strong>
                  <small>4 lessons · quiz · video</small>
                </div>
              </div>
              <div className="preview-module">
                <span className="preview-module-index">2</span>
                <div>
                  <strong>Supervised Learning</strong>
                  <small>5 lessons · quiz · video</small>
                </div>
              </div>
              <div className="preview-module">
                <span className="preview-module-index">3</span>
                <div>
                  <strong>Model Evaluation</strong>
                  <small>3 lessons · quiz · video</small>
                </div>
              </div>
            </div>
            <div className="preview-chips">
              <span className="preview-chip">Objectives</span>
              <span className="preview-chip">MCQs</span>
              <span className="preview-chip">Resources</span>
              <span className="preview-chip">PDF</span>
            </div>
          </div>
        </section>
      </main>

      <footer className="landing-footer">
        <span>Built with React · Spring Boot · MongoDB · Gemini</span>
      </footer>
    </div>
  )
}

export default LandingPage
