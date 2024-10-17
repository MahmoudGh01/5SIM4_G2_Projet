package com.example.entreprise.repositories;

import com.example.entreprise.entities.Entreprise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EntrepriseRepository extends JpaRepository<Entreprise, String> {

    @Query("SELECT e FROM Entreprise e WHERE e.email = :email")
    Entreprise findByEmail(@Param("email") String email);

}