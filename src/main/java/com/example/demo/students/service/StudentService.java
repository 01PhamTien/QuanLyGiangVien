package com.example.demo.students.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.students.model.dto.StudentListRow;
import com.example.demo.students.model.entity.Student;
import com.example.demo.students.repository.StudentRepository;
import com.example.demo.users.repository.UserRepository;

@Service
public class StudentService {

    private final StudentRepository repo;
    private final UserRepository userRepository;

    public StudentService(StudentRepository repo, UserRepository userRepository) {
        this.repo = repo;
        this.userRepository = userRepository;
    }

    public List<Student> getAll() {
        return repo.findAll();
    }

    public Student getById(UUID id) {
        return repo.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public Student getByIdWithUser(UUID id) {
        return repo.findByIdWithUser(id).orElse(null);
    }

    @Transactional
    public Student create(Student student) {
        bindUserReference(student);
        LocalDateTime now = LocalDateTime.now();
        if (student.getCreatedAt() == null) {
            student.setCreatedAt(now);
        }
        if (student.getUpdatedAt() == null) {
            student.setUpdatedAt(now);
        }
        if (student.getIsActive() == null) {
            student.setIsActive(true);
        }
        if (student.getStatus() == null || student.getStatus().isBlank()) {
            student.setStatus("studying");
        }
        return repo.save(student);
    }

    @Transactional
    public Student update(UUID id, Student student) {
        Student old = repo.findById(id).orElse(null);
        if (old == null) {
            return null;
        }
        bindUserReference(student);
        copyScalarFields(old, student);
        old.setUpdatedAt(LocalDateTime.now());
        return repo.save(old);
    }

    public void delete(UUID id) {
        repo.deleteById(id);
    }

    public List<Student> search(String fullname) {
        return repo.findByFullnameContainingIgnoreCase(fullname);
    }

    public Page<StudentListRow> adminPage(String keyword, Pageable pageable) {
        String kw = keyword == null ? "" : keyword.trim();
        return repo.pageForAdmin(kw, pageable);
    }

    @Transactional
    public Student saveFromAdmin(Student incoming, boolean isEdit) {
        if (incoming.getCode() != null) {
            incoming.setCode(incoming.getCode().trim());
        }
        if (incoming.getFullname() != null) {
            incoming.setFullname(incoming.getFullname().trim());
        }
        if (incoming.getGender() != null) {
            incoming.setGender(incoming.getGender().trim());
        }
        if (incoming.getPersonal_identification_number() != null) {
            incoming.setPersonal_identification_number(incoming.getPersonal_identification_number().trim());
        }
        if (incoming.getCard_place() != null) {
            incoming.setCard_place(incoming.getCard_place().trim());
        }
        if (incoming.getAddress() != null) {
            incoming.setAddress(incoming.getAddress().trim());
        }
        if (incoming.getCurrent_address() != null) {
            incoming.setCurrent_address(incoming.getCurrent_address().trim());
        }
        if (incoming.getStatus() != null) {
            incoming.setStatus(incoming.getStatus().trim());
        }
        LocalDateTime now = LocalDateTime.now();
        if (isEdit && incoming.getId() != null) {
            Student existing = repo.findById(incoming.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sinh viên"));
            if (repo.existsByCodeIgnoreCaseAndIdNot(incoming.getCode(), incoming.getId())) {
                throw new IllegalArgumentException("Mã sinh viên đã tồn tại");
            }
            copyScalarFields(existing, incoming);
            existing.setUpdatedAt(now);
            return repo.save(existing);
        }
        if (repo.existsByCodeIgnoreCase(incoming.getCode())) {
            throw new IllegalArgumentException("Mã sinh viên đã tồn tại");
        }
        bindUserReference(incoming);
        incoming.setCreatedAt(now);
        incoming.setUpdatedAt(now);
        if (incoming.getIsActive() == null) {
            incoming.setIsActive(true);
        }
        if (incoming.getStatus() == null || incoming.getStatus().isBlank()) {
            incoming.setStatus("studying");
        }
        return repo.save(incoming);
    }

    private void bindUserReference(Student student) {
        UUID uid = student.getUser_id();
        if (uid != null && userRepository.existsById(uid)) {
            student.setUser(userRepository.getReferenceById(uid));
        } else {
            student.setUser(null);
        }
    }

    private void copyScalarFields(Student target, Student src) {
        target.setCode(src.getCode());
        target.setFullname(src.getFullname());
        target.setDate_of_birth(src.getDate_of_birth());
        target.setGender(src.getGender());
        target.setPersonal_identification_number(src.getPersonal_identification_number());
        target.setDate_of_issue(src.getDate_of_issue());
        target.setCard_place(src.getCard_place());
        target.setAddress(src.getAddress());
        target.setCurrent_address(src.getCurrent_address());
        target.setStatus(src.getStatus());
        target.setAcademic_year_year(src.getAcademic_year_year());
        target.setDepartment_id(src.getDepartment_id());
        target.setMajor_id(src.getMajor_id());
        target.setTraining_program_id(src.getTraining_program_id());
        target.setStudent_classe_id(src.getStudent_classe_id());
        target.setAdmission_year(src.getAdmission_year());
        target.setIsActive(src.getIsActive());
        if (src.getUser_id() != null && userRepository.existsById(src.getUser_id())) {
            target.setUser(userRepository.getReferenceById(src.getUser_id()));
        } else {
            target.setUser(null);
        }
    }
}
