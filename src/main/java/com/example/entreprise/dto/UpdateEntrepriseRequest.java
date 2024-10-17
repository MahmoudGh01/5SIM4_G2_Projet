package com.example.entreprise.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateEntrepriseRequest {
    private String nom;
    private String email;
    private Long phone;
    private String adresse;
}
