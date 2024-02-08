package org.enes.service;

import lombok.RequiredArgsConstructor;
import org.enes.dto.request.RegisterRequestDto;
import org.enes.dto.response.RegisterResponseDto;
import org.enes.entity.Auth;
import org.enes.mapper.AuthMapper;
import org.enes.repository.AuthRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthRepository authRepository;
    private final AuthMapper authMapper;

    public RegisterResponseDto register(RegisterRequestDto dto) {
        Auth auth = authMapper.toAuth(dto);
        auth.setCreateDate(System.currentTimeMillis());
        auth.setUpdateDate(System.currentTimeMillis());
        authRepository.save(auth);
        return authMapper.toRegisterResponseDto(auth);
    }
}
