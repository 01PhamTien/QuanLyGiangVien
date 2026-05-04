package com.example.demo.roles.service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.permissions.model.entity.Permission;
import com.example.demo.permissions.repository.PermissionRepository;
import com.example.demo.roles.model.entity.Role;
import com.example.demo.roles.repository.RoleRepository;

@Service
public class RoleService {

    private final RoleRepository roleRepo;
    private final PermissionRepository permRepo;

    public RoleService(RoleRepository roleRepo, PermissionRepository permRepo) {
        this.roleRepo = roleRepo;
        this.permRepo = permRepo;
    }

    public Role createRole(Role role) {
        return roleRepo.save(role);
    }

    public List<Role> findAll() {
        return roleRepo.findAll();
    }

    public Role findById(UUID roleId) {
        return roleRepo.findById(roleId).orElseThrow();
    }

    public Role findByIdOrNull(UUID roleId) {
        return roleRepo.findById(roleId).orElse(null);
    }

    public Page<Role> adminPage(String keyword, Pageable pageable) {
        String kw = keyword == null ? "" : keyword.trim();
        return roleRepo.pageForAdmin(kw, pageable);
    }

    @Transactional
    public Role addPermission(UUID roleId, UUID permId) {
        Role r = roleRepo.findById(roleId).orElseThrow();
        Permission p = permRepo.findById(permId).orElseThrow();
        r.getPermissions().add(p);
        return roleRepo.save(r);
    }

    @Transactional
    public Role saveFromAdmin(Role incoming, List<UUID> permissionIds, boolean isEdit) {
        if (incoming.getCode() != null) {
            incoming.setCode(incoming.getCode().trim());
        }
        if (incoming.getName() != null) {
            incoming.setName(incoming.getName().trim());
        }
        LocalDateTime now = LocalDateTime.now();
        Set<Permission> newPerms = resolvePermissions(permissionIds);

        if (isEdit && incoming.getId() != null) {
            Role existing = roleRepo.findById(incoming.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy vai trò"));
            if (roleRepo.existsByCodeIgnoreCaseAndIdNot(incoming.getCode(), incoming.getId())) {
                throw new IllegalArgumentException("Mã vai trò đã tồn tại");
            }
            existing.setCode(incoming.getCode());
            existing.setName(incoming.getName());
            existing.setDescription(incoming.getDescription());
            existing.setIsActive(incoming.getIsActive());
            existing.getPermissions().clear();
            existing.getPermissions().addAll(newPerms);
            existing.setUpdatedAt(now);
            return roleRepo.save(existing);
        }
        if (roleRepo.existsByCodeIgnoreCase(incoming.getCode())) {
            throw new IllegalArgumentException("Mã vai trò đã tồn tại");
        }
        incoming.setPermissions(new HashSet<>(newPerms));
        incoming.setCreatedAt(now);
        incoming.setUpdatedAt(now);
        if (incoming.getIsActive() == null) {
            incoming.setIsActive(true);
        }
        incoming.setIsSystem(false);
        return roleRepo.save(incoming);
    }

    private Set<Permission> resolvePermissions(List<UUID> permissionIds) {
        if (permissionIds == null || permissionIds.isEmpty()) {
            return new HashSet<>();
        }
        return new HashSet<>(permRepo.findAllById(permissionIds));
    }

    @Transactional
    public void delete(UUID id) {
        Role r = roleRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy vai trò"));
        if (Boolean.TRUE.equals(r.getIsSystem())) {
            throw new IllegalArgumentException("Không xóa được vai trò hệ thống.");
        }
        roleRepo.deleteById(id);
    }
}
