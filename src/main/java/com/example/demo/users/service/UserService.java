package com.example.demo.users.service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.roles.model.entity.Role;
import com.example.demo.roles.repository.RoleRepository;
import com.example.demo.users.model.entity.User;
import com.example.demo.users.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;

    public UserService(UserRepository userRepo, RoleRepository roleRepo) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
    }

    public User createUser(String username, String password, String email, String phone, String avatarUrl) {
        User u = new User();
        u.setUsername(username);
        u.setPassword(password);
        u.setFullName(username != null ? username : "");
        u.setEmail(email);
        u.setPhone(phone);
        u.setAvatarUrl(avatarUrl);
        return userRepo.save(u);
    }

    @Transactional
    public User assignRole(UUID userId, UUID roleId) {
        User u = userRepo.findById(userId).orElseThrow();
        Role r = roleRepo.findById(roleId).orElseThrow();
        u.getRoles().add(r);
        return userRepo.save(u);
    }

    public List<User> findAll() {
        return userRepo.findAll();
    }

    public User findByIdOrNull(UUID id) {
        return userRepo.findById(id).orElse(null);
    }

    public Page<User> adminPage(String keyword, Pageable pageable) {
        String kw = keyword == null ? "" : keyword.trim();
        return userRepo.pageForAdmin(kw, pageable);
    }

    public List<Role> allRolesSorted() {
        return roleRepo.findAll().stream()
                .sorted((a, b) -> {
                    String ca = a.getCode() != null ? a.getCode() : "";
                    String cb = b.getCode() != null ? b.getCode() : "";
                    return ca.compareToIgnoreCase(cb);
                })
                .toList();
    }

    @Transactional
    public User saveFromAdmin(User incoming, List<UUID> roleIds, String rawPassword, boolean isEdit) {
        if (incoming.getUsername() != null) {
            incoming.setUsername(incoming.getUsername().trim());
        }
        if (incoming.getFullName() != null) {
            incoming.setFullName(incoming.getFullName().trim());
        }
        if (incoming.getEmail() != null) {
            incoming.setEmail(incoming.getEmail().trim());
        }
        if (incoming.getPhone() != null) {
            incoming.setPhone(incoming.getPhone().trim());
        }
        if (incoming.getAvatarUrl() != null) {
            incoming.setAvatarUrl(incoming.getAvatarUrl().trim());
        }
        LocalDateTime now = LocalDateTime.now();
        Set<Role> roles = resolveRoles(roleIds);

        if (isEdit && incoming.getId() != null) {
            User existing = userRepo.findById(incoming.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng"));
            if (userRepo.existsByUsernameIgnoreCaseAndIdNot(incoming.getUsername(), incoming.getId())) {
                throw new IllegalArgumentException("Tên đăng nhập đã tồn tại");
            }
            existing.setUsername(incoming.getUsername());
            existing.setFullName(incoming.getFullName() != null && !incoming.getFullName().isBlank()
                    ? incoming.getFullName()
                    : existing.getFullName());
            existing.setEmail(incoming.getEmail());
            existing.setPhone(incoming.getPhone());
            existing.setAvatarUrl(incoming.getAvatarUrl());
            existing.setIsActive(incoming.getIsActive());
            if (rawPassword != null && !rawPassword.isBlank()) {
                existing.setPassword(rawPassword);
            }
            existing.getRoles().clear();
            existing.getRoles().addAll(roles);
            existing.setUpdatedAt(now);
            return userRepo.save(existing);
        }
        if (userRepo.existsByUsernameIgnoreCase(incoming.getUsername())) {
            throw new IllegalArgumentException("Tên đăng nhập đã tồn tại");
        }
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("Mật khẩu không được để trống khi tạo mới");
        }
        incoming.setPassword(rawPassword);
        if (incoming.getFullName() == null || incoming.getFullName().isBlank()) {
            incoming.setFullName(incoming.getUsername());
        }
        incoming.setRoles(new HashSet<>(roles));
        incoming.setCreatedAt(now);
        incoming.setUpdatedAt(now);
        if (incoming.getIsActive() == null) {
            incoming.setIsActive(true);
        }
        return userRepo.save(incoming);
    }

    private Set<Role> resolveRoles(List<UUID> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return new HashSet<>();
        }
        return new HashSet<>(roleRepo.findAllById(roleIds));
    }

    @Transactional
    public void delete(UUID id) {
        User u = userRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng"));
        u.getRoles().clear();
        userRepo.save(u);
        userRepo.deleteById(id);
    }
}
