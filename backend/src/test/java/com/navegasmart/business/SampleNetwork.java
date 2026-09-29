package com.navegasmart.business;

import com.navegasmart.business.model.Connection;
import com.navegasmart.business.model.Graph;
import com.navegasmart.business.model.NodeInfo;

/**
 * Copia en memoria de la red de ejemplo de V2__seed_sample_network.sql,
 * para probar las estrategias sin necesidad de PostgreSQL.
 */
public final class SampleNetwork {

    public static final long TERMINAL = 1L;
    public static final long PLAZA_CENTRAL = 2L;
    public static final long MERCADO = 3L;
    public static final long HOSPITAL = 4L;
    public static final long UNIVERSIDAD = 5L;
    public static final long PARQUE = 6L;
    public static final long BIBLIOTECA = 7L;
    public static final long ESTADIO = 8L;
    public static final long MUSEO = 9L;
    public static final long CENTRO_COMERCIAL = 10L;
    public static final long ZONA_INDUSTRIAL = 11L;
    public static final long AEROPUERTO = 12L;

    private SampleNetwork() {
    }

    public static Graph create() {
        Graph graph = new Graph();
        graph.addNode(new NodeInfo(TERMINAL, "Terminal de Transporte", 8, 50));
        graph.addNode(new NodeInfo(PLAZA_CENTRAL, "Plaza Central", 30, 48));
        graph.addNode(new NodeInfo(MERCADO, "Mercado", 26, 22));
        graph.addNode(new NodeInfo(HOSPITAL, "Hospital", 28, 80));
        graph.addNode(new NodeInfo(UNIVERSIDAD, "Universidad", 50, 50));
        graph.addNode(new NodeInfo(PARQUE, "Parque Principal", 50, 20));
        graph.addNode(new NodeInfo(BIBLIOTECA, "Biblioteca", 52, 78));
        graph.addNode(new NodeInfo(ESTADIO, "Estadio", 70, 50));
        graph.addNode(new NodeInfo(MUSEO, "Museo", 74, 22));
        graph.addNode(new NodeInfo(CENTRO_COMERCIAL, "Centro Comercial", 74, 80));
        graph.addNode(new NodeInfo(ZONA_INDUSTRIAL, "Zona Industrial", 94, 24));
        graph.addNode(new NodeInfo(AEROPUERTO, "Aeropuerto", 96, 56));

        add(graph, 1, 2, 2.4, 10, "centro", true);
        add(graph, 2, 5, 2.2, 9, "centro", true);
        add(graph, 5, 8, 2.2, 9, "centro", true);
        add(graph, 8, 12, 2.9, 12, "centro", true);
        add(graph, 1, 3, 3.7, 4, "avenida", true);
        add(graph, 3, 6, 2.6, 3, "avenida", true);
        add(graph, 6, 9, 2.6, 3, "avenida", true);
        add(graph, 9, 11, 2.2, 3, "avenida", true);
        add(graph, 11, 12, 3.5, 4, "avenida", true);
        add(graph, 2, 3, 2.9, 7, "local", true);
        add(graph, 5, 6, 3.3, 8, "local", true);
        add(graph, 8, 9, 3.1, 7, "local", true);
        add(graph, 2, 4, 3.5, 8, "local", true);
        add(graph, 5, 7, 3.1, 7, "local", true);
        add(graph, 8, 10, 3.3, 8, "local", true);
        add(graph, 1, 4, 4.0, 8, "perimetral", true);
        add(graph, 4, 12, 7.9, 16, "perimetral", true);
        add(graph, 4, 7, 2.6, 6, "local", true);
        add(graph, 7, 10, 2.4, 6, "local", false);
        add(graph, 10, 12, 3.6, 9, "local", true);
        return graph;
    }

    private static void add(Graph graph, long from, long to, double km, double min, String type, boolean both) {
        graph.addConnection(new Connection(from, to, km, min, type, both));
    }
}
