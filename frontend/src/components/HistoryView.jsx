import { useCallback, useEffect, useState } from 'react'
import { api } from '../api.js'
import { downloadCsv, formatInt, formatKm, formatMin, formatMs, strategyColor } from '../format.js'

const dateFormat = new Intl.DateTimeFormat('es-CO', { dateStyle: 'short', timeStyle: 'medium' })

export default function HistoryView({ graph, strategies }) {
  const [rows, setRows] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  const nodeName = (id) => graph.nodes.find((n) => n.id === id)?.name ?? '—'
  const strategyName = (key) => strategies.find((s) => s.key === key)?.name ?? key

  const load = useCallback(() => {
    setLoading(true)
    setError(null)
    api.getExecutions(100)
      .then(setRows)
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false))
  }, [])

  useEffect(load, [load])

  const exportCsv = () =>
    downloadCsv(
      'navegasmart-historial.csv',
      ['fecha', 'tipo', 'estrategia', 'origen', 'destino', 'nodos', 'conexiones', 'tramos', 'nodos_explorados', 'distancia_km', 'tiempo_min', 'calculo_ms'],
      rows.map((r) => [r.createdAt, r.runType, r.strategyKey,
        r.originId ? nodeName(r.originId) : '', r.destinationId ? nodeName(r.destinationId) : '',
        r.nodeCount, r.edgeCount, r.hops, r.nodesExplored, r.totalDistanceKm?.toFixed(2), r.totalTimeMin?.toFixed(2),
        r.executionTimeMs.toFixed(4)]),
    )

  return (
    <section className="panel">
      <div className="section-head">
        <div>
          <h1 className="panel-title">Historial de ejecuciones</h1>
          <p className="hint">Cada cálculo de ruta y cada prueba de rendimiento queda registrado en PostgreSQL.</p>
        </div>
        <div className="button-row">
          <button className="button button-quiet" onClick={load} disabled={loading}>Actualizar</button>
          <button className="button button-quiet" onClick={exportCsv} disabled={rows.length === 0}>Descargar CSV</button>
        </div>
      </div>

      {error && <p className="notice notice-error" role="alert">{error}</p>}
      {!error && !loading && rows.length === 0 && (
        <p className="empty">Todavía no hay registros. Calcula una ruta o ejecuta una prueba de rendimiento.</p>
      )}

      {rows.length > 0 && (
        <div className="table-wrap">
          <table className="data-table">
            <thead>
              <tr>
                <th scope="col">Fecha</th>
                <th scope="col">Tipo</th>
                <th scope="col">Criterio</th>
                <th scope="col">Recorrido</th>
                <th scope="col" className="num">Nodos</th>
                <th scope="col" className="num">Tramos</th>
                <th scope="col" className="num">Distancia (km)</th>
                <th scope="col" className="num">Tiempo (min)</th>
                <th scope="col" className="num">Cálculo (ms)</th>
              </tr>
            </thead>
            <tbody>
              {rows.map((r) => (
                <tr key={r.id}>
                  <td>{r.createdAt ? dateFormat.format(new Date(r.createdAt)) : ''}</td>
                  <td>{r.runType === 'BENCHMARK' ? 'Prueba de rendimiento' : 'Ruta'}</td>
                  <th scope="row">
                    <span className="swatch" style={{ background: strategyColor(strategies, r.strategyKey) }} />
                    {strategyName(r.strategyKey)}
                  </th>
                  <td>{r.originId ? `${nodeName(r.originId)} a ${nodeName(r.destinationId)}` : 'Red sintética'}</td>
                  <td className="num">{formatInt(r.nodeCount)}</td>
                  <td className="num">{r.hops != null ? formatInt(r.hops) : ''}</td>
                  <td className="num">{r.totalDistanceKm != null ? formatKm(r.totalDistanceKm) : ''}</td>
                  <td className="num">{r.totalTimeMin != null ? formatMin(r.totalTimeMin) : ''}</td>
                  <td className="num">{formatMs(r.executionTimeMs)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </section>
  )
}
