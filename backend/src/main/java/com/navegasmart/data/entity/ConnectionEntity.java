package com.navegasmart.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "connections")
public class ConnectionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "origin_id", nullable = false)
    private NodeEntity origin;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "destination_id", nullable = false)
    private NodeEntity destination;

    @Column(name = "distance_km", nullable = false)
    private Double distanceKm;

    @Column(name = "time_min", nullable = false)
    private Double timeMin;

    @Column(name = "road_type", nullable = false, length = 30)
    private String roadType;

    @Column(nullable = false)
    private Boolean bidirectional;

    protected ConnectionEntity() {
        // requerido por JPA
    }

    public Long getId() {
        return id;
    }

    public NodeEntity getOrigin() {
        return origin;
    }

    public NodeEntity getDestination() {
        return destination;
    }

    public Double getDistanceKm() {
        return distanceKm;
    }

    public Double getTimeMin() {
        return timeMin;
    }

    public String getRoadType() {
        return roadType;
    }

    public Boolean getBidirectional() {
        return bidirectional;
    }
}
