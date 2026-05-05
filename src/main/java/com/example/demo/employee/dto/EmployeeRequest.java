package com.example.demo.employee.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class EmployeeRequest {

    @NotBlank(message = "Code không được để trống")
    private String code;

    @NotBlank(message = "Tên không được để trống")
    private String fullName;

    @Email(message = "Email không hợp lệ")
    private String email;

    @NotNull(message = "Department không được null")
    private UUID departmentId;

    @NotNull(message = "Position không được null")
    private UUID positionId;
}