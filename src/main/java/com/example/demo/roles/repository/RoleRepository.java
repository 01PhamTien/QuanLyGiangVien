package com.example.demo.roles.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.roles.model.entity.Role;

public interface RoleRepository extends JpaRepository<Role, UUID> {

    boolean existsByCodeIgnoreCaseAndIdNot(String code, UUID id);

    boolean existsByCodeIgnoreCase(String code);

    @Query(
            value = """
                    select r from Role r
                    where :kw = ''
                       or lower(r.code) like lower(concat('%', :kw, '%'))
                       or lower(r.name) like lower(concat('%', :kw, '%'))
                       or lower(coalesce(r.description, '')) like lower(concat('%', :kw, '%'))
                    """,
            countQuery = """
                    select count(r) from Role r
                    where :kw = ''
                       or lower(r.code) like lower(concat('%', :kw, '%'))
                       or lower(r.name) like lower(concat('%', :kw, '%'))
                       or lower(coalesce(r.description, '')) like lower(concat('%', :kw, '%'))
                    """)
    Page<Role> pageForAdmin(@Param("kw") String keyword, Pageable pageable);
}
