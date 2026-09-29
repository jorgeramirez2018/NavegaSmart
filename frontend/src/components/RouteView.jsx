import { useState } from 'react'
import { api } from '../api.js'
import MapView from './MapView.jsx'
import RouteSummary from './RouteSummary.jsx'
import CompareTable from './CompareTable.jsx'

export default function RouteView({ graph, strategies }) {
  const [originId, setOriginId] = useState('')
  const [destinationId, setDestinationId] = useState('')
  const [strategyKey, setStrategyKey] = useState(strategies[0]?.key ?? '')
  const [routes, setRoutes] = useState([])        // una ruta (cálculo) o varias (comparación)
  const [mode, setMode] = useState('single')      // 'single' | 'compare'
  const [highlightKey, setHighlightKey] = useState(null)
  const [pending, setPending] = useState(null)    // acción en curso: 'single' | 'compare' | null
  const [error, setError] = useState(null)

  const ready = originId !== '' && destinationId !== '' && originId !== destinationId

  const resetResult = () => {
    setRoutes([])
    setError(null)
  }

  const pickFromMap = (nodeId) => {
    const id = String(nodeId)
    resetResult()
    if (originId === '' || (originId !== '' && destinationId !== '')) {
      setOriginId(id)
      setDestinationId('')
    } else if (id !== originId) {
      setDestinationId(id)
    }
  }

  const swap = () => {
    setOriginId(destinationId)
    setDestinationId(originId)
    resetResult()
  }

  const run = async (action) => {
    setPending(action)
    setError(null)
    try {
      if (action === 'single') {
        const route = await api.calculateRoute(Number(originId), Number(destinationId), strategyKey)
        setRoutes([route])
        setHighlightKey(route.strategyKey)
      } else {
        const results = await api.compareRoutes(Number(originId), Number(destinationId))
        setRoutes(results)
        setHighlightKey(results[0]?.strategyKey ?? null)
      }
      setMode(action)
    } catch (e) {
      setRoutes([])
      setError(e.message)
    } finally {
      setPending(null)
    }
  }

  const sortedNodes = [...graph.nodes].sort((a, b) => a.name.localeCompare(b.name, 'es'))
  const highlighted = routes.find((r) => r.strategyKey === highlightKey) ?? routes[0]

  return (
    <div className="route-layout">
      <aside className="panel controls">
        <h1 className="panel-title">Planear un recorrido</h1>
        <p className="hint">Elige origen y destino en las listas o haciendo clic en los puntos del mapa.</p>

        <div className="endpoints">
          <label className="field">
            <span className="field-label">Origen</span>
            <select value={originId} onChange={(e) => { setOriginId(e.target.value); resetResult() }}>
              <option value="">Selecciona un punto</option>
              {sortedNodes.map((n) => (
                <option key={n.id} value={n.id}>{n.name}</option>
              ))}
            </select>
          </label>
          <button className="swap" onClick={swap} aria-label="Intercambiar origen y destino" title="Intercambiar origen y destino">
            <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M7 4v14M7 18l-3-3M7 18l3-3M17 20V6M17 6l-3 3M17 6l3 3" /></svg>
          </button>
          <label className="field">
            <span className="field-label">Destino</span>
            <select value={destinationId} onChange={(e) => { setDestinationId(e.target.value); resetResult() }}>
              <option value="">Selecciona un punto</option>
              {sortedNodes.map((n) => (
                <option key={n.id} value={n.id} disabled={String(n.id) === originId}>{n.name}</option>
              ))}
            </select>
          </label>
        </div>

        <fieldset className="strategies">
          <legend className="field-label">Criterio</legend>
          {strategies.map((s) => (
            <label key={s.key} className={strategyKey === s.key ? 'strategy is-selected' : 'strategy'}>
              <input
                type="radio"
                name="strategy"
                value={s.key}
                checked={strategyKey === s.key}
                onChange={() => setStrategyKey(s.key)}
              />
              <span className="strategy-name">{s.name}</span>
              <span className="strategy-desc">{s.description}</span>
            </label>
          ))}
        </fieldset>

        <div className="actions">
          <button className="button button-primary" disabled={!ready || pending !== null} onClick={() => run('single')}>
            {pending === 'single' ? 'Calculando…' : 'Calcular ruta'}
          </button>
          <button className="button button-secondary" disabled={!ready || pending !== null} onClick={() => run('compare')}>
            {pending === 'compare' ? 'Comparando…' : 'Comparar criterios'}
          </button>
        </div>

        {error && <p className="notice notice-error" role="alert">{error}</p>}
      </aside>

      <section className="map-area" aria-label="Mapa de la red">
        <MapView
          graph={graph}
          strategies={strategies}
          routes={routes}
          highlightKey={highlighted?.strategyKey}
          originId={originId === '' ? null : Number(originId)}
          destinationId={destinationId === '' ? null : Number(destinationId)}
          onNodeClick={pickFromMap}
        />
      </section>

      <section className="results">
        {routes.length === 0 && !error && (
          <p className="empty">La ruta aparecerá aquí con su distancia, tiempo y cada parada del recorrido.</p>
        )}
        {mode === 'compare' && routes.length > 0 && (
          <CompareTable
            routes={routes}
            strategies={strategies}
            highlightKey={highlighted?.strategyKey}
            onSelect={setHighlightKey}
          />
        )}
        {highlighted && <RouteSummary route={highlighted} strategies={strategies} />}
      </section>
    </div>
  )
}
