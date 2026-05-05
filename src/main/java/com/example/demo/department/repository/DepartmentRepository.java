package com.example.demo.department.repository;

import com.example.demo.department.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface DepartmentRepository extends JpaRepository<Department, UUID> {
    List<Department> findByIsActiveTrue();

    Optional<Department> findByCode(String code);
}