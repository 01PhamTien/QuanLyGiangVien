package com.example.demo.position.service;

import com.example.demo.position.dto.PositionRequest;
import com.example.demo.position.model.Position;
import com.example.demo.position.repository.PositionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PositionServiceImpl implements PositionService {

    private final PositionRepository repository;

    public PositionServiceImpl(PositionRepository repository) {
        this.repository = repository;
    }

    // CREATE
    @Override
    public Position create(PositionRequest request) {
        Position p = new Position();
        p.setCode(request.getCode());
        p.setName(request.getName());
        p.setDescription(request.getDescription());

        return repository.save(p);
    }

    // GET ALL
    @Override
    public List<Position> getAll() {
        return repository.findByIsActiveTrue();
    }

    // GET BY ID
    @Override
    public Position getById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Position not found"));
    }

    // UPDATE
    @Override
    public Position update(UUID id, PositionRequest request) {
        Position p = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Position not found"));

        p.setCode(request.getCode());
        p.setName(request.getName());
        p.setDescription(request.getDescription());
        p.setUpdatedAt(LocalDateTime.now());

        return repository.save(p);
    }

    // DELETE (soft delete)
    @Override
    public void delete(UUID id) {
        Position p = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Position not found"));

        p.setDeletedAt(LocalDateTime.now());
        p.setIsActive(false);

        repository.save(p);
    }
}