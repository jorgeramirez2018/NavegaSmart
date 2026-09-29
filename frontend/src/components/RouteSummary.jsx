import { formatInt, formatKm, formatMin, formatMs, strategyColor } from '../format.js'

export default function RouteSummary({ route, strategies }) {
  const color = strategyColor(strategies, route.strategyKey)
  const stops = route.path

  return (
    <article className="summary" style={{ '--route-color': color }}>
      <header className="summary-head">
        <span className="line-badge" aria-hidden="true" />
        <h2>{route.strategyName}</h2>
      </header>

      <dl className="metrics">
        <div className="metric metric-main">
          <dt>Distancia</dt>
          <dd>{formatKm(route.totalDistanceKm)}<small>km</small></dd>
        </div>
        <div className="metric metric-main">
          <dt>Tiempo estimado</dt>
          <dd>{formatMin(route.totalTimeMin)}<small>min</small></dd>
        </div>
        <div className="metric">
          <dt>Tramos</dt>
          <dd>{formatInt(route.hops)}</dd>
        </div>
        <div className="metric">
          <dt>Nodos explorados</dt>
          <dd>{formatInt(route.nodesExplored)}</dd>
        </div>
        <div className="metric">
          <dt>Tiempo de cálculo</dt>
          <dd>{formatMs(route.executionTimeMs)}<small>ms</small></dd>
        </div>
      </dl>

      <ol className="itinerary" aria-label="Paradas del recorrido">
        {stops.map((stop, index) => (
          <li
            key={stop.id}
            className={index === 0 || index === stops.length - 1 ? 'stop is-endpoint' : 'stop'}
          >
            <span className="stop-name">{stop.name}</span>
            {index === 0 && <span className="stop-role">Salida</span>}
            {index === stops.length - 1 && <span className="stop-role">Llegada</span>}
          </li>
        ))}
      </ol>
    </article>
  )
}
