package com.example.demo.roles.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.permissions.model.entity.Permission;
import com.example.demo.permissions.service.PermissionService;
import com.example.demo.roles.model.entity.Role;
import com.example.demo.roles.service.RoleService;

@RestController
@RequestMapping("/api/roles")
public class RoleApiController {

    private final RoleService roleService;
    private final PermissionService permissionService;

    public RoleApiController(RoleService roleService, PermissionService permissionService) {
        this.roleService = roleService;
        this.permissionService = permissionService;
    }

    @GetMapping
    public List<Role> list() {
        return roleService.findAll();
    }

    @GetMapping("/page")
    public Page<Role> page(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "15") int size) {
        int normalizedPage = Math.max(page, 0);
        int normalizedSize = Math.min(Math.max(size, 1), 100);
        var pageable = PageRequest.of(normalizedPage, normalizedSize, Sort.by("code").ascending());
        return roleService.adminPage(keyword, pageable);
    }

    @GetMapping("/permissions")
    public List<Permission> allPermissions() {
        return permissionService.findAllSorted();
    }

    @GetMapping("/{id}")
    public Role getById(@PathVariable UUID id) {
        return roleService.findByIdOrNull(id);
    }

    @PostMapping
    public Role create(@RequestBody RoleUpsertRequest request) {
        Role role = new Role();
        role.setCode(request.code());
        role.setName(request.name());
        role.setDescription(request.description());
        role.setIsActive(request.isActive());
        return roleService.saveFromAdmin(role, request.permissionIds(), false);
    }

    @PutMapping("/{id}")
    public Role update(@PathVariable UUID id, @RequestBody RoleUpsertRequest request) {
        Role role = new Role();
        role.setId(id);
        role.setCode(request.code());
        role.setName(request.name());
        role.setDescription(request.description());
        role.setIsActive(request.isActive());
        return roleService.saveFromAdmin(role, request.permissionIds(), true);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        roleService.delete(id);
    }

    public record RoleUpsertRequest(
            String code,
            String name,
            String description,
            Boolean isActive,
            List<UUID> permissionIds) {
    }
}