package com.example.demo.position.controller;

import com.example.demo.position.dto.PositionRequest;
import com.example.demo.position.model.Position;
import com.example.demo.position.service.PositionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/positions")
public class PositionController {

    private final PositionService service;

    public PositionController(PositionService service) {
        this.service = service;
    }

    @PostMapping(produces = "application/json;charset=UTF-8")
    public Position create(@Valid @RequestBody PositionRequest request) {
        return service.create(request);
    }

    @GetMapping(produces = "application/json;charset=UTF-8")
    public List<Position> getAll() {
        return service.getAll();
    }

    @PutMapping(value = "/{id}", produces = "application/json;charset=UTF-8")
    public Position update(@PathVariable UUID id,
            @Valid @RequestBody PositionRequest request) {
        return service.update(id, request);
    }

    @GetMapping(value = "/{id}", produces = "application/json;charset=UTF-8")
    public Position getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable UUID id) {
        service.delete(id);
        return "Deleted";
    }
}