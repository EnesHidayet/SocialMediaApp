package org.enes.manager;

import org.enes.dto.request.CreateUserRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.enes.constants.RestApiUrl.*;

@FeignClient(url = "http://localhost:7071/dev/v1/user", name = "auth-userprofile")
public interface UserManager {
    @PostMapping("/create")
    ResponseEntity<Boolean> createUser(@RequestBody CreateUserRequestDto dto);

    @GetMapping("/activation")
    ResponseEntity<Boolean> Activation(@RequestParam Long authId);

    @DeleteMapping(DELETE_BY_ID)
    ResponseEntity<Boolean> softDeleteByToken(@RequestParam String token);
}
