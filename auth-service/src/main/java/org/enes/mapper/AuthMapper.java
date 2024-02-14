package org.enes.mapper;

import org.enes.dto.request.CreateUserRequestDto;
import org.enes.dto.request.RegisterRequestDto;
import org.enes.dto.response.RegisterResponseDto;
import org.enes.entity.Auth;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AuthMapper {
    AuthMapper INSTANCE = Mappers.getMapper(AuthMapper.class);


    Auth toAuth(final RegisterRequestDto dto);

    RegisterResponseDto toRegisterResponseDto(final Auth auth);

    @Mapping(source = "id" , target = "authId")
    CreateUserRequestDto fromAuthToCreateUserRequestDto(final Auth auth);

}
