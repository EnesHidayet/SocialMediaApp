package org.enes.service;

import org.enes.dto.request.ActivateStatusRequestDto;
import org.enes.dto.request.AuthUpdateRequestDto;
import org.enes.dto.request.LoginRequestDto;
import org.enes.dto.request.RegisterRequestDto;
import org.enes.dto.response.RegisterResponseDto;
import org.enes.entity.Auth;
import org.enes.exception.AuthManagerException;
import org.enes.exception.ErrorType;
import org.enes.manager.UserManager;
import org.enes.mapper.AuthMapper;
import org.enes.repository.AuthRepository;
import org.enes.utility.CodeGenerator;
import org.enes.utility.JwtTokenManager;
import org.enes.utility.enums.EStatus;
import org.enes.utility.ServiceManager;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService extends ServiceManager<Auth, Long> {
    private final AuthRepository authRepository;
    private final UserManager userManager;
    private final JwtTokenManager manager;

    public AuthService(AuthRepository repository,UserManager userManager,JwtTokenManager manager) {
        super(repository);
        this.authRepository = repository;
        this.userManager = userManager;
        this.manager = manager;

    }

    public RegisterResponseDto register(RegisterRequestDto dto) {
        Auth auth = AuthMapper.INSTANCE.toAuth(dto);
        auth.setActivationCode(CodeGenerator.generateCode());
        save(auth);
        userManager.createUser(AuthMapper.INSTANCE.fromAuthToCreateUserRequestDto(auth));
        return AuthMapper.INSTANCE.toRegisterResponseDto(auth);
    }

    public String login(LoginRequestDto dto) {
        Optional<Auth> auth = authRepository.findByUsernameAndPassword(dto.getUsername(), dto.getPassword());
        if (auth.isEmpty()){
            throw new AuthManagerException(ErrorType.LOGIN_ERROR);
        }
        if(!auth.get().getStatus().equals(EStatus.ACTIVE)){
            throw new AuthManagerException(ErrorType.USER_NOT_ACTIVE);
        }
        return manager.createToken(auth.get().getId(),auth.get().getRole()).
                orElseThrow(() -> {
                    throw new AuthManagerException(ErrorType.TOKEN_NOT_CREATED);
                });
    }


    public Boolean activateStatus(ActivateStatusRequestDto dto) {
        Optional<Auth> auth = authRepository.findById(dto.getAuthId());
        if (auth.isEmpty()){
            throw new AuthManagerException(ErrorType.USER_NOT_FOUND);
        }
        if (auth.get().getActivationCode().equals(dto.getActivationCode())){
            auth.get().setStatus(EStatus.ACTIVE);
            update(auth.get());
            userManager.Activation(auth.get().getId());
            return true;
        }else {
            throw new AuthManagerException(ErrorType.ACTIVATION_CODE_ERROR);
        }
    }

    public Boolean updateEmail(AuthUpdateRequestDto dto){
        Optional<Auth> auth = authRepository.findById(dto.getId());
        if (auth.isEmpty()){
            throw new AuthManagerException(ErrorType.USER_NOT_FOUND);
        }
        auth.get().setEmail(dto.getEmail());
        update(auth.get());
        return true;
    }
}
