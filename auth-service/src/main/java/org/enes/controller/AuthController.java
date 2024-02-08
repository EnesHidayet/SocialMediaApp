package org.enes.controller;

import lombok.RequiredArgsConstructor;
import org.enes.dto.request.RegisterRequestDto;
import org.enes.dto.response.RegisterResponseDto;
import org.enes.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import static org.enes.constants.RestApiUrl.*;
@RestController
@RequiredArgsConstructor
@RequestMapping(AUTH)
public class AuthController {

    private final AuthService authService;

    @PostMapping(REGISTER)
    public ResponseEntity<RegisterResponseDto> register(RegisterRequestDto dto) {
        return ResponseEntity.ok().body(authService.register(dto));
    }

}
