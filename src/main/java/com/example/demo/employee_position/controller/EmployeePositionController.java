package com.example.demo.employee_position.controller;

import com.example.demo.employee.dto.EmployeePositionRequest;
import com.example.demo.employee.dto.EmployeePositionResponse;
import com.example.demo.employee_position.service.EmployeePositionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/employee-positions")
public class EmployeePositionController {

    private final EmployeePositionService service;

    public EmployeePositionController(EmployeePositionService service) {
        this.service = service;
    }

    @PostMapping(produces = "application/json;charset=UTF-8")
    public EmployeePositionResponse create(@RequestBody EmployeePositionRequest request) {
        return service.create(request);
    }

    @GetMapping(produces = "application/json;charset=UTF-8")
    public List<EmployeePositionResponse> getAll() {
        return service.getAll();
    }

    @GetMapping(value = "/{id}", produces = "application/json;charset=UTF-8")
    public EmployeePositionResponse getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @PutMapping(value = "/{id}", produces = "application/json;charset=UTF-8")
    public EmployeePositionResponse update(@PathVariable UUID id,
            @RequestBody EmployeePositionRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable UUID id) {
        service.delete(id);
        return "Deleted (soft delete)";
    }
}
