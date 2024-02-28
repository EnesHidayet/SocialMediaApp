package org.enes.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
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
import org.enes.rabbitmq.model.ActivationMailModel;
import org.enes.rabbitmq.model.CreateUserModel;
import org.enes.rabbitmq.producer.ActivateUserProducer;
import org.enes.rabbitmq.producer.ActivationMailProducer;
import org.enes.rabbitmq.producer.CreateUserProducer;
import org.enes.repository.AuthRepository;
import org.enes.utility.CodeGenerator;
import org.enes.utility.JwtTokenManager;
import org.enes.utility.enums.ERole;
import org.enes.utility.enums.EStatus;
import org.enes.utility.ServiceManager;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Slf4j
public class AuthService extends ServiceManager<Auth, Long> {
    private final AuthRepository authRepository;
    private final UserManager userManager;
    private final JwtTokenManager manager;
    private final CacheManager cacheManager;
    private final CreateUserProducer createUserProducer;
    private final ActivateUserProducer activateUserProducer;
    private final ActivationMailProducer activationMailProducer;

    public AuthService(AuthRepository repository, UserManager userManager, JwtTokenManager manager, CacheManager cacheManager, CreateUserProducer createUserProducer, ActivateUserProducer activateUserProducer, ActivationMailProducer activationMailProducer) {
        super(repository);
        this.authRepository = repository;
        this.userManager = userManager;
        this.manager = manager;
        this.cacheManager = cacheManager;
        this.createUserProducer = createUserProducer;
        this.activateUserProducer = activateUserProducer;
        this.activationMailProducer = activationMailProducer;
    }

    @Transactional
    public RegisterResponseDto register(RegisterRequestDto dto) {
        Auth auth = AuthMapper.INSTANCE.toAuth(dto);
        auth.setActivationCode(CodeGenerator.generateCode());
        save(auth);
//        try {
            userManager.createUser(AuthMapper.INSTANCE.fromAuthToCreateUserRequestDto(auth));
//        }catch (Exception e){
//            delete(auth);
//            throw new AuthManagerException(ErrorType.INTERNAL_SERVER_ERROR);
//        }
        try {
            cacheManager.getCache("find-all-by-role").clear();
        }catch (Exception e){
            throw new AuthManagerException(ErrorType.INTERNAL_SERVER_ERROR);
        }

        return AuthMapper.INSTANCE.toRegisterResponseDto(auth);
    }

    @Transactional
    public RegisterResponseDto registerWithRabbitMq(RegisterRequestDto dto) {
        Auth auth = AuthMapper.INSTANCE.toAuth(dto);
        auth.setActivationCode(CodeGenerator.generateCode());
        save(auth);
        try {
            cacheManager.getCache("find-all-by-role").clear();
        }catch (Exception e){
            throw new AuthManagerException(ErrorType.INTERNAL_SERVER_ERROR);
        }

        createUserProducer.sendMessage(CreateUserModel.builder()
                .email(auth.getEmail())
                .username(auth.getUsername())
                .authId(auth.getId())
                .build());

        activationMailProducer.sendMessage(ActivationMailModel.builder()
                        .activationCode(auth.getActivationCode())
                        .email(auth.getEmail())
                        .username(auth.getUsername())
                        .authId(auth.getId())
                .build());


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

    public Boolean activateStatusRabbitMq(ActivateStatusRequestDto dto) {
        Optional<Auth> auth = authRepository.findById(dto.getAuthId());
        if (auth.isEmpty()){
            throw new AuthManagerException(ErrorType.USER_NOT_FOUND);
        }
        if (auth.get().getActivationCode().equals(dto.getActivationCode())){
            auth.get().setStatus(EStatus.ACTIVE);
            update(auth.get());
            activateUserProducer.sendMessage(auth.get().getId());
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

    public Boolean softDeleteByToken(String token){
        Optional<Long> id = manager.getIdFromToken(token);
        if (id.isEmpty()){
            throw new AuthManagerException(ErrorType.INVALID_TOKEN);
        }
        Optional<Auth> auth = authRepository.findById(id.get());
        if (auth.isEmpty()){
            throw new AuthManagerException(ErrorType.USER_NOT_FOUND);
        }
        auth.get().setStatus(EStatus.DELETED);
        update(auth.get());
        userManager.softDeleteByToken(token);
        return true;
    }

    /**
     * Cacheable içerisine , key = "#value.toUpperCase()" gibi eklemeler yapılabilir
     * Bu da işimizi kolaylaştırır.
     * @param value
     * @return
     */
    @Cacheable(value = "get-string")
    public String getString(String value) {
        String value2 ="Girdiğiniz değer -> "+value.toUpperCase();

        try{
            Thread.sleep(3000L);
        }catch (InterruptedException exception){
            log.error("Beklenmeyen thread hatası");
        }

        return value2;
    }

    public Boolean redisDelete(){
        try {
            cacheManager.getCache("get-string").clear();
            return true;
        }catch (Exception e){
            throw new AuthManagerException(ErrorType.INTERNAL_SERVER_ERROR);
        }
    }

    public Boolean redisDelete2(String request){
        try {
            cacheManager.getCache("get-string").evict(request);
            return true;
        }catch (Exception e){
            throw new AuthManagerException(ErrorType.INTERNAL_SERVER_ERROR);
        }
    }

    public List<Long> findByRole(ERole role){
        List<Auth> auth = authRepository.findAllByRole(role);
        List<Long> authIdList =new ArrayList<>();
        auth.forEach(x->{
            authIdList.add(x.getId());
        });
        return authIdList;
    }
}
