package com.example.demo.employee.model;

import com.example.demo.department.model.Department;
import com.example.demo.position.model.Position;
import jakarta.persistence.*;
import lombok.*;

import java.time.*;
import java.util.UUID;

@Entity
@Table(name = "employees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID userId;

    @Column(nullable = false, unique = true)
    private String code;

    private String fullName;
    private LocalDate dateOfBirth;
    private String gender;

    private String email;
    private String phone;
    private String address;

    // 🔥 FK → Department
    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    // 🔥 FK → Position
    @ManyToOne
    @JoinColumn(name = "position_id")
    private Position position;

    private LocalDate hireDate;
    private String contractType;
    private Double salaryCoefficient;

    private String academicDegree;
    private String academicTitle;
    private String specialization;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private UUID createdBy;
    private UUID updatedBy;

    private LocalDateTime deletedAt;
    private UUID deletedBy;

    private Boolean isActive;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.isActive = true;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}