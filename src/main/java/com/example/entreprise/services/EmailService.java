package com.example.entreprise.services;

import com.example.entreprise.entities.Entreprise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;

    public void sendForgotPasswordEmail(Entreprise entreprise, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        String resetPasswordLink = "http://localhost:4200/#/auth/resetpassword/" + token;
        String subject = "Forgot Password";
        String text = "Dear "+ entreprise.getNom() + ",\n\nYou have requested to reset your password. Please follow the link below to reset it:\n\n"+ resetPasswordLink +"\n\nIf you did not request this, please ignore this email.\n\nBest regards,\nThe CrocoCoder Team";
        message.setFrom("samaali.achref@gmail.com");
        message.setTo(entreprise.getEmail());
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }

}
