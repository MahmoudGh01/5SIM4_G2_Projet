package com.example.entreprise.services;

import com.example.entreprise.dto.*;
import com.example.entreprise.entities.Entreprise;
import com.example.entreprise.entities.Image;
import com.example.entreprise.repositories.EntrepriseRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
public class EntrepriseServiceImpl implements IEntrepriseService  {


    //injection des services
    EntrepriseRepository entrepriseRepository;
    private  PasswordEncoder passwordEncoder;
    private CloudinaryService cloudinaryService;


    @Override
    public Entreprise addEntreprise(Entreprise entreprise) {
        //crypt password
        String encodedPassword=passwordEncoder.encode(entreprise.getMdp());
        entreprise.setMdp(encodedPassword);
        //add entreprise
        return entrepriseRepository.save(entreprise);
    }


    @Override
    public Entreprise findByMatricule(String m) {
        return entrepriseRepository.findById(m).get();
    }


    @Override
    public void deleteEntreprise(String m) {
        entrepriseRepository.deleteById(m);
    }


    public Entreprise getEntrepriseByEmail(String email) {
        return entrepriseRepository.findByEmail(email);
    }


    @Override
    public boolean resetPassword(String email, String newPassword) {
        Entreprise entreprise = entrepriseRepository.findById(email).orElse(null);
        if (entreprise != null) {
            String encodedPassword = passwordEncoder.encode(newPassword);
            entreprise.setMdp(encodedPassword);
            entrepriseRepository.save(entreprise);
            return true;
        }
        return false;
    }


    @Override
    public Entreprise updateLogo(String matricule, MultipartFile multipartFile) throws IOException {
        // Find the entreprise by matricule
        Entreprise entreprise = entrepriseRepository.findById(matricule)
                .orElseThrow(() -> new RuntimeException("Entreprise not found"));

        // Upload the file using Cloudinary
        Map<String, Object> uploadResult = cloudinaryService.upload(multipartFile);

        // Create or update the Image entity
        Image logo = entreprise.getLogo();
        if (logo == null) {
            logo = new Image();
        }
        logo.setName((String) uploadResult.get("original_filename"));
        logo.setImageURL((String) uploadResult.get("url"));

        entreprise.setLogo(logo);
        return entrepriseRepository.save(entreprise);
    }

    public Entreprise updateEntreprise(String matricule, UpdateEntrepriseRequest updateEntrepriseRequest) {
        return entrepriseRepository.findById(matricule)
                .map(entreprise -> {
                    entreprise.setNom(updateEntrepriseRequest.getNom());
                    entreprise.setEmail(updateEntrepriseRequest.getEmail());
                    entreprise.setPhone(updateEntrepriseRequest.getPhone());
                    entreprise.setAdresse(updateEntrepriseRequest.getAdresse());
                    return entrepriseRepository.save(entreprise);
                })
                .orElseThrow(() -> new EntityNotFoundException("Entreprise with matricule " + matricule + " not found"));
    }


    public boolean changePassword(String matricule, ChangePasword changePasword) {
        // Retrieve the entreprise from the database
        Optional<Entreprise> optionalEntreprise = entrepriseRepository.findById(matricule);
        if (optionalEntreprise.isPresent()) {
            Entreprise entreprise = optionalEntreprise.get();

            // Check if the old password matches the current password
            if (passwordEncoder.matches(changePasword.getOldPassword(), entreprise.getMdp())) {
                // Encode the new password
                String encodedNewPassword = passwordEncoder.encode(changePasword.getNewPassword());
                entreprise.setMdp(encodedNewPassword);

                // Save the updated entreprise
                entrepriseRepository.save(entreprise);

                return true; // Password change was successful
            } else {
                return false; // Old password does not match
            }
        } else {
            throw new EntityNotFoundException("Entreprise not found with ID: " + matricule);
        }
    }


}


