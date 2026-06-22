package com.backend.latihan.repository;

import com.backend.latihan.entity.Role;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    @Cacheable(value = "roles")
    Optional<Role> findRoleByName(String name);
}
