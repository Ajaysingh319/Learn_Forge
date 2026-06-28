import { Component } from 'react'
import ErrorMessage from './ErrorMessage'

class ErrorBoundary extends Component {
  constructor(props) {
    super(props)
    this.state = { hasError: false, message: '' }
  }

  static getDerivedStateFromError(error) {
    return {
      hasError: true,
      message: error?.message || 'Unexpected application error',
    }
  }

  render() {
    if (this.state.hasError) {
      return (
        <section className="page">
          <ErrorMessage message={this.state.message} />
          <button
            type="button"
            onClick={() => this.setState({ hasError: false, message: '' })}
          >
            Try Again
          </button>
        </section>
      )
    }

    return this.props.children
  }
}

export default ErrorBoundary
