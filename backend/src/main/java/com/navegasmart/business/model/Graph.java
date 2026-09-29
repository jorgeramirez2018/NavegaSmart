package com.navegasmart.business.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Grafo en memoria representado con lista de adyacencia.
 * Es la estructura sobre la que trabajan todas las estrategias de navegación,
 * por lo que no depende de la base de datos ni de Spring.
 */
public class Graph {

    private final Map<Long, NodeInfo> nodes = new LinkedHashMap<>();
    private final Map<Long, List<Edge>> adjacency = new HashMap<>();
    private final List<Connection> connections = new ArrayList<>();

    public void addNode(NodeInfo node) {
        nodes.put(node.id(), node);
        adjacency.putIfAbsent(node.id(), new ArrayList<>());
    }

    public void addConnection(Connection connection) {
        requireNode(connection.fromId());
        requireNode(connection.toId());
        adjacency.get(connection.fromId()).add(
                new Edge(connection.fromId(), connection.toId(), connection.distanceKm(), connection.timeMin()));
        if (connection.bidirectional()) {
            adjacency.get(connection.toId()).add(
                    new Edge(connection.toId(), connection.fromId(), connection.distanceKm(), connection.timeMin()));
        }
        connections.add(connection);
    }

    public List<Edge> neighbors(Long nodeId) {
        return adjacency.getOrDefault(nodeId, Collections.emptyList());
    }

    public boolean contains(Long nodeId) {
        return nodes.containsKey(nodeId);
    }

    public NodeInfo node(Long nodeId) {
        return nodes.get(nodeId);
    }

    public Collection<NodeInfo> nodes() {
        return Collections.unmodifiableCollection(nodes.values());
    }

    public List<Connection> connections() {
        return Collections.unmodifiableList(connections);
    }

    public int nodeCount() {
        return nodes.size();
    }

    public int connectionCount() {
        return connections.size();
    }

    private void requireNode(Long nodeId) {
        if (!nodes.containsKey(nodeId)) {
            throw new IllegalArgumentException("La conexión referencia un nodo inexistente: " + nodeId);
        }
    }
}
