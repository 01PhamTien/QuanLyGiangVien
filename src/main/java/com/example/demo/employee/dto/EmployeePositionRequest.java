package com.example.demo.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeePositionRequest {
    private UUID employeeId;
    private UUID positionId;
    private String description;
    private String note;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
