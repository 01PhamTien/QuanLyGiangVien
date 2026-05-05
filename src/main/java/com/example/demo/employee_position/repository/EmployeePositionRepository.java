package com.example.demo.employee_position.repository;

import com.example.demo.employee_position.model.EmployeePosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface EmployeePositionRepository extends JpaRepository<EmployeePosition, UUID> {
    List<EmployeePosition> findByIsActiveTrue();

    List<EmployeePosition> findByEmployee_IdAndIsActiveTrue(UUID employeeId);

    @Query("SELECT ep FROM EmployeePosition ep WHERE ep.employee.id = :employeeId AND ep.isActive = true")
    List<EmployeePosition> findActiveByEmployeeId(@Param("employeeId") UUID employeeId);
}
