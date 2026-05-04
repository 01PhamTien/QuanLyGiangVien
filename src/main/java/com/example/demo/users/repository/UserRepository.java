package com.example.demo.users.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.users.model.entity.User;

public interface UserRepository extends JpaRepository<User, UUID> {

    List<User> findByUsername(String username);

    boolean existsByUsernameIgnoreCaseAndIdNot(String username, UUID id);

    boolean existsByUsernameIgnoreCase(String username);

    @Query(
            value = """
                    select distinct u from User u
                    left join u.roles r
                    where :kw = ''
                       or lower(u.username) like lower(concat('%', :kw, '%'))
                       or lower(coalesce(u.fullName, '')) like lower(concat('%', :kw, '%'))
                       or lower(coalesce(u.email, '')) like lower(concat('%', :kw, '%'))
                       or lower(coalesce(u.phone, '')) like lower(concat('%', :kw, '%'))
                       or lower(coalesce(r.name, '')) like lower(concat('%', :kw, '%'))
                    """,
            countQuery = """
                    select count(distinct u) from User u
                    left join u.roles r
                    where :kw = ''
                       or lower(u.username) like lower(concat('%', :kw, '%'))
                       or lower(coalesce(u.fullName, '')) like lower(concat('%', :kw, '%'))
                       or lower(coalesce(u.email, '')) like lower(concat('%', :kw, '%'))
                       or lower(coalesce(u.phone, '')) like lower(concat('%', :kw, '%'))
                       or lower(coalesce(r.name, '')) like lower(concat('%', :kw, '%'))
                    """)
    Page<User> pageForAdmin(@Param("kw") String keyword, Pageable pageable);
}
