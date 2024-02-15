package org.enes.service;


import org.enes.dto.request.AuthUpdateRequestDto;
import org.enes.dto.request.CreateUserRequestDto;
import org.enes.dto.request.UserProfileUpdateRequestDto;
import org.enes.entity.UserProfile;
import org.enes.exception.ErrorType;
import org.enes.exception.UserManagerException;
import org.enes.manager.AuthManager;
import org.enes.mapper.UserProfileMapper;
import org.enes.repository.UserProfileRepository;
import org.enes.utility.JwtTokenManager;
import org.enes.utility.ServiceManager;
import org.enes.utility.enums.EStatus;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class UserProfileService extends ServiceManager<UserProfile, String> {

    private final UserProfileRepository userProfileRepository;
    private final JwtTokenManager manager;
    private final AuthManager authManager;

    public UserProfileService(UserProfileRepository repository, JwtTokenManager manager, AuthManager authManager) {
        super(repository);
        this.userProfileRepository = repository;
        this.manager = manager;
        this.authManager = authManager;
    }


    public Boolean createUser(CreateUserRequestDto dto) {
        try {
            save(UserProfileMapper.INSTANCE.fromCreateRequestToUserProfile(dto));
            return true;
        }catch (Exception e){
            throw new UserManagerException(ErrorType.USER_NOT_CREATED);
        }
    }

    public Boolean Activation(Long authId){
        Optional<UserProfile> user = userProfileRepository.findByAuthId(authId);
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
        return true;
    }

}
