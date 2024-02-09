package org.enes.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum ErrorType {

    INTERNAL_SERVER_ERROR(5100,"Sunucuda beklenmeyen hata oluştu, lütfen tekrar deneyiniz.",HttpStatus.INTERNAL_SERVER_ERROR),

    BAD_REQUEST(4100,"Girilen parametreler hatalıdır.Lütfen düzelterek tekrar deneyiniz.",HttpStatus.BAD_REQUEST),

    USERNAME_DUPLICATE(4111,"Kullanıcı adı kullanılmaktadır.Lütfen başka bir kullanıcı adı seçiniz.",HttpStatus.BAD_REQUEST),

    LOGIN_ERROR(4110,"Kullanıcı adı ya da şifre hatalıdır.Lütfen tekrar deneyiniz.",HttpStatus.BAD_REQUEST),
    ACTIVATION_ERROR(1001,"Aktivasyon kodu hatalıdır. Lütfen tekrar deneyiniz." ,HttpStatus.BAD_REQUEST);



    int code;
    String message;
    HttpStatus httpStatus;
}
