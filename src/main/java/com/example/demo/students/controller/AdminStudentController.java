package com.example.demo.students.controller;

import java.beans.PropertyEditorSupport;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.students.model.entity.Student;
import com.example.demo.students.service.StudentService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/students")
public class AdminStudentController {

    private final StudentService studentService;

    public AdminStudentController(StudentService studentService) {
        this.studentService = studentService;
    }

   
    private static final DateTimeFormatter ADMISSION_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(LocalDate.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                if (text == null || text.isBlank()) {
                    setValue(null);
                    return;
                }
                try {
                    setValue(LocalDate.parse(text));
                } catch (DateTimeParseException ex) {
                    throw new IllegalArgumentException("Định dạng ngày không hợp lệ: " + text, ex);
                }
            }
        });
        binder.registerCustomEditor(LocalDateTime.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                if (text == null || text.isBlank()) {
                    setValue(null);
                    return;
                }
                String t = text.trim();
                if (t.length() == 16) {
                    t = t + ":00";
                }
                try {
                    setValue(LocalDateTime.parse(t, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                } catch (DateTimeParseException ex) {
                    try {
                        setValue(LocalDateTime.parse(text.trim(), ADMISSION_FORMAT));
                    } catch (DateTimeParseException ex2) {
                        throw new IllegalArgumentException("Định dạng ngày giờ không hợp lệ: " + text, ex2);
                    }
                }
            }
        });
        binder.registerCustomEditor(UUID.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                if (text == null || text.isBlank()) {
                    setValue(null);
                    return;
                }
                try {
                    setValue(UUID.fromString(text.trim()));
                } catch (IllegalArgumentException ex) {
                    throw new IllegalArgumentException("UUID không hợp lệ: " + text, ex);
                }
            }
        });
    }

    @GetMapping
    public String list(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        var pageable = PageRequest.of(page, Math.min(Math.max(size, 1), 50), Sort.by("code").ascending());
        var studentPage = studentService.adminPage(keyword, pageable);
        model.addAttribute("studentPage", studentPage);
        model.addAttribute("keyword", keyword != null ? keyword : "");
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", studentPage.getTotalPages());
        model.addAttribute("pageSize", pageable.getPageSize());
        model.addAttribute("currentMenu", "students");
        return "admin/students/list";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        if (!model.containsAttribute("student")) {
            model.addAttribute("student", new Student());
        }
        model.addAttribute("isEdit", false);
        model.addAttribute("currentMenu", "students");
        return "admin/students/form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable UUID id, Model model, RedirectAttributes ra) {
        Student s = studentService.getByIdWithUser(id);
        if (s == null) {
            ra.addFlashAttribute("error", "Không tìm thấy sinh viên.");
            return "redirect:/admin/students";
        }
        model.addAttribute("student", s);
        model.addAttribute("isEdit", true);
        model.addAttribute("currentMenu", "students");
        return "admin/students/form";
    }

    @PostMapping("/save")
    public String save(
            @Valid @ModelAttribute("student") Student student,
            @RequestParam(value = "isEdit", defaultValue = "false") boolean isEdit,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", isEdit);
            model.addAttribute("currentMenu", "students");
            return "admin/students/form";
        }
        try {
            studentService.saveFromAdmin(student, isEdit);
            ra.addFlashAttribute("success", isEdit ? "Đã cập nhật sinh viên." : "Đã thêm sinh viên mới.");
            return "redirect:/admin/students";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("isEdit", isEdit);
            model.addAttribute("currentMenu", "students");
            return "admin/students/form";
        }
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable UUID id, RedirectAttributes ra) {
        try {
            studentService.delete(id);
            ra.addFlashAttribute("success", "Đã xóa sinh viên.");
        } catch (DataIntegrityViolationException ex) {
            ra.addFlashAttribute("error",
                    "Không xóa được: sinh viên đang được tham chiếu (tài khoản hoặc dữ liệu liên quan).");
        } catch (Exception ex) {
            ra.addFlashAttribute("error", "Không xóa được: " + ex.getMessage());
        }
        return "redirect:/admin/students";
    }
}
