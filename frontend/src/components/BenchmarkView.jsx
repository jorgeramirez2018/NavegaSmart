import { useState } from 'react'
import { CartesianGrid, Legend, Line, LineChart, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts'
import { api } from '../api.js'
import { downloadCsv, formatInt, formatKm, formatMin, formatMs, strategyColor } from '../format.js'

const DEFAULT_SIZES = '100, 500, 1000, 5000, 10000, 50000'

function parseSizes(text) {
  return text
    .split(/[\s,;]+/)
    .filter(Boolean)
    .map((value) => Number(value))
}

export default function BenchmarkView({ strategies }) {
  const [sizesText, setSizesText] = useState(DEFAULT_SIZES)
  const [repetitions, setRepetitions] = useState(10)
  const [warmupRuns, setWarmupRuns] = useState(3)
  const [results, setResults] = useState([])
  const [running, setRunning] = useState(false)
  const [error, setError] = useState(null)

  const sizes = parseSizes(sizesText)
  const sizesValid = sizes.length > 0 && sizes.length <= 8 && sizes.every((n) => Number.isInteger(n) && n >= 10 && n <= 200000)

  const run = async () => {
    setRunning(true)
    setError(null)
    try {
      setResults(await api.runBenchmark(sizes, Number(repetitions), Number(warmupRuns)))
    } catch (e) {
      setError(e.message)
    } finally {
      setRunning(false)
    }
  }

  // Una fila por tamaño de grafo, una columna por estrategia (formato que espera Recharts).
  const chartData = [...new Set(results.map((r) => r.nodeCount))].map((nodeCount) => {
    const row = { nodeCount }
    results.filter((r) => r.nodeCount === nodeCount).forEach((r) => { row[r.strategyKey] = r.averageMs })
    return row
  })
  const keysInResults = [...new Set(results.map((r) => r.strategyKey))]
  const nameOf = (key) => strategies.find((s) => s.key === key)?.name ?? key

  const exportCsv = () =>
    downloadCsv(
      'navegasmart-rendimiento.csv',
      ['estrategia', 'nodos', 'conexiones', 'repeticiones', 'promedio_ms', 'min_ms', 'max_ms', 'nodos_explorados', 'tramos', 'distancia_km', 'tiempo_min'],
      results.map((r) => [r.strategyKey, r.nodeCount, r.connectionCount, r.repetitions, r.averageMs.toFixed(4),
        r.minMs.toFixed(4), r.maxMs.toFixed(4), r.nodesExplored, r.hops, r.totalDistanceKm.toFixed(2), r.totalTimeMin.toFixed(2)]),
    )

  return (
    <div className="stack">
      <section className="panel">
        <h1 className="panel-title">Prueba de rendimiento y escalabilidad</h1>
        <p className="hint">
          Se genera una red sintética tipo cuadrícula para cada tamaño y se calcula la ruta entre esquinas opuestas
          con todos los criterios. El resultado es el tiempo promedio de cálculo, sin contar el calentamiento de la JVM.
        </p>

        <div className="bench-form">
          <label className="field field-wide">
            <span className="field-label">Tamaños de grafo (nodos)</span>
            <input value={sizesText} onChange={(e) => setSizesText(e.target.value)} aria-invalid={!sizesValid} />
            <span className="field-help">Hasta 8 valores entre 10 y 200 000, separados por comas.</span>
          </label>
          <label className="field">
            <span className="field-label">Repeticiones</span>
            <input type="number" min="1" max="50" value={repetitions} onChange={(e) => setRepetitions(e.target.value)} />
          </label>
          <label className="field">
            <span className="field-label">Calentamiento</span>
            <input type="number" min="0" max="20" value={warmupRuns} onChange={(e) => setWarmupRuns(e.target.value)} />
          </label>
          <button className="button button-primary" onClick={run} disabled={!sizesValid || running}>
            {running ? 'Midiendo…' : 'Ejecutar prueba'}
          </button>
        </div>
        {error && <p className="notice notice-error" role="alert">{error}</p>}
      </section>

      {results.length === 0 && !running && (
        <p className="empty">Ejecuta la prueba para ver cómo crece el tiempo de cálculo al aumentar los nodos.</p>
      )}

      {results.length > 0 && (
        <>
          <section className="panel">
            <h2 className="section-title">Tiempo promedio vs. número de nodos</h2>
            <div className="chart">
              <ResponsiveContainer width="100%" height={320}>
                <LineChart data={chartData} margin={{ top: 8, right: 24, bottom: 24, left: 8 }}>
                  <CartesianGrid stroke="#DDE3E9" />
                  <XAxis
                    dataKey="nodeCount"
                    type="number"
                    scale="linear"
                    domain={[0, 'dataMax']}
                    tickFormatter={formatInt}
                    label={{ value: 'Nodos', position: 'insideBottom', offset: -12 }}
                  />
                  <YAxis tickFormatter={(v) => formatMs(v)} label={{ value: 'ms', angle: -90, position: 'insideLeft' }} />
                  <Tooltip formatter={(value, key) => [`${formatMs(value)} ms`, nameOf(key)]} labelFormatter={(n) => `${formatInt(n)} nodos`} />
                  <Legend
                    formatter={nameOf}
                    verticalAlign="top"
                    height={32}
                    itemSorter={(item) => keysInResults.indexOf(item.dataKey)}
                  />
                  {keysInResults.map((key) => (
                    <Line
                      key={key}
                      type="monotone"
                      dataKey={key}
                      stroke={strategyColor(strategies, key)}
                      strokeWidth={3}
                      dot={{ r: 4 }}
                      isAnimationActive={false}
                    />
                  ))}
                </LineChart>
              </ResponsiveContainer>
            </div>
          </section>

          <section className="panel">
            <div className="section-head">
              <h2 className="section-title">Resultados detallados</h2>
              <button className="button button-quiet" onClick={exportCsv}>Descargar CSV</button>
            </div>
            <div className="table-wrap">
              <table className="data-table">
                <thead>
                  <tr>
                    <th scope="col">Criterio</th>
                    <th scope="col" className="num">Nodos</th>
                    <th scope="col" className="num">Conexiones</th>
                    <th scope="col" className="num">Promedio (ms)</th>
                    <th scope="col" className="num">Mín. (ms)</th>
                    <th scope="col" className="num">Máx. (ms)</th>
                    <th scope="col" className="num">Nodos explorados</th>
                    <th scope="col" className="num">Tramos</th>
                    <th scope="col" className="num">Distancia (km)</th>
                    <th scope="col" className="num">Tiempo (min)</th>
                  </tr>
                </thead>
                <tbody>
                  {results.map((r) => (
                    <tr key={`${r.nodeCount}-${r.strategyKey}`}>
                      <th scope="row">
                        <span className="swatch" style={{ background: strategyColor(strategies, r.strategyKey) }} />
                        {r.strategyName}
                      </th>
                      <td className="num">{formatInt(r.nodeCount)}</td>
                      <td className="num">{formatInt(r.connectionCount)}</td>
                      <td className="num strong">{formatMs(r.averageMs)}</td>
                      <td className="num">{formatMs(r.minMs)}</td>
                      <td className="num">{formatMs(r.maxMs)}</td>
                      <td className="num">{formatInt(r.nodesExplored)}</td>
                      <td className="num">{formatInt(r.hops)}</td>
                      <td className="num">{formatKm(r.totalDistanceKm)}</td>
                      <td className="num">{formatMin(r.totalTimeMin)}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </section>
        </>
      )}
    </div>
  )
}
