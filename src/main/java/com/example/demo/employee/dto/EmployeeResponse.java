package com.example.demo.employee.dto;

import lombok.*;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponse {

    private UUID id;
    private String code;
    private String fullName;
    private String email;

    private UUID departmentId;
    private UUID positionId;
    private String departmentName;
    private String positionName;
}