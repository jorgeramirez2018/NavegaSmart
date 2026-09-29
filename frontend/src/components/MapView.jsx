import { useMemo } from 'react'
import { strategyColor } from '../format.js'

const WIDTH = 1000
const MARGIN = 70

const ROAD_STYLES = {
  avenida: { label: 'Avenida', width: 9, className: 'road road-avenida' },
  perimetral: { label: 'Perimetral', width: 7, className: 'road road-perimetral' },
  local: { label: 'Calle local', width: 5, className: 'road road-local' },
  centro: { label: 'Centro congestionado', width: 5, className: 'road road-centro' },
}

const LABEL_FONT = 23
const LABEL_CHAR_WIDTH = LABEL_FONT * 0.56 // estimación conservadora del ancho promedio de un carácter
// Posiciones candidatas para la etiqueta de cada punto (desplazamiento respecto al centro).
const LABEL_SLOTS = [
  { dx: 22, dy: -14, anchor: 'start' },
  { dx: -22, dy: -14, anchor: 'end' },
  { dx: 0, dy: -26, anchor: 'middle' },
  { dx: 22, dy: 30, anchor: 'start' },
  { dx: -22, dy: 30, anchor: 'end' },
  { dx: 0, dy: 44, anchor: 'middle' },
]

function segmentHitsBox(a, b, box) {
  // Recorte de Liang-Barsky: ¿el segmento a-b atraviesa el rectángulo?
  const dx = b.x - a.x
  const dy = b.y - a.y
  const p = [-dx, dx, -dy, dy]
  const q = [a.x - box.x1, box.x2 - a.x, a.y - box.y1, box.y2 - a.y]
  let t0 = 0
  let t1 = 1
  for (let i = 0; i < 4; i++) {
    if (p[i] === 0) {
      if (q[i] < 0) return false
    } else {
      const t = q[i] / p[i]
      if (p[i] < 0) {
        if (t > t1) return false
        if (t > t0) t0 = t
      } else {
        if (t < t0) return false
        if (t < t1) t1 = t
      }
    }
  }
  return true
}

const boxesOverlap = (a, b) => a.x1 < b.x2 && b.x1 < a.x2 && a.y1 < b.y2 && b.y1 < a.y2

// Coloca cada etiqueta en la posición que menos choca con calles, puntos y otras etiquetas.
function placeLabels(nodes, points, connections, height) {
  const segments = connections.map((c) => [points[c.fromId], points[c.toId]])
  const placed = new Map()

  const evaluate = (node, slot, others) => {
    const p = points[node.id]
    const width = node.name.length * LABEL_CHAR_WIDTH
    let x = p.x + slot.dx
    let x1 = slot.anchor === 'start' ? x : slot.anchor === 'end' ? x - width : x - width / 2
    let penalty = 0
    const shift = x1 < 6 ? 6 - x1 : x1 + width > WIDTH - 6 ? WIDTH - 6 - (x1 + width) : 0
    if (shift !== 0) {
      x += shift
      x1 += shift
      penalty += 4
    }
    const baseline = p.y + slot.dy
    const box = { x1: x1 - 4, x2: x1 + width + 4, y1: baseline - LABEL_FONT * 0.8 - 4, y2: baseline + LABEL_FONT * 0.25 + 4 }
    if (box.y1 < 0 || box.y2 > height) penalty += 100
    segments.forEach(([a, b]) => { if (segmentHitsBox(a, b, box)) penalty += 10 })
    nodes.forEach((n) => {
      const q = points[n.id]
      if (q.x > box.x1 - 12 && q.x < box.x2 + 12 && q.y > box.y1 - 12 && q.y < box.y2 + 12) penalty += 25
    })
    others.forEach((other) => { if (boxesOverlap(box, other.box)) penalty += 40 })
    return { x, y: baseline, anchor: slot.anchor, box, penalty }
  }

  const best = (node, others) =>
    LABEL_SLOTS.map((slot, i) => {
      const result = evaluate(node, slot, others)
      return { ...result, penalty: result.penalty + i * 0.5 }
    }).reduce((a, b) => (b.penalty < a.penalty ? b : a))

  // Dos pasadas: la segunda recoloca cada etiqueta conociendo la posición de todas las demás.
  nodes.forEach((node) => placed.set(node.id, best(node, [...placed.values()])))
  nodes.forEach((node) => {
    const others = [...placed.entries()].filter(([id]) => id !== node.id).map(([, v]) => v)
    placed.set(node.id, best(node, others))
  })
  return placed
}

function useProjection(nodes) {
  return useMemo(() => {
    const xs = nodes.map((n) => n.x)
    const ys = nodes.map((n) => n.y)
    const minX = Math.min(...xs)
    const minY = Math.min(...ys)
    const spanX = Math.max(Math.max(...xs) - minX, 1)
    const spanY = Math.max(Math.max(...ys) - minY, 1)
    const aspect = Math.min(Math.max(spanY / spanX, 0.45), 0.9)
    const height = Math.round(WIDTH * aspect)
    const project = (node) => ({
      x: MARGIN + ((node.x - minX) / spanX) * (WIDTH - 2 * MARGIN),
      y: MARGIN + ((node.y - minY) / spanY) * (height - 2 * MARGIN),
    })
    return { height, project }
  }, [nodes])
}

export default function MapView({ graph, strategies, routes, highlightKey, originId, destinationId, onNodeClick }) {
  const { height, project } = useProjection(graph.nodes)
  const points = useMemo(
    () => Object.fromEntries(graph.nodes.map((n) => [n.id, project(n)])),
    [graph.nodes, project],
  )

  const labels = useMemo(
    () => placeLabels(graph.nodes, points, graph.connections, height),
    [graph.nodes, graph.connections, points, height],
  )

  const compare = routes.length > 1
  // En comparación, cada ruta se dibuja más delgada que la anterior: donde comparten
  // tramo se ven como franjas paralelas, igual que las líneas de un mapa de metro.
  const drawn = routes.map((route, index) => ({
    route,
    color: strategyColor(strategies, route.strategyKey),
    width: compare ? 8 + (routes.length - 1 - index) * 6 : 14,
    points: route.path.map((n) => `${points[n.id].x},${points[n.id].y}`).join(' '),
  }))

  const highlighted = routes.find((r) => r.strategyKey === highlightKey)
  const onRoute = new Set(highlighted?.path.map((n) => n.id) ?? [])
  const roadTypes = [...new Set(graph.connections.map((c) => c.roadType))]

  const handleKey = (event, nodeId) => {
    if (event.key === 'Enter' || event.key === ' ') {
      event.preventDefault()
      onNodeClick(nodeId)
    }
  }

  return (
    <figure className="map">
      <div className="map-scroll">
      <svg viewBox={`0 0 ${WIDTH} ${height}`} role="img" aria-label="Mapa de puntos y conexiones de la red">
        <g>
          {graph.connections.map((c, i) => {
            const a = points[c.fromId]
            const b = points[c.toId]
            const style = ROAD_STYLES[c.roadType] ?? ROAD_STYLES.local
            return (
              <g key={i}>
                <line className={style.className} x1={a.x} y1={a.y} x2={b.x} y2={b.y} strokeWidth={style.width} />
                {!c.bidirectional && <OneWayArrow from={a} to={b} />}
              </g>
            )
          })}
        </g>

        <g>
          {drawn.map(({ route, color, width, points: pts }) => (
            <g key={`${route.strategyKey}-${pts}`} className="route" style={{ '--route-color': color }}>
              <polyline className="route-casing" points={pts} strokeWidth={width + 6} pathLength="1" />
              <polyline
                className={route.strategyKey === highlightKey || !compare ? 'route-line' : 'route-line is-dimmed'}
                points={pts}
                strokeWidth={width}
                pathLength="1"
              />
            </g>
          ))}
        </g>

        <g>
          {graph.nodes.map((node) => {
            const p = points[node.id]
            const isEndpoint = node.id === originId || node.id === destinationId
            const label = labels.get(node.id)
            return (
              <g
                key={node.id}
                className={isEndpoint ? 'station is-endpoint' : onRoute.has(node.id) ? 'station is-on-route' : 'station'}
                role="button"
                tabIndex={0}
                aria-label={`${node.name}${node.id === originId ? ' (origen)' : ''}${node.id === destinationId ? ' (destino)' : ''}`}
                onClick={() => onNodeClick(node.id)}
                onKeyDown={(e) => handleKey(e, node.id)}
              >
                <circle className="station-hit" cx={p.x} cy={p.y} r={26} />
                <circle className="station-dot" cx={p.x} cy={p.y} r={isEndpoint ? 15 : 10} />
                {isEndpoint && <circle className="station-core" cx={p.x} cy={p.y} r={5} />}
                <text className="station-label" x={label.x} y={label.y} textAnchor={label.anchor}>
                  {node.name}
                </text>
              </g>
            )
          })}
        </g>
      </svg>
      </div>

      <figcaption className="legend">
        {roadTypes.map((type) => {
          const style = ROAD_STYLES[type] ?? ROAD_STYLES.local
          return (
            <span key={type} className="legend-item">
              <svg viewBox="0 0 36 12" aria-hidden="true">
                <line className={style.className} x1="2" y1="6" x2="34" y2="6" strokeWidth={Math.min(style.width, 8)} />
              </svg>
              {style.label}
            </span>
          )
        })}
        {graph.connections.some((c) => !c.bidirectional) && (
          <span className="legend-item">
            <svg viewBox="0 0 36 12" aria-hidden="true"><path className="oneway" d="M12 1 L24 6 L12 11 Z" /></svg>
            Un solo sentido
          </span>
        )}
      </figcaption>
    </figure>
  )
}

function OneWayArrow({ from, to }) {
  const mx = (from.x + to.x) / 2
  const my = (from.y + to.y) / 2
  const angle = (Math.atan2(to.y - from.y, to.x - from.x) * 180) / Math.PI
  return <path className="oneway" d="M-9 -8 L11 0 L-9 8 Z" transform={`translate(${mx} ${my}) rotate(${angle})`} />
}
