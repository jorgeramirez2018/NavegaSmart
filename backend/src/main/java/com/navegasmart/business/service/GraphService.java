package com.navegasmart.business.service;

import com.navegasmart.business.model.Connection;
import com.navegasmart.business.model.Graph;
import com.navegasmart.business.model.NodeInfo;
import com.navegasmart.data.entity.ConnectionEntity;
import com.navegasmart.data.entity.NodeEntity;
import com.navegasmart.data.repository.ConnectionRepository;
import com.navegasmart.data.repository.NodeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Construye el grafo en memoria a partir de la base de datos.
 *
 * Decisión de rendimiento: el grafo se carga una sola vez y se guarda en caché,
 * de modo que el tiempo medido en cada cálculo corresponde solo al algoritmo y
 * no a las consultas SQL. Si se modifican los datos, se llama a reload().
 */
@Service
public class GraphService {

    private static final Logger log = LoggerFactory.getLogger(GraphService.class);

    private final NodeRepository nodeRepository;
    private final ConnectionRepository connectionRepository;
    private volatile Graph cachedGraph;

    public GraphService(NodeRepository nodeRepository, ConnectionRepository connectionRepository) {
        this.nodeRepository = nodeRepository;
        this.connectionRepository = connectionRepository;
    }

    public Graph getGraph() {
        Graph graph = cachedGraph;
        if (graph == null) {
            synchronized (this) {
                if (cachedGraph == null) {
                    cachedGraph = loadFromDatabase();
                }
                graph = cachedGraph;
            }
        }
        return graph;
    }

    public synchronized Graph reload() {
        cachedGraph = loadFromDatabase();
        return cachedGraph;
    }

    private Graph loadFromDatabase() {
        Graph graph = new Graph();
        for (NodeEntity node : nodeRepository.findAll()) {
            graph.addNode(new NodeInfo(node.getId(), node.getName(), node.getPosX(), node.getPosY()));
        }
        for (ConnectionEntity connection : connectionRepository.findAllWithNodes()) {
            graph.addConnection(new Connection(
                    connection.getOrigin().getId(),
                    connection.getDestination().getId(),
                    connection.getDistanceKm(),
                    connection.getTimeMin(),
                    connection.getRoadType(),
                    connection.getBidirectional()));
        }
        log.info("Red cargada desde PostgreSQL: {} nodos, {} conexiones", graph.nodeCount(), graph.connectionCount());
        return graph;
    }
}
