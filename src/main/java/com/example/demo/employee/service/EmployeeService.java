package com.example.demo.employee.service;

import com.example.demo.employee.dto.EmployeeRequest;
import com.example.demo.employee.dto.EmployeeResponse;

import java.util.List;
import java.util.UUID;

public interface EmployeeService {

    EmployeeResponse create(EmployeeRequest request);

    List<EmployeeResponse> getAll();

    EmployeeResponse getById(UUID id);

    EmployeeResponse update(UUID id, EmployeeRequest request);

    void delete(UUID id);
}