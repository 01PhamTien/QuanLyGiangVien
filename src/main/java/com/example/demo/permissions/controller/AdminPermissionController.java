package com.example.demo.permissions.controller;

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

import com.example.demo.permissions.model.entity.Permission;
import com.example.demo.permissions.service.PermissionService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/permissions")
public class AdminPermissionController {

    private final PermissionService permissionService;

    public AdminPermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @GetMapping
    public String list(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size,
            Model model) {
        var pageable = PageRequest.of(page, Math.min(Math.max(size, 1), 100), Sort.by("code").ascending());
        var permPage = permissionService.adminPage(keyword, pageable);
        model.addAttribute("permissionPage", permPage);
        model.addAttribute("keyword", keyword != null ? keyword : "");
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", permPage.getTotalPages());
        model.addAttribute("pageSize", pageable.getPageSize());
        model.addAttribute("currentMenu", "permissions");
        return "admin/permissions/list";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        if (!model.containsAttribute("permission")) {
            Permission p = new Permission();
            p.setIsActive(true);
            model.addAttribute("permission", p);
        }
        model.addAttribute("isEdit", false);
        model.addAttribute("currentMenu", "permissions");
        return "admin/permissions/form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable UUID id, Model model, RedirectAttributes ra) {
        Permission p = permissionService.findByIdOrNull(id);
        if (p == null) {
            ra.addFlashAttribute("error", "Không tìm thấy quyền.");
            return "redirect:/admin/permissions";
        }
        model.addAttribute("permission", p);
        model.addAttribute("isEdit", true);
        model.addAttribute("currentMenu", "permissions");
        return "admin/permissions/form";
    }

    @PostMapping("/save")
    public String save(
            @Valid @ModelAttribute("permission") Permission permission,
            @RequestParam(value = "isEdit", defaultValue = "false") boolean isEdit,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", isEdit);
            model.addAttribute("currentMenu", "permissions");
            return "admin/permissions/form";
        }
        try {
            permissionService.saveFromAdmin(permission, isEdit);
            ra.addFlashAttribute("success", isEdit ? "Đã cập nhật quyền." : "Đã thêm quyền mới.");
            return "redirect:/admin/permissions";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("isEdit", isEdit);
            model.addAttribute("currentMenu", "permissions");
            return "admin/permissions/form";
        }
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable UUID id, RedirectAttributes ra) {
        try {
            permissionService.delete(id);
            ra.addFlashAttribute("success", "Đã xóa quyền.");
        } catch (DataIntegrityViolationException ex) {
            ra.addFlashAttribute("error", "Không xóa được: quyền đang được gán cho vai trò.");
        } catch (Exception ex) {
            ra.addFlashAttribute("error", "Không xóa được: " + ex.getMessage());
        }
        return "redirect:/admin/permissions";
    }
}
