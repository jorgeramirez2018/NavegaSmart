package com.navegasmart.data.repository;

import com.navegasmart.data.entity.NodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NodeRepository extends JpaRepository<NodeEntity, Long> {
}
