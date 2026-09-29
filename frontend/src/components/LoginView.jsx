import { useState } from 'react'
import { api } from '../api.js'

// Pantalla de acceso: iniciar sesión o crear una cuenta nueva.
export default function LoginView({ onAuthenticated, notice }) {
  const [mode, setMode] = useState('login')   // 'login' | 'register'
  const [fullName, setFullName] = useState('')
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [pending, setPending] = useState(false)
  const [error, setError] = useState(null)

  const isRegister = mode === 'register'

  const switchMode = () => {
    setMode(isRegister ? 'login' : 'register')
    setError(null)
  }

  const submit = async (event) => {
    event.preventDefault()
    setPending(true)
    setError(null)
    try {
      const result = isRegister
        ? await api.register(fullName, username, password)
        : await api.login(username, password)
      onAuthenticated(result)
    } catch (e) {
      setError(e.message)
      setPending(false)
    }
  }

  return (
    <div className="auth-screen">
      <div className="auth-card panel">
        <div className="brand auth-brand">
          <svg className="brand-mark" viewBox="0 0 32 32" aria-hidden="true">
            <path d="M6 24 L14 12 L26 8" />
            <circle cx="6" cy="24" r="4" />
            <circle cx="26" cy="8" r="4" />
          </svg>
          <span className="brand-name">NavegaSmart</span>
        </div>

        <h1 className="panel-title">{isRegister ? 'Crear cuenta' : 'Iniciar sesión'}</h1>
        <p className="hint">
          {isRegister
            ? 'Regístrate para calcular rutas y consultar el historial.'
            : 'Ingresa con tu usuario para planear recorridos.'}
        </p>

        {notice && !error && <p className="notice notice-info" role="status">{notice}</p>}

        <form className="auth-form" onSubmit={submit} noValidate>
          {isRegister && (
            <label className="field">
              <span className="field-label">Nombre completo</span>
              <input
                value={fullName}
                onChange={(e) => setFullName(e.target.value)}
                autoComplete="name"
                required
              />
            </label>
          )}
          <label className="field">
            <span className="field-label">Usuario</span>
            <input
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              autoComplete="username"
              autoCapitalize="none"
              spellCheck="false"
              required
            />
            {isRegister && <span className="field-help">De 3 a 40 caracteres: letras, números, punto o guion.</span>}
          </label>
          <label className="field">
            <span className="field-label">Contraseña</span>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              autoComplete={isRegister ? 'new-password' : 'current-password'}
              required
            />
            {isRegister && <span className="field-help">Mínimo 8 caracteres.</span>}
          </label>

          {error && <p className="notice notice-error" role="alert">{error}</p>}

          <button className="button button-primary" type="submit" disabled={pending}>
            {pending
              ? (isRegister ? 'Creando cuenta…' : 'Ingresando…')
              : (isRegister ? 'Crear cuenta' : 'Ingresar')}
          </button>
        </form>

        <p className="auth-switch">
          {isRegister ? '¿Ya tienes cuenta?' : '¿No tienes cuenta?'}{' '}
          <button type="button" className="link-button" onClick={switchMode}>
            {isRegister ? 'Inicia sesión' : 'Regístrate'}
          </button>
        </p>
      </div>
    </div>
  )
}
