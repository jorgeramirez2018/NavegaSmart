-- =====================================================================
-- NavegaSmart - Esquema inicial (capa de datos)
-- La red de navegación se modela como un grafo: nodos + conexiones.
-- =====================================================================

CREATE TABLE nodes (
    id      BIGSERIAL PRIMARY KEY,
    name    VARCHAR(100)     NOT NULL UNIQUE,
    pos_x   DOUBLE PRECISION NOT NULL,   -- coordenada para dibujar el mapa
    pos_y   DOUBLE PRECISION NOT NULL
);

CREATE TABLE connections (
    id              BIGSERIAL PRIMARY KEY,
    origin_id       BIGINT           NOT NULL REFERENCES nodes (id) ON DELETE CASCADE,
    destination_id  BIGINT           NOT NULL REFERENCES nodes (id) ON DELETE CASCADE,
    distance_km     DOUBLE PRECISION NOT NULL CHECK (distance_km > 0),
    time_min        DOUBLE PRECISION NOT NULL CHECK (time_min > 0),
    road_type       VARCHAR(30)      NOT NULL,
    bidirectional   BOOLEAN          NOT NULL DEFAULT TRUE,
    CONSTRAINT chk_connections_distinct_nodes CHECK (origin_id <> destination_id)
);

CREATE INDEX idx_connections_origin ON connections (origin_id);
CREATE INDEX idx_connections_destination ON connections (destination_id);

-- Registro de resultados y tiempos de ejecución (alcance: "registro de resultados")
CREATE TABLE execution_logs (
    id                 BIGSERIAL PRIMARY KEY,
    run_type           VARCHAR(20)      NOT NULL,  -- ROUTE (usuario) | BENCHMARK (prueba de rendimiento)
    strategy_key       VARCHAR(40)      NOT NULL,
    origin_id          BIGINT,
    destination_id     BIGINT,
    node_count         INTEGER          NOT NULL,
    edge_count         INTEGER          NOT NULL,
    hops               INTEGER,
    nodes_explored     INTEGER,
    total_distance_km  DOUBLE PRECISION,
    total_time_min     DOUBLE PRECISION,
    execution_time_ms  DOUBLE PRECISION NOT NULL,
    created_at         TIMESTAMP        NOT NULL DEFAULT now()
);

CREATE INDEX idx_execution_logs_created_at ON execution_logs (created_at DESC);
