package com.example.entreprise.controllers;

import com.example.entreprise.auth.JwtUtil;
import com.example.entreprise.dto.*;
import com.example.entreprise.entities.Entreprise;
import com.example.entreprise.entities.Image;
import com.example.entreprise.entities.Role;
import com.example.entreprise.repositories.RoleRepository;
import com.example.entreprise.services.CloudinaryService;
import com.example.entreprise.services.EmailService;
import com.example.entreprise.services.EntrepriseServiceImpl;
import com.example.entreprise.services.IEntrepriseService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@AllArgsConstructor
@RequestMapping("crud")
@Slf4j
public class ControllerEntreprise {

    IEntrepriseService iEntrepriseService;
    EntrepriseServiceImpl entrepriseService;
    UserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private CloudinaryService cloudinaryService;

    private RoleRepository roleRepository;


    @Autowired
    EmailService mailer;


    @PostMapping("/addEntreprise")
    public Entreprise addEntreprise(
            @RequestPart("entreprise") SignUpRequest signUpRequest,
            @RequestPart("logo") MultipartFile logo) throws IOException {
        // Enregistrer l'image sur Cloudinary
        Map uploadResult = cloudinaryService.upload(logo);
        // Récupérer l'URL de l'image depuis Cloudinary
        String imageUrl = (String) uploadResult.get("url");
        Image image = new Image();
        image.setName(logo.getOriginalFilename());
        image.setImageURL(imageUrl);

        Entreprise entreprise = new Entreprise();
        entreprise.setMatricule(signUpRequest.getMatricule());
        entreprise.setMdp(signUpRequest.getMdp());
        entreprise.setNom(signUpRequest.getNom());
        entreprise.setAdresse(signUpRequest.getAdresse());
        entreprise.setLogo(image);
        entreprise.setEmail(signUpRequest.getEmail());
        entreprise.setPhone(signUpRequest.getPhone());

        // Charger les rôles
        List<Role> roles = new ArrayList<>();
        for (String roleName : signUpRequest.getRoles()) {
            Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
            roles.add(role);
        }

        // Ajouter les rôles à l'entreprise
        entreprise.setRoles(roles);

        return iEntrepriseService.addEntreprise(entreprise);
    }


    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@RequestBody LoginRequest loginRequest) {
        Map<String, Object> map = new HashMap<>();

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getMatricule(), loginRequest.getMdp()));

            if (authentication.isAuthenticated()) {

                UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getMatricule());
                Entreprise entreprise =iEntrepriseService.findByMatricule(loginRequest.getMatricule());
                String token = jwtUtil.createToken1(userDetails, entreprise);
                map.put("status", HttpStatus.OK.value());
                map.put("message", "Authentication successful");
                map.put("token", token);
                return ResponseEntity.ok(map);
            } else {
                map.put("status", HttpStatus.UNAUTHORIZED.value());
                map.put("message", "Authentication failed");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(map);
            }
        } catch (BadCredentialsException ex) {
            map.put("status", HttpStatus.UNAUTHORIZED.value());
            map.put("message", "Bad credentials");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(map);
        } catch (LockedException ex) {
            map.put("status", HttpStatus.UNAUTHORIZED.value());
            map.put("message", "Your account is locked");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(map);
        } catch (DisabledException ex) {
            map.put("status", HttpStatus.UNAUTHORIZED.value());
            map.put("message", "Your account is disabled");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(map);
        } catch (AuthenticationException ex) {
            map.put("status", HttpStatus.UNAUTHORIZED.value());
            map.put("message", "Authentication failed");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(map);
        }
    }


    @GetMapping("/getByMatricule/{matricule}")
    public  Entreprise getEntrepriseByMatricule(@PathVariable ("matricule") String m){
        return iEntrepriseService.findByMatricule(m);
    }


    @DeleteMapping("/delete/{matricule}")
    public void deleteEntreprise(@PathVariable ("matricule") String matricule){
        iEntrepriseService.deleteEntreprise(matricule);
    }


    @PostMapping("/forgetPassword/{email}")
    public ResponseEntity resetPasswordRequest(@PathVariable String email){
        Entreprise entreprise = entrepriseService.getEntrepriseByEmail(email);
        String token = jwtUtil.createPasswordToken(userDetailsService.loadUserByUsername(entreprise.getMatricule()));
        mailer.sendForgotPasswordEmail(entreprise, token);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @PostMapping("/resetpassword")
    public ResponseEntity resetPassword(@RequestBody ResetPasswordRequest request){
        if(jwtUtil.isPasswordToken(request.getToken())){
            String matricule;
            try{
                matricule = jwtUtil.getMatriculeFromToken(request.getToken());
            } catch (ExpiredJwtException | SignatureException | UnsupportedJwtException | MalformedJwtException exception){
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            entrepriseService.resetPassword(matricule,request.getMdp());
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }



    @PutMapping("/update/{matricule}")
    public ResponseEntity<Entreprise> updateEntreprise(
            @PathVariable String matricule,
            @RequestBody UpdateEntrepriseRequest updateEntrepriseRequest) {

        Entreprise updatedEntreprise = entrepriseService.updateEntreprise(matricule, updateEntrepriseRequest);
        return ResponseEntity.ok(updatedEntreprise);
    }



    @PutMapping("/{matricule}/logo")
    public ResponseEntity<Entreprise> updateLogo(@PathVariable String matricule, @RequestParam("file") MultipartFile file) {
        try {
            Entreprise updatedEntreprise = entrepriseService.updateLogo(matricule, file);
            return ResponseEntity.ok(updatedEntreprise);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }


    @PutMapping("/{matricule}/change-password")
    public ResponseEntity<String> changePassword(@PathVariable("matricule") String matricule, @RequestBody ChangePasword changePasword) {
        boolean isPasswordChanged = entrepriseService.changePassword(matricule, changePasword);

        if (isPasswordChanged) {
            return ResponseEntity.ok("Password changed successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Old password is incorrect.");
        }
    }


}
