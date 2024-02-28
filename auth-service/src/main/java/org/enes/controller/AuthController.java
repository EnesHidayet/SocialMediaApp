package org.enes.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.enes.dto.request.ActivateStatusRequestDto;
import org.enes.dto.request.AuthUpdateRequestDto;
import org.enes.dto.request.LoginRequestDto;
import org.enes.dto.request.RegisterRequestDto;
import org.enes.dto.response.RegisterResponseDto;
import org.enes.entity.Auth;
import org.enes.service.AuthService;
import org.enes.utility.JwtTokenManager;
import org.enes.utility.enums.ERole;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.enes.constants.RestApiUrl.*;
@RestController
@RequiredArgsConstructor
@RequestMapping(AUTH)
public class AuthController {

    private final AuthService authService;
    private final JwtTokenManager tokenManager;

    @PostMapping(REGISTER)
    public ResponseEntity<RegisterResponseDto> register(@RequestBody @Valid RegisterRequestDto dto) {
        return ResponseEntity.ok(authService.register(dto));
    }

    @PostMapping("/register-with-rabbitmq")
    public ResponseEntity<RegisterResponseDto> registerWithRabbitMq(@RequestBody @Valid RegisterRequestDto dto) {
        return ResponseEntity.ok(authService.registerWithRabbitMq(dto));
    }

    @PostMapping(LOGIN)
    public ResponseEntity<String> login(@RequestBody @Valid LoginRequestDto dto) {
        return ResponseEntity.ok(authService.login(dto));
    }

    @PostMapping(ACTIVATE_STATUS)
    public ResponseEntity<Boolean> activateStatus(@RequestBody ActivateStatusRequestDto dto) {
        return ResponseEntity.ok(authService.activateStatus(dto));
    }

    @PostMapping("/activate-status-with-rabbitmq")
    public ResponseEntity<Boolean> activateStatusWithRabbitMq(@RequestBody ActivateStatusRequestDto dto) {
        return ResponseEntity.ok(authService.activateStatusRabbitMq(dto));
    }

    @GetMapping("/create-token")
    public ResponseEntity<String> createToken(Long id, ERole role){
        return ResponseEntity.ok(tokenManager.createToken(id,role).get());
    }

    @GetMapping("/create-token1")
    public ResponseEntity<String> createToken(Long id){
        return ResponseEntity.ok(tokenManager.createToken(id).get());
    }

    @GetMapping("/get-id-from-token")
    public ResponseEntity<Long> getIdFromToken(String token){
        return ResponseEntity.ok(tokenManager.getIdFromToken(token).get());
    }

    @GetMapping("/get-role-from-token")
    public ResponseEntity<String> getRoleFromToken(String token){
        return ResponseEntity.ok(tokenManager.getRoleFromToken(token).get());
    }

    @PutMapping(UPDATE)
    public ResponseEntity<Boolean> updateEmail(@RequestBody AuthUpdateRequestDto dto){
        authService.updateEmail(dto);
        return ResponseEntity.ok(true);
    }

    @DeleteMapping(DELETE_BY_TOKEN)
    public ResponseEntity<Boolean> softDeleteByToken(@RequestParam String token){
        return ResponseEntity.ok(authService.softDeleteByToken(token));
    }

    @GetMapping("get-string")
    public ResponseEntity<String> getString(String value){
        return ResponseEntity.ok(authService.getString(value));
    }

    @DeleteMapping("redis-delete")
    public ResponseEntity<Boolean> redisDelete(){
        return ResponseEntity.ok(authService.redisDelete());
    }


    @DeleteMapping("redis-delete2")
    public ResponseEntity<Boolean> redisDelete(String value){
        return ResponseEntity.ok(authService.redisDelete2(value));
    }

    @GetMapping("find-by-role")
    public ResponseEntity<List<Long>> findByRole(@RequestParam ERole role){
        return ResponseEntity.ok(authService.findByRole(role));
    }
}
