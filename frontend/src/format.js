const number = (value, digits) =>
  Number(value).toLocaleString('es-CO', { minimumFractionDigits: digits, maximumFractionDigits: digits })

export const formatKm = (value) => number(value, 1)
export const formatMin = (value) => number(Math.round(value), 0)
export const formatMs = (value) => (value < 1 ? number(value, 3) : number(value, 2))
export const formatInt = (value) => number(value, 0)

// Colores de línea por estrategia (orden en que las entrega la API).
const PALETTE = ['#2455C3', '#E39B0B', '#138A6B', '#9B3FA8', '#C2410C']

export function strategyColor(strategies, key) {
  const index = strategies.findIndex((s) => s.key === key)
  return PALETTE[(index >= 0 ? index : 0) % PALETTE.length]
}

// Descarga un arreglo de filas como CSV (útil como evidencia para el informe).
export function downloadCsv(filename, header, rows) {
  const escape = (value) => {
    const text = value === null || value === undefined ? '' : String(value)
    return /[",;\n]/.test(text) ? `"${text.replace(/"/g, '""')}"` : text
  }
  const csv = [header, ...rows].map((row) => row.map(escape).join(',')).join('\n')
  const blob = new Blob(['\uFEFF' + csv], { type: 'text/csv;charset=utf-8' })
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = filename
  link.click()
  URL.revokeObjectURL(url)
}
