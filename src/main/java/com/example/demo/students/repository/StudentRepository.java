package com.example.demo.students.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.students.model.dto.StudentListRow;
import com.example.demo.students.model.entity.Student;

public interface StudentRepository extends JpaRepository<Student, UUID> {

    List<Student> findByFullnameContainingIgnoreCase(String fullname);

    @Query("select s from Student s left join fetch s.user where s.id = :id")
    Optional<Student> findByIdWithUser(@Param("id") UUID id);

    @Query(
            value = """
                    select new com.example.demo.students.model.dto.StudentListRow(
                        s.id, s.code, s.status, s.fullname, s.gender,
                        s.date_of_birth, s.date_of_issue, s.personal_identification_number,
                        s.card_place, s.address, s.current_address)
                    from Student s
                    left join s.user u
                    where :kw = ''
                       or lower(s.fullname) like lower(concat('%', :kw, '%'))
                       or lower(s.code) like lower(concat('%', :kw, '%'))
                       or lower(coalesce(u.email, '')) like lower(concat('%', :kw, '%'))
                       or lower(coalesce(s.gender, '')) like lower(concat('%', :kw, '%'))
                       or lower(coalesce(s.status, '')) like lower(concat('%', :kw, '%'))
                       or lower(coalesce(s.personal_identification_number, '')) like lower(concat('%', :kw, '%'))
                       or lower(coalesce(s.card_place, '')) like lower(concat('%', :kw, '%'))
                       or lower(coalesce(s.address, '')) like lower(concat('%', :kw, '%'))
                       or lower(coalesce(s.current_address, '')) like lower(concat('%', :kw, '%'))
                    """,
            countQuery = """
                    select count(s)
                    from Student s
                    left join s.user u
                    where :kw = ''
                       or lower(s.fullname) like lower(concat('%', :kw, '%'))
                       or lower(s.code) like lower(concat('%', :kw, '%'))
                       or lower(coalesce(u.email, '')) like lower(concat('%', :kw, '%'))
                       or lower(coalesce(s.gender, '')) like lower(concat('%', :kw, '%'))
                       or lower(coalesce(s.status, '')) like lower(concat('%', :kw, '%'))
                       or lower(coalesce(s.personal_identification_number, '')) like lower(concat('%', :kw, '%'))
                       or lower(coalesce(s.card_place, '')) like lower(concat('%', :kw, '%'))
                       or lower(coalesce(s.address, '')) like lower(concat('%', :kw, '%'))
                       or lower(coalesce(s.current_address, '')) like lower(concat('%', :kw, '%'))
                    """)
    Page<StudentListRow> pageForAdmin(@Param("kw") String keyword, Pageable pageable);

    boolean existsByCodeIgnoreCaseAndIdNot(String code, UUID id);

    boolean existsByCodeIgnoreCase(String code);
}
