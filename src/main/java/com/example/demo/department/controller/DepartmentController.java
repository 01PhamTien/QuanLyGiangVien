package com.example.demo.department.controller;

import com.example.demo.department.dto.DepartmentRequest;
import com.example.demo.department.model.Department;
import com.example.demo.department.service.DepartmentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    private final DepartmentService service;

    public DepartmentController(DepartmentService service) {
        this.service = service;
    }

    @PostMapping(produces = "application/json;charset=UTF-8")
    public Department create(@Valid @RequestBody DepartmentRequest request) {
        return service.create(request);
    }

    @GetMapping(produces = "application/json;charset=UTF-8")
    public List<Department> getAll() {
        return service.getAll();
    }

    @PutMapping(value = "/{id}", produces = "application/json;charset=UTF-8")
    public Department update(@PathVariable UUID id,
            @Valid @RequestBody DepartmentRequest request) {
        return service.update(id, request);
    }

    @GetMapping(value = "/{id}", produces = "application/json;charset=UTF-8")
    public Department getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable UUID id) {
        service.delete(id);
        return "Deleted";
    }
}