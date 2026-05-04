package com.example.demo.users.controller;

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

import com.example.demo.roles.model.entity.Role;
import com.example.demo.users.model.entity.User;
import com.example.demo.users.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserApiController {

    private final UserService userService;

    public UserApiController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> list() {
        return userService.findAll();
    }

    @GetMapping("/page")
    public Page<User> page(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "15") int size) {
        int normalizedPage = Math.max(page, 0);
        int normalizedSize = Math.min(Math.max(size, 1), 100);
        var pageable = PageRequest.of(normalizedPage, normalizedSize, Sort.by("username").ascending());
        return userService.adminPage(keyword, pageable);
    }

    @GetMapping("/roles")
    public List<Role> allRoles() {
        return userService.allRolesSorted();
    }

    @GetMapping("/{id}")
    public User getById(@PathVariable UUID id) {
        return userService.findByIdOrNull(id);
    }

    @PostMapping
    public User create(@RequestBody UserUpsertRequest request) {
        User user = new User();
        user.setUsername(request.username());
        user.setFullName(request.fullName());
        user.setEmail(request.email());
        user.setPhone(request.phone());
        user.setAvatarUrl(request.avatarUrl());
        user.setIsActive(request.isActive());
        return userService.saveFromAdmin(user, request.roleIds(), request.password(), false);
    }

    @PutMapping("/{id}")
    public User update(@PathVariable UUID id, @RequestBody UserUpsertRequest request) {
        User user = new User();
        user.setId(id);
        user.setUsername(request.username());
        user.setFullName(request.fullName());
        user.setEmail(request.email());
        user.setPhone(request.phone());
        user.setAvatarUrl(request.avatarUrl());
        user.setIsActive(request.isActive());
        return userService.saveFromAdmin(user, request.roleIds(), request.password(), true);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        userService.delete(id);
    }

    public record UserUpsertRequest(
            String username,
            String password,
            String fullName,
            String email,
            String phone,
            String avatarUrl,
            Boolean isActive,
            List<UUID> roleIds) {
    }
}