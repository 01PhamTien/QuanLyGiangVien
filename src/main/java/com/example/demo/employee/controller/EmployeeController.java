package com.example.demo.employee.controller;

import com.example.demo.employee.dto.EmployeeRequest;
import com.example.demo.employee.dto.EmployeeResponse;
import com.example.demo.employee.service.EmployeeService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService service;

    public EmployeeController(EmployeeService service) {
        this.service = service;
    }

    @PostMapping(produces = "application/json;charset=UTF-8")
    public EmployeeResponse create(@Valid @RequestBody EmployeeRequest request) {
        return service.create(request);
    }

    @GetMapping(produces = "application/json;charset=UTF-8")
    public List<EmployeeResponse> getAll() {
        return service.getAll();
    }

    @GetMapping(value = "/{id}", produces = "application/json;charset=UTF-8")
    public EmployeeResponse getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @PutMapping(value = "/{id}", produces = "application/json;charset=UTF-8")
    public EmployeeResponse update(@PathVariable UUID id,
            @Valid @RequestBody EmployeeRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable UUID id) {
        service.delete(id);
        return "Deleted";
    }
}