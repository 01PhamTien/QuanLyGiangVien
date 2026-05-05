package com.example.demo.employee.service;

import com.example.demo.department.model.Department;
import com.example.demo.department.repository.DepartmentRepository;
import com.example.demo.employee.dto.EmployeeRequest;
import com.example.demo.employee.dto.EmployeeResponse;
import com.example.demo.employee.model.Employee;
import com.example.demo.employee.repository.EmployeeRepository;
import com.example.demo.position.model.Position;
import com.example.demo.position.repository.PositionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository repository;
    private final DepartmentRepository departmentRepository;
    private final PositionRepository positionRepository;

    public EmployeeServiceImpl(EmployeeRepository repository,
            DepartmentRepository departmentRepository,
            PositionRepository positionRepository) {
        this.repository = repository;
        this.departmentRepository = departmentRepository;
        this.positionRepository = positionRepository;
    }

    // CREATE
    @Override
    public EmployeeResponse create(EmployeeRequest request) {

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Department not found"));

        Position position = positionRepository.findById(request.getPositionId())
                .orElseThrow(() -> new RuntimeException("Position not found"));

        Employee employee = new Employee();
        employee.setCode(request.getCode());
        employee.setFullName(request.getFullName());
        employee.setEmail(request.getEmail());
        employee.setDepartment(department);
        employee.setPosition(position);

        Employee saved = repository.save(employee);

        return mapToResponse(saved);
    }

    // GET ALL
    @Override
    public List<EmployeeResponse> getAll() {
        return repository.findByIsActiveTrue()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // GET BY ID
    @Override
    public EmployeeResponse getById(UUID id) {
        Employee e = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        return mapToResponse(e);
    }

    // UPDATE
    @Override
    public EmployeeResponse update(UUID id, EmployeeRequest request) {

        Employee existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Department not found"));

        Position position = positionRepository.findById(request.getPositionId())
                .orElseThrow(() -> new RuntimeException("Position not found"));

        existing.setCode(request.getCode());
        existing.setFullName(request.getFullName());
        existing.setEmail(request.getEmail());
        existing.setDepartment(department);
        existing.setPosition(position);

        Employee saved = repository.save(existing);

        return mapToResponse(saved);
    }

    // DELETE (soft)
    @Override
    public void delete(UUID id) {
        Employee existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        existing.setDeletedAt(LocalDateTime.now());
        existing.setIsActive(false);

        repository.save(existing);
    }

    // 🔥 MAP FUNCTION (QUAN TRỌNG)
    private EmployeeResponse mapToResponse(Employee e) {
        return EmployeeResponse.builder()
                .id(e.getId())
                .code(e.getCode())
                .fullName(e.getFullName())
                .email(e.getEmail())
                .departmentId(e.getDepartment() != null ? e.getDepartment().getId() : null)
                .positionId(e.getPosition() != null ? e.getPosition().getId() : null)
                .departmentName(e.getDepartment() != null ? e.getDepartment().getName() : null)
                .positionName(e.getPosition() != null ? e.getPosition().getName() : null)
                .build();
    }
}