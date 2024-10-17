package com.example.entreprise.services;

import com.example.entreprise.dto.ChangePasword;
import com.example.entreprise.entities.Entreprise;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IEntrepriseService {
    Entreprise addEntreprise(Entreprise entreprise);
    Entreprise findByMatricule(String m);
    void deleteEntreprise(String m);
    public boolean resetPassword(String matricule, String newPassword);
    Entreprise getEntrepriseByEmail(String email);
    Entreprise updateLogo(String matricule, MultipartFile multipartFile) throws IOException;
    boolean changePassword(String matricule, ChangePasword changePasword);

}