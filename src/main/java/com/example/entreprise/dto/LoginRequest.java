package com.example.entreprise.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    private String matricule;
    private String mdp;
}
