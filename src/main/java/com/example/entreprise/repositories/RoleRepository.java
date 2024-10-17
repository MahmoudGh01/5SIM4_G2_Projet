package com.example.entreprise.repositories;

import com.example.entreprise.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name); // Méthode pour trouver un rôle par son nom
}
