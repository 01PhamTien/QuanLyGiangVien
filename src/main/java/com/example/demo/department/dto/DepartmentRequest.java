package com.example.demo.department.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
public class DepartmentRequest {

    @NotBlank(message = "Code không được để trống")
    private String code;

    @NotBlank(message = "Tên không được để trống")
    private String name;

    @Size(max = 255)
    private String description;
}