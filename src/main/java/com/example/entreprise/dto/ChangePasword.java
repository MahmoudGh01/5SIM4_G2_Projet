package com.example.entreprise.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.RequestBody;
@Getter
@Setter
public class ChangePasword {
    private  String oldPassword;
    private  String newPassword;
}
