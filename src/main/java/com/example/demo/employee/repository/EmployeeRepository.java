package com.example.demo.employee.repository;

import com.example.demo.employee.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface EmployeeRepository extends JpaRepository<Employee, UUID> {
    List<Employee> findByIsActiveTrue();
}