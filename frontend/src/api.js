// Cliente HTTP de la API de NavegaSmart (capa de presentación -> backend).
const BASE_URL = import.meta.env.VITE_API_URL ?? ''
const SESSION_KEY = 'navegasmart.session'

// ---------- Sesión (token JWT guardado en el navegador) ----------

let currentToken = null
let unauthorizedHandler = () => {}

export const session = {
  load() {
    try {
      const saved = JSON.parse(localStorage.getItem(SESSION_KEY))
      if (saved?.token && new Date(saved.expiresAt) > new Date()) return saved
    } catch {
      // almacenamiento no disponible o dato corrupto: se trata como sin sesión
    }
    session.clear()
    return null
  },
  save(data) {
    try {
      localStorage.setItem(SESSION_KEY, JSON.stringify(data))
    } catch {
      // sin almacenamiento la sesión dura solo mientras la pestaña esté abierta
    }
    currentToken = data.token
  },
  clear() {
    try {
      localStorage.removeItem(SESSION_KEY)
    } catch {
      // nada que limpiar
    }
    currentToken = null
  },
  /** La aplicación registra aquí qué hacer cuando el servidor rechaza el token (401). */
  onUnauthorized(handler) {
    unauthorizedHandler = handler
  },
}

currentToken = session.load()?.token ?? null

// ---------- Solicitudes ----------

async function request(path, options = {}) {
  const headers = { 'Content-Type': 'application/json' }
  if (currentToken) headers.Authorization = `Bearer ${currentToken}`

  let response
  try {
    response = await fetch(`${BASE_URL}${path}`, { ...options, headers })
  } catch {
    throw new Error('No se pudo conectar con el servidor. Verifica que el backend esté en ejecución en el puerto 8080.')
  }

  const body = await response.json().catch(() => null)
  if (!response.ok) {
    // Un 401 en /api/auth es "credenciales incorrectas"; en cualquier otra ruta, sesión vencida.
    if (response.status === 401 && !path.startsWith('/api/auth/')) {
      session.clear()
      unauthorizedHandler()
    }
    throw new Error(body?.message ?? `El servidor respondió con el código ${response.status}.`)
  }
  return body
}

export const api = {
  login: (username, password) =>
    request('/api/auth/login', {
      method: 'POST',
      body: JSON.stringify({ username, password }),
    }),
  register: (fullName, username, password) =>
    request('/api/auth/register', {
      method: 'POST',
      body: JSON.stringify({ fullName, username, password }),
    }),
  getGraph: () => request('/api/graph'),
  getStrategies: () => request('/api/strategies'),
  calculateRoute: (originId, destinationId, strategy) =>
    request('/api/routes', {
      method: 'POST',
      body: JSON.stringify({ originId, destinationId, strategy }),
    }),
  compareRoutes: (originId, destinationId) =>
    request('/api/routes/compare', {
      method: 'POST',
      body: JSON.stringify({ originId, destinationId }),
    }),
  runBenchmark: (sizes, repetitions, warmupRuns) =>
    request('/api/benchmarks', {
      method: 'POST',
      body: JSON.stringify({ sizes, repetitions, warmupRuns }),
    }),
  getExecutions: (limit = 50) => request(`/api/executions?limit=${limit}`),
}
