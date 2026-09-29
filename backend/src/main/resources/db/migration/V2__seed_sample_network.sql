-- =====================================================================
-- NavegaSmart - Red de ejemplo (12 puntos, 20 conexiones)
-- Diseñada para que las estrategias produzcan rutas distintas.
-- Ejemplo: Terminal de Transporte -> Aeropuerto
--   * Más corta : Terminal, Plaza Central, Universidad, Estadio, Aeropuerto (9.7 km, 40 min)
--   * Más rápida: Terminal, Mercado, Parque, Museo, Zona Industrial, Aeropuerto (14.6 km, 17 min)
--   * Menos conexiones: Terminal, Hospital, Aeropuerto (2 tramos, 11.9 km, 24 min)
-- Tipos de vía: centro (congestionado), avenida (rápida), local, perimetral.
-- =====================================================================

INSERT INTO nodes (id, name, pos_x, pos_y) VALUES
    (1, 'Terminal de Transporte', 8, 50),
    (2, 'Plaza Central', 30, 48),
    (3, 'Mercado', 26, 22),
    (4, 'Hospital', 28, 80),
    (5, 'Universidad', 50, 50),
    (6, 'Parque Principal', 50, 20),
    (7, 'Biblioteca', 52, 78),
    (8, 'Estadio', 70, 50),
    (9, 'Museo', 74, 22),
    (10, 'Centro Comercial', 74, 80),
    (11, 'Zona Industrial', 94, 24),
    (12, 'Aeropuerto', 96, 56);

SELECT setval('nodes_id_seq', (SELECT MAX(id) FROM nodes));

INSERT INTO connections (origin_id, destination_id, distance_km, time_min, road_type, bidirectional) VALUES
    (1, 2, 2.4, 10, 'centro', TRUE),
    (2, 5, 2.2, 9, 'centro', TRUE),
    (5, 8, 2.2, 9, 'centro', TRUE),
    (8, 12, 2.9, 12, 'centro', TRUE),
    (1, 3, 3.7, 4, 'avenida', TRUE),
    (3, 6, 2.6, 3, 'avenida', TRUE),
    (6, 9, 2.6, 3, 'avenida', TRUE),
    (9, 11, 2.2, 3, 'avenida', TRUE),
    (11, 12, 3.5, 4, 'avenida', TRUE),
    (2, 3, 2.9, 7, 'local', TRUE),
    (5, 6, 3.3, 8, 'local', TRUE),
    (8, 9, 3.1, 7, 'local', TRUE),
    (2, 4, 3.5, 8, 'local', TRUE),
    (5, 7, 3.1, 7, 'local', TRUE),
    (8, 10, 3.3, 8, 'local', TRUE),
    (1, 4, 4.0, 8, 'perimetral', TRUE),
    (4, 12, 7.9, 16, 'perimetral', TRUE),
    (4, 7, 2.6, 6, 'local', TRUE),
    (7, 10, 2.4, 6, 'local', FALSE),  -- vía de un solo sentido
    (10, 12, 3.6, 9, 'local', TRUE);
