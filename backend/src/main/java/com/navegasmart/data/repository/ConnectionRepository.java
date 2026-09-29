package com.navegasmart.data.repository;

import com.navegasmart.data.entity.ConnectionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ConnectionRepository extends JpaRepository<ConnectionEntity, Long> {

    /** Carga las conexiones junto con sus nodos en una sola consulta (evita el problema N+1). */
    @Query("select c from ConnectionEntity c join fetch c.origin join fetch c.destination")
    List<ConnectionEntity> findAllWithNodes();
}
