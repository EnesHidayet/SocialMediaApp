package org.enes.dto.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequestDto {

    @Size(min = 3, max = 20,message = "Kullanıcı adı en az 3, en fazla 20 karakter olabilir.")
    private String username;
    @Email
    private String email;
    @Size(min = 8, max = 32,message = "Kullanici şifresi en az 8 en fazla 32 karakter olabilir.")
    private String password;
}
