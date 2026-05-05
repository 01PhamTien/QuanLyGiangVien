package com.example.demo.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeePositionResponse {
    private UUID id;
    private String employeeCode;
    private String employeeName;
    private String positionName;
    private String description;
    private String note;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
