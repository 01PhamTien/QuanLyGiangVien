package com.example.demo.department.service;

import com.example.demo.department.dto.DepartmentRequest;
import com.example.demo.department.model.Department;

import java.util.List;
import java.util.UUID;

public interface DepartmentService {

    Department create(DepartmentRequest request);

    List<Department> getAll();

    Department getById(UUID id);

    Department update(UUID id, DepartmentRequest request);

    void delete(UUID id);
}