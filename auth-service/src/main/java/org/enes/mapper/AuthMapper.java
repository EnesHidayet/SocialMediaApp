package org.enes.mapper;

import org.enes.dto.request.RegisterRequestDto;
import org.enes.dto.response.RegisterResponseDto;
import org.enes.entity.Auth;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AuthMapper {
    AuthMapper INSTANCE = Mappers.getMapper(AuthMapper.class);


    Auth toAuth(RegisterRequestDto dto);

    RegisterResponseDto toRegisterResponseDto(Auth auth);

}