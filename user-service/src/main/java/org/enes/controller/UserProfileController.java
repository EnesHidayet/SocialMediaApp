package org.enes.controller;

import lombok.RequiredArgsConstructor;
import org.enes.dto.request.CreateUserRequestDto;
import org.enes.dto.request.FollowRequestDto;
import org.enes.dto.request.UserProfileUpdateRequestDto;
import org.enes.entity.Follow;
import org.enes.entity.UserProfile;
import org.enes.service.FollowService;
import org.enes.service.UserProfileService;
import org.enes.utility.enums.ERole;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static org.enes.constants.RestApiUrl.*;
@RestController
@RequiredArgsConstructor
@RequestMapping(USER)
public class UserProfileController {
    private final UserProfileService userProfileService;
    private final FollowService followService;

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

    @PutMapping(UPDATE)
    public ResponseEntity<Boolean> update(@RequestBody UserProfileUpdateRequestDto dto){
        return ResponseEntity.ok(userProfileService.update(dto));
    }

    @DeleteMapping(DELETE_BY_TOKEN)
    public ResponseEntity<Boolean> softDeleteByToken(@RequestParam String token){
        return ResponseEntity.ok(userProfileService.softDeleteByToken(token));
    }


    @GetMapping("find-by-username")
    public ResponseEntity<UserProfile> findByUsername(@RequestParam String value){
        return ResponseEntity.ok(userProfileService.findByUsername(value));
    }

    @GetMapping("find-by-role")
    public ResponseEntity<List<UserProfile>> findByRole(@RequestParam ERole role){
        return ResponseEntity.ok(userProfileService.findByRole(role));
    }

    @PostMapping(CREATE+"follow")
    public ResponseEntity<Boolean> createFollow(@RequestBody FollowRequestDto follow) {
        return ResponseEntity.ok(followService.createFollow(follow));
    }

    @DeleteMapping(DELETE+"follow")
    public ResponseEntity<Boolean> deleteFollow(@RequestParam String followId) {
        return ResponseEntity.ok(followService.deleteFollow(followId));
    }

    @GetMapping(FIND_ALL+"follow")
    public ResponseEntity<List<Follow>> findAllFollow() {
        return ResponseEntity.ok(followService.findAll());
    }

    @GetMapping(ACTIVATION+"follow")
    public ResponseEntity<Boolean> activationFollow(@RequestParam String id,@RequestParam String followingId) {
        return ResponseEntity.ok(followService.activationFollow(id,followingId));
    }
}
