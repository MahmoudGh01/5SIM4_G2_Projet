package com.example.entreprise.services;

import com.example.entreprise.entities.Entreprise;
import com.example.entreprise.repositories.EntrepriseRepository;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@NoArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    EntrepriseRepository entrepriseRepository;

    @Override
    public UserDetails loadUserByUsername(String matricule) throws UsernameNotFoundException {
        Entreprise entreprise=entrepriseRepository.findById(matricule).orElse(null);

        if (entreprise == null) {
            throw new UsernameNotFoundException("User not found with matricule: " + matricule);
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(matricule)
                .password(entreprise.getMdp())
                .build();

    }

}