package org.enes.service;


import lombok.extern.slf4j.Slf4j;
import org.enes.dto.request.AuthUpdateRequestDto;
import org.enes.dto.request.CreateUserRequestDto;
import org.enes.dto.request.UserProfileUpdateRequestDto;
import org.enes.entity.UserProfile;
import org.enes.exception.ErrorType;
import org.enes.exception.UserManagerException;
import org.enes.manager.AuthManager;
import org.enes.mapper.UserProfileMapper;
import org.enes.rabbitmq.model.CreateUserModel;
import org.enes.rabbitmq.producer.CreateUserElasticProducer;
import org.enes.repository.UserProfileRepository;
import org.enes.utility.JwtTokenManager;
import org.enes.utility.ServiceManager;
import org.enes.utility.enums.ERole;
import org.enes.utility.enums.EStatus;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@Slf4j
public class UserProfileService extends ServiceManager<UserProfile, String> {

    private final UserProfileRepository userProfileRepository;
    private final JwtTokenManager manager;
    private final AuthManager authManager;
    private final CacheManager cacheManager;
    private final CreateUserElasticProducer createUserElasticProducer;

    public UserProfileService(UserProfileRepository repository, JwtTokenManager manager, AuthManager authManager, CacheManager cacheManager, CreateUserElasticProducer createUserElasticProducer) {
        super(repository);
        this.userProfileRepository = repository;
        this.manager = manager;
        this.authManager = authManager;
        this.cacheManager = cacheManager;
        this.createUserElasticProducer = createUserElasticProducer;
    }


    public Boolean createUser(CreateUserRequestDto dto) {
        try {
            UserProfile userProfile = save(UserProfileMapper.INSTANCE.fromCreateRequestToUserProfile(dto));

            createUserElasticProducer.sendMessage(CreateUserModel.builder()
                    .authId(userProfile.getAuthId())
                    .username(userProfile.getUsername())
                    .email(userProfile.getEmail())
                    .build());

            return true;
        }catch (Exception e){
            throw new UserManagerException(ErrorType.USER_NOT_CREATED);
        }
    }

    public Boolean Activation(Long authId){
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Optional<UserProfile> user = userProfileRepository.findByAuthId(authId);
        System.out.println(user);
        if (user.isEmpty()){
            throw new UserManagerException(ErrorType.USER_NOT_FOUND);
        }
        user.get().setStatus(EStatus.ACTIVE);
        update(user.get());
        return true;
    }

    public Boolean update(UserProfileUpdateRequestDto dto){
        Optional<Long> authId = manager.getIdFromToken(dto.getToken());
        if (authId.isEmpty()){
            throw new UserManagerException(ErrorType.INVALID_TOKEN);
        }
        Optional<UserProfile> user = userProfileRepository.findByAuthId(authId.get());
        if (user.isEmpty()){
            throw new UserManagerException(ErrorType.USER_NOT_FOUND);
        }

        user.get().setEmail(dto.getEmail());
        user.get().setPhone(dto.getPhone());
        user.get().setAvatar(dto.getAvatar());
        user.get().setAddress(dto.getAddress());
        user.get().setAbout(dto.getAbout());
        update(user.get());

        AuthUpdateRequestDto authUpdateRequestDto = AuthUpdateRequestDto.builder().id(user.get().getAuthId()).email(dto.getEmail()).build();
        authManager.updateEmail(authUpdateRequestDto);

        try {
            cacheManager.getCache("find-by-username").evict(user.get().getUsername());
            cacheManager.getCache("find-all-by-role").clear();
        }catch (Exception e){
            throw new UserManagerException(ErrorType.USER_NOT_FOUND);
        }
        return true;
    }

    public Boolean softDeleteByToken(String token){
        Optional<Long> id = manager.getIdFromToken(token);
        if (id.isEmpty()){
            throw new UserManagerException(ErrorType.INVALID_TOKEN);
        }
        Optional<UserProfile> userProfile = userProfileRepository.findByAuthId(id.get());
        if (userProfile.isEmpty()){
            throw new UserManagerException(ErrorType.USER_NOT_FOUND);
        }
        userProfile.get().setStatus(EStatus.DELETED);
        update(userProfile.get());
        return true;
    }

    @Cacheable(value = "find-by-username", key = "#value.toLowerCase()")
    public UserProfile findByUsername(String value) {
        try{
            Thread.sleep(3000L);
        }catch (InterruptedException exception){
            log.error("Beklenmeyen thread hatası");
        }

        Optional<UserProfile> user = userProfileRepository.findByUsernameIgnoreCase(value);
        if (user.isEmpty()){
            throw new UserManagerException(ErrorType.USER_NOT_FOUND);
        }
        return user.get();
    }

    @Cacheable(value = "find-all-by-role")
    public List<UserProfile> findByRole(ERole role){
        List<Long> idler = authManager.findByRole(role).getBody();
        List<UserProfile> userProfileList = new ArrayList<>();
        idler.forEach(x->{
            userProfileList.add(userProfileRepository.findByAuthId(x).get());
        });
        return userProfileList;
    }

}
