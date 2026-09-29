-- =====================================================================
-- NavegaSmart - Usuarios de la aplicación (inicio de sesión)
-- La contraseña nunca se guarda en texto plano: solo su hash BCrypt.
-- =====================================================================

CREATE TABLE users (
    id             BIGSERIAL PRIMARY KEY,
    username       VARCHAR(40)  NOT NULL UNIQUE,
    full_name      VARCHAR(100) NOT NULL,
    password_hash  VARCHAR(100) NOT NULL,
    created_at     TIMESTAMP    NOT NULL DEFAULT now()
);
