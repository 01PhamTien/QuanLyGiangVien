package com.example.demo.position.service;

import com.example.demo.position.dto.PositionRequest;
import com.example.demo.position.model.Position;

import java.util.List;
import java.util.UUID;

public interface PositionService {

    Position create(PositionRequest request);

    List<Position> getAll();

    Position getById(UUID id);

    Position update(UUID id, PositionRequest request);

    void delete(UUID id);
}