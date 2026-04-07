package com.example.demo.students.controller;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.students.model.dto.StudentListRow;
import com.example.demo.students.model.entity.Student;
import com.example.demo.students.service.StudentService;

@RestController
@RequestMapping("/api/students")
@CrossOrigin
public class StudentController {

    private final StudentService service;

    public StudentController(StudentService service) {
        this.service = service;
    }

    @GetMapping
    public List<Student> getAll() {
        return service.getAll();
    }

    @GetMapping("/page")
    public Page<StudentListRow> page(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        int normalizedPage = Math.max(page, 0);
        int normalizedSize = Math.min(Math.max(size, 1), 50);
        var pageable = PageRequest.of(normalizedPage, normalizedSize, Sort.by("code").ascending());
        return service.adminPage(keyword, pageable);
    }

    @GetMapping("/admin-page")
    public Page<StudentListRow> adminPageCompat(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        return page(keyword, page, size);
    }

    @GetMapping("/search")
    public List<Student> search(
            @RequestParam(value = "full_name", required = false, defaultValue = "") String fullName) {
        if (fullName == null || fullName.isBlank()) {
            return Collections.emptyList();
        }
        return service.search(fullName.trim());
    }

    @GetMapping("/{id}")
    public Student getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @PostMapping
    public Student create(@RequestBody Student student) {
        return service.saveFromAdmin(student, false);
    }

    @PutMapping("/{id}")
    public Student update(@PathVariable UUID id, @RequestBody Student student) {
        student.setId(id);
        return service.saveFromAdmin(student, true);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
