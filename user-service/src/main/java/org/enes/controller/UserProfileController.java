package org.enes.controller;

import lombok.RequiredArgsConstructor;
import org.enes.dto.request.CreateUserRequestDto;
import org.enes.entity.UserProfile;
import org.enes.service.UserProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.enes.constants.RestApiUrl.*;
@RestController
@RequiredArgsConstructor
@RequestMapping(USER)
public class UserProfileController {
    private final UserProfileService userProfileService;

    @PostMapping(CREATE)
    public ResponseEntity<Boolean> createUser(@RequestBody CreateUserRequestDto dto){
        return ResponseEntity.ok(userProfileService.createUser(dto));
    }

    @GetMapping(ACTIVATION)
    public ResponseEntity<Boolean> Activation(@RequestParam Long authId){
        return ResponseEntity.ok(userProfileService.Activation(authId));
    }

    @GetMapping(FIND_ALL)
    public ResponseEntity<List<UserProfile>> findAll(){
        return ResponseEntity.ok(userProfileService.findAll());
    }
}
