function CodeBlock({ language = 'text', text = '' }) {
  return (
    <div className="block-code">
      <div className="block-code-header">{language}</div>
      <pre>
        <code>{text}</code>
      </pre>
    </div>
  )
}

export default CodeBlock
