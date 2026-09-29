package com.navegasmart.business.service;

import com.navegasmart.business.model.Connection;
import com.navegasmart.business.model.Graph;
import com.navegasmart.business.model.NodeInfo;

import java.util.Random;

/**
 * Genera redes sintéticas para las pruebas de rendimiento y escalabilidad.
 *
 * La red imita una ciudad: una cuadrícula de calles (velocidad baja/media) con
 * algunas avenidas rápidas que cruzan varias cuadras. Así la ruta más corta y la
 * más rápida suelen ser distintas, igual que en la red de ejemplo.
 * Se usa una semilla fija para que todas las estrategias se midan sobre el mismo grafo.
 */
public final class SyntheticGraphGenerator {

    private static final double BLOCK_KM = 0.5;
    private static final double DETOUR_FACTOR = 1.15;
    private static final int MAX_SHORTCUT_SPAN = 6;

    private SyntheticGraphGenerator() {
    }

    public static Graph generate(int nodeCount, long seed) {
        if (nodeCount < 2) {
            throw new IllegalArgumentException("El grafo sintético necesita al menos 2 nodos.");
        }
        Random random = new Random(seed);
        Graph graph = new Graph();
        int columns = (int) Math.ceil(Math.sqrt(nodeCount));

        for (int i = 0; i < nodeCount; i++) {
            int row = i / columns;
            int column = i % columns;
            double x = column * BLOCK_KM + random.nextDouble() * 0.1;
            double y = row * BLOCK_KM + random.nextDouble() * 0.1;
            graph.addNode(new NodeInfo((long) i, "N" + i, x, y));
        }

        // Cuadrícula de calles: cada nodo se conecta con su vecino derecho e inferior (garantiza conectividad).
        for (int i = 0; i < nodeCount; i++) {
            int column = i % columns;
            if (column + 1 < columns && i + 1 < nodeCount) {
                connect(graph, random, i, i + 1, 15, 45, "local");
            }
            if (i + columns < nodeCount) {
                connect(graph, random, i, i + columns, 15, 45, "local");
            }
        }

        // Avenidas rápidas: ~5 % de conexiones extra que saltan varias cuadras.
        int shortcuts = Math.max(1, nodeCount / 20);
        int created = 0;
        int attempts = 0;
        while (created < shortcuts && attempts < shortcuts * 10) {
            attempts++;
            int from = random.nextInt(nodeCount);
            int rowOffset = random.nextInt(2 * MAX_SHORTCUT_SPAN + 1) - MAX_SHORTCUT_SPAN;
            int columnOffset = random.nextInt(2 * MAX_SHORTCUT_SPAN + 1) - MAX_SHORTCUT_SPAN;
            int targetColumn = from % columns + columnOffset;
            int to = from + rowOffset * columns + columnOffset;
            if (targetColumn < 0 || targetColumn >= columns || to < 0 || to >= nodeCount || to == from) {
                continue;
            }
            connect(graph, random, from, to, 60, 90, "avenida");
            created++;
        }
        return graph;
    }

    private static void connect(Graph graph, Random random, int from, int to,
                                double minSpeedKmh, double maxSpeedKmh, String roadType) {
        NodeInfo a = graph.node((long) from);
        NodeInfo b = graph.node((long) to);
        double distanceKm = Math.max(0.05, Math.hypot(a.x() - b.x(), a.y() - b.y()) * DETOUR_FACTOR);
        double speedKmh = minSpeedKmh + random.nextDouble() * (maxSpeedKmh - minSpeedKmh);
        double timeMin = distanceKm / speedKmh * 60.0;
        graph.addConnection(new Connection((long) from, (long) to, distanceKm, timeMin, roadType, true));
    }
}
