package com.navegasmart.data.repository;

import com.navegasmart.data.entity.ExecutionLogEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExecutionLogRepository extends JpaRepository<ExecutionLogEntity, Long> {

    List<ExecutionLogEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
