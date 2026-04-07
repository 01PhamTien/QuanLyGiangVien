package com.example.demo.users.controller;

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

import com.example.demo.roles.model.entity.Role;
import com.example.demo.users.model.entity.User;
import com.example.demo.users.service.UserService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String list(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size,
            Model model) {
        var pageable = PageRequest.of(page, Math.min(Math.max(size, 1), 100), Sort.by("username").ascending());
        var userPage = userService.adminPage(keyword, pageable);
        model.addAttribute("userPage", userPage);
        model.addAttribute("keyword", keyword != null ? keyword : "");
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("pageSize", pageable.getPageSize());
        model.addAttribute("currentMenu", "users");
        return "admin/users/list";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        if (!model.containsAttribute("user")) {
            User u = new User();
            u.setIsActive(true);
            model.addAttribute("user", u);
        }
        model.addAttribute("allRoles", userService.allRolesSorted());
        model.addAttribute("selectedRoleIds", List.<UUID>of());
        model.addAttribute("isEdit", false);
        model.addAttribute("currentMenu", "users");
        return "admin/users/form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable UUID id, Model model, RedirectAttributes ra) {
        User u = userService.findByIdOrNull(id);
        if (u == null) {
            ra.addFlashAttribute("error", "Không tìm thấy người dùng.");
            return "redirect:/admin/users";
        }
        u.setPassword(null);
        model.addAttribute("user", u);
        model.addAttribute("allRoles", userService.allRolesSorted());
        model.addAttribute("selectedRoleIds", u.getRoles().stream().map(Role::getId).toList());
        model.addAttribute("isEdit", true);
        model.addAttribute("currentMenu", "users");
        return "admin/users/form";
    }

    @PostMapping("/save")
    public String save(
            @Valid @ModelAttribute("user") User user,
            @RequestParam(value = "isEdit", defaultValue = "false") boolean isEdit,
            @RequestParam(required = false) List<UUID> roleIds,
            @RequestParam(required = false) String password,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("allRoles", userService.allRolesSorted());
            model.addAttribute("selectedRoleIds", roleIds != null ? roleIds : List.of());
            model.addAttribute("isEdit", isEdit);
            model.addAttribute("currentMenu", "users");
            return "admin/users/form";
        }
        try {
            userService.saveFromAdmin(user, roleIds, password, isEdit);
            ra.addFlashAttribute("success", isEdit ? "Đã cập nhật người dùng." : "Đã thêm người dùng mới.");
            return "redirect:/admin/users";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("allRoles", userService.allRolesSorted());
            model.addAttribute("selectedRoleIds", roleIds != null ? roleIds : List.of());
            model.addAttribute("isEdit", isEdit);
            model.addAttribute("currentMenu", "users");
            return "admin/users/form";
        }
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable UUID id, RedirectAttributes ra) {
        try {
            userService.delete(id);
            ra.addFlashAttribute("success", "Đã xóa người dùng.");
        } catch (IllegalArgumentException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        } catch (DataIntegrityViolationException ex) {
            ra.addFlashAttribute("error", "Không xóa được: tài khoản đang được tham chiếu (ví dụ sinh viên).");
        } catch (Exception ex) {
            ra.addFlashAttribute("error", "Không xóa được: " + ex.getMessage());
        }
        return "redirect:/admin/users";
    }
}
