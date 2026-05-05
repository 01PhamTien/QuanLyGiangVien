package com.example.demo.employee_position.service;

import com.example.demo.employee.dto.EmployeePositionRequest;
import com.example.demo.employee.dto.EmployeePositionResponse;
import com.example.demo.employee_position.model.EmployeePosition;

import java.util.List;
import java.util.UUID;

public interface EmployeePositionService {
    EmployeePositionResponse create(EmployeePositionRequest request);

    List<EmployeePositionResponse> getAll();

    EmployeePositionResponse getById(UUID id);

    EmployeePositionResponse update(UUID id, EmployeePositionRequest request);

    void delete(UUID id);
}
