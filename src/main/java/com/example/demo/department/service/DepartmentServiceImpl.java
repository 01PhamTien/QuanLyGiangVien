package com.example.demo.department.service;

import com.example.demo.department.dto.DepartmentRequest;
import com.example.demo.department.model.Department;
import com.example.demo.department.repository.DepartmentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository repository;

    public DepartmentServiceImpl(DepartmentRepository repository) {
        this.repository = repository;
    }

    // CREATE
    @Override
    public Department create(DepartmentRequest request) {
        Department d = new Department();
        d.setCode(request.getCode());
        d.setName(request.getName());
        d.setDescription(request.getDescription());

        return repository.save(d);
    }

    // GET ALL
    @Override
    public List<Department> getAll() {
        return repository.findByIsActiveTrue();
    }

    // GET BY ID
    @Override
    public Department getById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));
    }

    // UPDATE
    @Override
    public Department update(UUID id, DepartmentRequest request) {
        Department d = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        d.setCode(request.getCode());
        d.setName(request.getName());
        d.setDescription(request.getDescription());
        d.setUpdatedAt(LocalDateTime.now());

        return repository.save(d);
    }

    // DELETE (soft delete)
    @Override
    public void delete(UUID id) {
        Department d = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        d.setDeletedAt(LocalDateTime.now());
        d.setIsActive(false);

        repository.save(d);
    }
}