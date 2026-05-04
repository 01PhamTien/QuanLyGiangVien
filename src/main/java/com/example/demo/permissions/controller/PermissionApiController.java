package com.example.demo.permissions.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.permissions.model.entity.Permission;
import com.example.demo.permissions.service.PermissionService;

@RestController
@RequestMapping("/api/permissions")
public class PermissionApiController {

    private final PermissionService permissionService;

    public PermissionApiController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @GetMapping
    public List<Permission> list() {
        return permissionService.findAll();
    }

    @GetMapping("/page")
    public Page<Permission> page(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "15") int size) {
        int normalizedPage = Math.max(page, 0);
        int normalizedSize = Math.min(Math.max(size, 1), 100);
        var pageable = PageRequest.of(normalizedPage, normalizedSize, Sort.by("code").ascending());
        return permissionService.adminPage(keyword, pageable);
    }

    @GetMapping("/{id}")
    public Permission getById(@PathVariable UUID id) {
        return permissionService.findByIdOrNull(id);
    }

    @PostMapping
    public Permission create(@RequestBody Permission permission) {
        return permissionService.saveFromAdmin(permission, false);
    }

    @PutMapping("/{id}")
    public Permission update(@PathVariable UUID id, @RequestBody Permission permission) {
        permission.setId(id);
        return permissionService.saveFromAdmin(permission, true);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        permissionService.delete(id);
    }
}