package com.example.demo.permissions.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.permissions.model.entity.Permission;
import com.example.demo.permissions.repository.PermissionRepository;

@Service
public class PermissionService {

    private final PermissionRepository permissionRepo;

    public PermissionService(PermissionRepository permissionRepo) {
        this.permissionRepo = permissionRepo;
    }

    public Permission create(Permission permission) {
        return permissionRepo.save(permission);
    }

    public List<Permission> findAll() {
        return permissionRepo.findAll();
    }

    public List<Permission> findAllSorted() {
        return permissionRepo.findAll().stream()
                .sorted((a, b) -> {
                    String ma = a.getModule() != null ? a.getModule() : "";
                    String mb = b.getModule() != null ? b.getModule() : "";
                    int c = ma.compareToIgnoreCase(mb);
                    if (c != 0) {
                        return c;
                    }
                    String ca = a.getCode() != null ? a.getCode() : "";
                    String cb = b.getCode() != null ? b.getCode() : "";
                    return ca.compareToIgnoreCase(cb);
                })
                .toList();
    }

    public Permission findById(UUID id) {
        return permissionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Permission not found"));
    }

    public Permission findByIdOrNull(UUID id) {
        return permissionRepo.findById(id).orElse(null);
    }

    public void delete(UUID id) {
        permissionRepo.deleteById(id);
    }

    public Page<Permission> adminPage(String keyword, Pageable pageable) {
        String kw = keyword == null ? "" : keyword.trim();
        return permissionRepo.pageForAdmin(kw, pageable);
    }

    @Transactional
    public Permission saveFromAdmin(Permission incoming, boolean isEdit) {
        if (incoming.getCode() != null) {
            incoming.setCode(incoming.getCode().trim());
        }
        if (incoming.getName() != null) {
            incoming.setName(incoming.getName().trim());
        }
        if (incoming.getModule() != null) {
            incoming.setModule(incoming.getModule().trim());
        }
        LocalDateTime now = LocalDateTime.now();
        if (isEdit && incoming.getId() != null) {
            Permission existing = permissionRepo.findById(incoming.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy quyền"));
            if (permissionRepo.existsByCodeIgnoreCaseAndIdNot(incoming.getCode(), incoming.getId())) {
                throw new IllegalArgumentException("Mã quyền đã tồn tại");
            }
            existing.setCode(incoming.getCode());
            existing.setName(incoming.getName());
            existing.setModule(incoming.getModule());
            existing.setDescription(incoming.getDescription());
            existing.setIsActive(incoming.getIsActive());
            existing.setUpdatedAt(now);
            return permissionRepo.save(existing);
        }
        if (permissionRepo.existsByCodeIgnoreCase(incoming.getCode())) {
            throw new IllegalArgumentException("Mã quyền đã tồn tại");
        }
        incoming.setCreatedAt(now);
        incoming.setUpdatedAt(now);
        if (incoming.getIsActive() == null) {
            incoming.setIsActive(true);
        }
        return permissionRepo.save(incoming);
    }
}
