package org.enes.service;

import org.enes.dto.request.LoginRequestDto;
import org.enes.dto.request.RegisterRequestDto;
import org.enes.dto.response.RegisterResponseDto;
import org.enes.entity.Auth;
import org.enes.exception.AuthManagerException;
import org.enes.exception.ErrorType;
import org.enes.mapper.AuthMapper;
import org.enes.repository.AuthRepository;
import org.enes.utility.enums.CodeGenerator;
import org.enes.utility.enums.EStatus;
import org.enes.utility.enums.ServiceManager;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService extends ServiceManager<Auth, Long> {
    private final AuthRepository authRepository;

    public AuthService(AuthRepository repository) {
        super(repository);
        this.authRepository = repository;
    }

    public RegisterResponseDto register(RegisterRequestDto dto) {
        Auth auth = AuthMapper.INSTANCE.toAuth(dto);
        auth.setActivationCode(CodeGenerator.generateCode());
        save(auth);
        return AuthMapper.INSTANCE.toRegisterResponseDto(auth);
    }

    public Boolean login(LoginRequestDto dto) {
        Optional<Auth> auth = authRepository.findByUsernameAndPassword(dto.getUsername(), dto.getPassword());
        if (auth.isEmpty()){
            throw new AuthManagerException(ErrorType.LOGIN_ERROR);
        }
        return true;
    }

    public String activateAccount(String activationCode){
        Optional<Auth> auth = authRepository.findByActivationCode(activationCode);
        if (auth.isEmpty()){
            throw new AuthManagerException(ErrorType.ACTIVATION_ERROR);
        }
        auth.get().setStatus(EStatus.ACTIVE);
        authRepository.save(auth.get());
        return "Aktivasyon Başarılı.";
    }
}
