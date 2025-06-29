package com.prata.dscatalog.controllers;

import com.prata.dscatalog.dto.EmailDTO;
import com.prata.dscatalog.dto.NewPasswordDTO;
import com.prata.dscatalog.dto.UserDTO;
import com.prata.dscatalog.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = "/auth")
public class AuthController {

    @Autowired
    private AuthService authService;


    @PostMapping(value = "/recover-token")
    public ResponseEntity<Void> createRecoverToken(@Valid @RequestBody EmailDTO body) {
        authService.createRecoverToken(body);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/new-password")
    public ResponseEntity<Void> saveNewPassword(@Valid @RequestBody NewPasswordDTO dto) {
        authService.saveNewPassword(dto);
        return ResponseEntity.noContent().build();
    }
}
