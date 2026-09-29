import { lazy, Suspense, useEffect, useState } from 'react'
import { api, session } from './api.js'
import LoginView from './components/LoginView.jsx'
import RouteView from './components/RouteView.jsx'
import HistoryView from './components/HistoryView.jsx'

// La vista de rendimiento incluye la librería de gráficas: se carga solo cuando se abre.
const BenchmarkView = lazy(() => import('./components/BenchmarkView.jsx'))

const TABS = [
  { id: 'routes', label: 'Rutas' },
  { id: 'benchmark', label: 'Rendimiento' },
  { id: 'history', label: 'Historial' },
]

export default function App() {
  const [auth, setAuth] = useState(() => session.load())
  const [authNotice, setAuthNotice] = useState(null)

  const signIn = (result) => {
    session.save(result)
    setAuthNotice(null)
    setAuth(result)
  }

  const signOut = (notice = null) => {
    session.clear()
    setAuthNotice(notice)
    setAuth(null)
  }

  // Si el servidor rechaza el token (vencido o inválido), se vuelve a la pantalla de acceso.
  useEffect(() => {
    session.onUnauthorized(() => signOut('Tu sesión expiró. Inicia sesión de nuevo.'))
  }, [])

  if (!auth) {
    return <LoginView onAuthenticated={signIn} notice={authNotice} />
  }

  return <Workspace user={auth.user} onSignOut={() => signOut()} />
}

function Workspace({ user, onSignOut }) {
  const [tab, setTab] = useState('routes')
  const [graph, setGraph] = useState(null)
  const [strategies, setStrategies] = useState([])
  const [loadError, setLoadError] = useState(null)

  const load = () => {
    setLoadError(null)
    Promise.all([api.getGraph(), api.getStrategies()])
      .then(([graphData, strategyData]) => {
        setGraph(graphData)
        setStrategies(strategyData)
      })
      .catch((error) => setLoadError(error.message))
  }

  useEffect(load, [])

  return (
    <div className="app">
      <header className="topbar">
        <div className="brand">
          <svg className="brand-mark" viewBox="0 0 32 32" aria-hidden="true">
            <path d="M6 24 L14 12 L26 8" />
            <circle cx="6" cy="24" r="4" />
            <circle cx="26" cy="8" r="4" />
          </svg>
          <span className="brand-name">NavegaSmart</span>
        </div>
        <nav className="tabs" role="tablist" aria-label="Secciones">
          {TABS.map((t) => (
            <button
              key={t.id}
              role="tab"
              aria-selected={tab === t.id}
              className={tab === t.id ? 'tab is-active' : 'tab'}
              onClick={() => setTab(t.id)}
            >
              {t.label}
            </button>
          ))}
        </nav>
        <div className="account">
          <span className="account-name" title={user.username}>{user.fullName}</span>
          <button className="button button-quiet" onClick={onSignOut}>Cerrar sesión</button>
        </div>
      </header>

      <main className="content">
        {loadError && (
          <div className="notice notice-error" role="alert">
            <p>{loadError}</p>
            <button className="button button-quiet" onClick={load}>Reintentar</button>
          </div>
        )}
        {!loadError && !graph && <p className="loading">Cargando la red de navegación…</p>}

        {graph && tab === 'routes' && <RouteView graph={graph} strategies={strategies} />}
        {graph && tab === 'benchmark' && (
          <Suspense fallback={<p className="loading">Cargando la vista de rendimiento…</p>}>
            <BenchmarkView strategies={strategies} />
          </Suspense>
        )}
        {graph && tab === 'history' && <HistoryView graph={graph} strategies={strategies} />}
      </main>
    </div>
  )
}
