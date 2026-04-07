package com.example.demo.roles.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.permissions.service.PermissionService;
import com.example.demo.roles.model.entity.Role;
import com.example.demo.roles.service.RoleService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/roles")
public class AdminRoleController {

    private final RoleService roleService;
    private final PermissionService permissionService;

    public AdminRoleController(RoleService roleService, PermissionService permissionService) {
        this.roleService = roleService;
        this.permissionService = permissionService;
    }

    @GetMapping
    public String list(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size,
            Model model) {
        var pageable = PageRequest.of(page, Math.min(Math.max(size, 1), 100), Sort.by("code").ascending());
        var rolePage = roleService.adminPage(keyword, pageable);
        model.addAttribute("rolePage", rolePage);
        model.addAttribute("keyword", keyword != null ? keyword : "");
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", rolePage.getTotalPages());
        model.addAttribute("pageSize", pageable.getPageSize());
        model.addAttribute("currentMenu", "roles");
        return "admin/roles/list";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        if (!model.containsAttribute("role")) {
            Role r = new Role();
            r.setIsActive(true);
            model.addAttribute("role", r);
        }
        model.addAttribute("allPermissions", permissionService.findAllSorted());
        model.addAttribute("selectedPermissionIds", List.<UUID>of());
        model.addAttribute("isEdit", false);
        model.addAttribute("currentMenu", "roles");
        return "admin/roles/form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable UUID id, Model model, RedirectAttributes ra) {
        Role r = roleService.findByIdOrNull(id);
        if (r == null) {
            ra.addFlashAttribute("error", "Không tìm thấy vai trò.");
            return "redirect:/admin/roles";
        }
        model.addAttribute("role", r);
        model.addAttribute("allPermissions", permissionService.findAllSorted());
        model.addAttribute("selectedPermissionIds",
                r.getPermissions().stream().map(p -> p.getId()).toList());
        model.addAttribute("isEdit", true);
        model.addAttribute("currentMenu", "roles");
        return "admin/roles/form";
    }

    @PostMapping("/save")
    public String save(
            @Valid @ModelAttribute("role") Role role,
            @RequestParam(value = "isEdit", defaultValue = "false") boolean isEdit,
            @RequestParam(required = false) List<UUID> permissionIds,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("allPermissions", permissionService.findAllSorted());
            model.addAttribute("selectedPermissionIds", permissionIds != null ? permissionIds : List.of());
            model.addAttribute("isEdit", isEdit);
            model.addAttribute("currentMenu", "roles");
            return "admin/roles/form";
        }
        try {
            roleService.saveFromAdmin(role, permissionIds, isEdit);
            ra.addFlashAttribute("success", isEdit ? "Đã cập nhật vai trò." : "Đã thêm vai trò mới.");
            return "redirect:/admin/roles";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("allPermissions", permissionService.findAllSorted());
            model.addAttribute("selectedPermissionIds", permissionIds != null ? permissionIds : List.of());
            model.addAttribute("isEdit", isEdit);
            model.addAttribute("currentMenu", "roles");
            return "admin/roles/form";
        }
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable UUID id, RedirectAttributes ra) {
        try {
            roleService.delete(id);
            ra.addFlashAttribute("success", "Đã xóa vai trò.");
        } catch (IllegalArgumentException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        } catch (DataIntegrityViolationException ex) {
            ra.addFlashAttribute("error", "Không xóa được: vai trò đang được gán cho người dùng.");
        } catch (Exception ex) {
            ra.addFlashAttribute("error", "Không xóa được: " + ex.getMessage());
        }
        return "redirect:/admin/roles";
    }
}
