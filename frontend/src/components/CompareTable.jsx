import { formatInt, formatKm, formatMin, formatMs, strategyColor } from '../format.js'

// Resalta el mejor valor de cada columna para que la diferencia entre criterios sea evidente.
const best = (routes, field) => Math.min(...routes.map((r) => r[field]))

export default function CompareTable({ routes, strategies, highlightKey, onSelect }) {
  const bestKm = best(routes, 'totalDistanceKm')
  const bestMin = best(routes, 'totalTimeMin')
  const bestHops = best(routes, 'hops')
  const mark = (value, target) => (Math.abs(value - target) < 1e-9 ? 'is-best' : undefined)

  return (
    <div className="table-wrap">
      <table className="data-table compare">
        <caption>Mismo origen y destino con cada criterio. Selecciona una fila para verla en el mapa.</caption>
        <thead>
          <tr>
            <th scope="col">Criterio</th>
            <th scope="col" className="num">Distancia (km)</th>
            <th scope="col" className="num">Tiempo (min)</th>
            <th scope="col" className="num">Tramos</th>
            <th scope="col" className="num">Nodos explorados</th>
            <th scope="col" className="num">Cálculo (ms)</th>
          </tr>
        </thead>
        <tbody>
          {routes.map((r) => (
            <tr
              key={r.strategyKey}
              className={r.strategyKey === highlightKey ? 'is-selected' : undefined}
              onClick={() => onSelect(r.strategyKey)}
              tabIndex={0}
              onKeyDown={(e) => (e.key === 'Enter' || e.key === ' ') && (e.preventDefault(), onSelect(r.strategyKey))}
              aria-current={r.strategyKey === highlightKey ? "true" : undefined}
            >
              <th scope="row">
                <span className="swatch" style={{ background: strategyColor(strategies, r.strategyKey) }} />
                {r.strategyName}
              </th>
              <td className={`num ${mark(r.totalDistanceKm, bestKm) ?? ''}`}>{formatKm(r.totalDistanceKm)}</td>
              <td className={`num ${mark(r.totalTimeMin, bestMin) ?? ''}`}>{formatMin(r.totalTimeMin)}</td>
              <td className={`num ${mark(r.hops, bestHops) ?? ''}`}>{formatInt(r.hops)}</td>
              <td className="num">{formatInt(r.nodesExplored)}</td>
              <td className="num">{formatMs(r.executionTimeMs)}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
