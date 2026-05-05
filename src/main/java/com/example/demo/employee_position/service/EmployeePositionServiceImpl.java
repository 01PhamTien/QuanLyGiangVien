package com.example.demo.employee_position.service;

import com.example.demo.employee.dto.EmployeePositionRequest;
import com.example.demo.employee.dto.EmployeePositionResponse;
import com.example.demo.employee.model.Employee;
import com.example.demo.employee.repository.EmployeeRepository;
import com.example.demo.employee_position.model.EmployeePosition;
import com.example.demo.employee_position.repository.EmployeePositionRepository;
import com.example.demo.position.model.Position;
import com.example.demo.position.repository.PositionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class EmployeePositionServiceImpl implements EmployeePositionService {

        private final EmployeePositionRepository repository;
        private final EmployeeRepository employeeRepository;
        private final PositionRepository positionRepository;

        public EmployeePositionServiceImpl(EmployeePositionRepository repository,
                        EmployeeRepository employeeRepository,
                        PositionRepository positionRepository) {
                this.repository = repository;
                this.employeeRepository = employeeRepository;
                this.positionRepository = positionRepository;
        }

        @Override
        public EmployeePositionResponse create(EmployeePositionRequest request) {
                Employee employee = employeeRepository.findById(request.getEmployeeId())
                                .orElseThrow(() -> new RuntimeException("Employee not found"));

                Position position = positionRepository.findById(request.getPositionId())
                                .orElseThrow(() -> new RuntimeException("Position not found"));

                EmployeePosition ep = EmployeePosition.builder()
                                .employee(employee)
                                .position(position)
                                .description(request.getDescription())
                                .note(request.getNote())
                                .startDate(request.getStartDate())
                                .endDate(request.getEndDate())
                                .build();

                EmployeePosition saved = repository.save(ep);
                return mapToResponse(saved);
        }

        @Override
        public List<EmployeePositionResponse> getAll() {
                return repository.findByIsActiveTrue()
                                .stream()
                                .map(this::mapToResponse)
                                .toList();
        }

        @Override
        public EmployeePositionResponse getById(UUID id) {
                EmployeePosition ep = repository.findById(id)
                                .orElseThrow(() -> new RuntimeException("EmployeePosition not found"));
                return mapToResponse(ep);
        }

        @Override
        public EmployeePositionResponse update(UUID id, EmployeePositionRequest request) {
                EmployeePosition existing = repository.findById(id)
                                .orElseThrow(() -> new RuntimeException("EmployeePosition not found"));

                Employee employee = employeeRepository.findById(request.getEmployeeId())
                                .orElseThrow(() -> new RuntimeException("Employee not found"));

                Position position = positionRepository.findById(request.getPositionId())
                                .orElseThrow(() -> new RuntimeException("Position not found"));

                existing.setEmployee(employee);
                existing.setPosition(position);
                existing.setDescription(request.getDescription());
                existing.setNote(request.getNote());
                existing.setStartDate(request.getStartDate());
                existing.setEndDate(request.getEndDate());

                EmployeePosition saved = repository.save(existing);
                return mapToResponse(saved);
        }

        @Override
        public void delete(UUID id) {
                EmployeePosition existing = repository.findById(id)
                                .orElseThrow(() -> new RuntimeException("EmployeePosition not found"));

                existing.setDeletedAt(LocalDateTime.now());
                existing.setIsActive(false);
                repository.save(existing);
        }

        private EmployeePositionResponse mapToResponse(EmployeePosition ep) {
                return EmployeePositionResponse.builder()
                                .id(ep.getId())
                                .employeeCode(ep.getEmployee() != null ? ep.getEmployee().getCode() : null)
                                .employeeName(ep.getEmployee() != null ? ep.getEmployee().getFullName() : null)
                                .positionName(ep.getPosition() != null ? ep.getPosition().getName() : null)
                                .description(ep.getDescription())
                                .note(ep.getNote())
                                .startDate(ep.getStartDate())
                                .endDate(ep.getEndDate())
                                .build();
        }
}
