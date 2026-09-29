package com.navegasmart.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "nodes")
public class NodeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "pos_x", nullable = false)
    private Double posX;

    @Column(name = "pos_y", nullable = false)
    private Double posY;

    protected NodeEntity() {
        // requerido por JPA
    }

    public NodeEntity(String name, Double posX, Double posY) {
        this.name = name;
        this.posX = posX;
        this.posY = posY;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Double getPosX() {
        return posX;
    }

    public Double getPosY() {
        return posY;
    }
}
