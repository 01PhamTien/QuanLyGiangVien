package com.example.demo.position.repository;

import com.example.demo.position.model.Position;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface PositionRepository extends JpaRepository<Position, UUID> {
    List<Position> findByIsActiveTrue();

    Optional<Position> findByCode(String code);
}