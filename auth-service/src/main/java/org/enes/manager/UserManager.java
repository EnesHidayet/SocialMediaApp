package org.enes.manager;

import org.enes.dto.request.CreateUserRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import static org.enes.constants.RestApiUrl.*;

@FeignClient(url = "http://localhost:7071/dev/v1/user", name = "auth-userprofile")
public interface UserManager {
    @PostMapping("/create")
    public ResponseEntity<Boolean> createUser(@RequestBody CreateUserRequestDto dto);

    @GetMapping("/activation")
    public ResponseEntity<Boolean> Activation(@RequestParam Long authId);
}
