package com.ledgerline.ledgerline.repository;

import com.ledgerline.ledgerline.entity.Role;
import com.ledgerline.ledgerline.entity.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for Role entities.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

    Optional<Role> findByName(RoleType name);
}

