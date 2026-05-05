package com.example.demo.employee_position.model;

import com.example.demo.employee.model.Employee;
import com.example.demo.position.model.Position;
import jakarta.persistence.*;
import lombok.*;

import java.time.*;
import java.util.UUID;

@Entity
@Table(name = "employee_positions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeePosition {

    @Id
    @GeneratedValue
    private UUID id;

    // 🔥 FK → Employee
    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    // 🔥 FK → Position
    @ManyToOne
    @JoinColumn(name = "position_id")
    private Position position;

    private String description;
    private String note;

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