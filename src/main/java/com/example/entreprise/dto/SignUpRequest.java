package com.example.entreprise.dto;

import com.example.entreprise.entities.Role;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SignUpRequest {
    private String matricule;
    private String mdp;
    private String nom;
    private String adresse;
    private String email;
    private Long phone;
    private List<String> roles;
}