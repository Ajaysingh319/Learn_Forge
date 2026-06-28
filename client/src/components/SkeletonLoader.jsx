function SkeletonLine({ width = '100%' }) {
  return <div className="skeleton-line" style={{ width }} />
}

function SkeletonCard({ lines = 3 }) {
  return (
    <section className="card skeleton-card" aria-hidden="true">
      <SkeletonLine width="45%" />
      {Array.from({ length: lines }).map((_, index) => (
        <SkeletonLine key={`line-${index}`} width={index === lines - 1 ? '70%' : '100%'} />
      ))}
    </section>
  )
}

function SkeletonList({ count = 3 }) {
  return (
    <div className="skeleton-list" aria-hidden="true">
      {Array.from({ length: count }).map((_, index) => (
        <div key={`item-${index}`} className="skeleton-list-item">
          <SkeletonLine width="55%" />
          <SkeletonLine width="35%" />
        </div>
      ))}
    </div>
  )
}

function LessonSkeleton() {
  return (
    <section className="page">
      <SkeletonCard lines={2} />
      <SkeletonCard lines={5} />
      <SkeletonCard lines={4} />
    </section>
  )
}

export { LessonSkeleton, SkeletonCard, SkeletonLine, SkeletonList }
