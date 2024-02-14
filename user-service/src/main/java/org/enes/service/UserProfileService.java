package org.enes.service;


import org.enes.dto.request.CreateUserRequestDto;
import org.enes.entity.UserProfile;
import org.enes.exception.ErrorType;
import org.enes.exception.UserManagerException;
import org.enes.mapper.UserProfileMapper;
import org.enes.repository.UserProfileRepository;
import org.enes.utility.ServiceManager;
import org.enes.utility.enums.EStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class UserProfileService extends ServiceManager<UserProfile, String> {

    private final UserProfileRepository userProfileRepository;

    public UserProfileService(UserProfileRepository repository) {
        super(repository);
        this.userProfileRepository = repository;
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
}
