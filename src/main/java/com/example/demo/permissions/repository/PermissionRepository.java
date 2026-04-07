package com.example.demo.permissions.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.permissions.model.entity.Permission;

public interface PermissionRepository extends JpaRepository<Permission, UUID> {

    boolean existsByCodeIgnoreCaseAndIdNot(String code, UUID id);

    boolean existsByCodeIgnoreCase(String code);

    @Query(
            value = """
                    select p from Permission p
                    where :kw = ''
                       or lower(p.code) like lower(concat('%', :kw, '%'))
                       or lower(p.name) like lower(concat('%', :kw, '%'))
                       or lower(coalesce(p.module, '')) like lower(concat('%', :kw, '%'))
                    """,
            countQuery = """
                    select count(p) from Permission p
                    where :kw = ''
                       or lower(p.code) like lower(concat('%', :kw, '%'))
                       or lower(p.name) like lower(concat('%', :kw, '%'))
                       or lower(coalesce(p.module, '')) like lower(concat('%', :kw, '%'))
                    """)
    Page<Permission> pageForAdmin(@Param("kw") String keyword, Pageable pageable);
}
