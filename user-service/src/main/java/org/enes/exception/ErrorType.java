package org.enes.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum ErrorType {

    INTERNAL_SERVER_ERROR(5200,"Sunucuda beklenmeyen hata oluştu, lütfen tekrar deneyiniz.",HttpStatus.INTERNAL_SERVER_ERROR),

    BAD_REQUEST(4200,"Girilen parametreler hatalıdır.Lütfen düzelterek tekrar deneyiniz.",HttpStatus.BAD_REQUEST),

    USERNAME_DUPLICATE(4211,"Kullanıcı adı kullanılmaktadır.Lütfen başka bir kullanıcı adı seçiniz.",HttpStatus.BAD_REQUEST),

    USER_NOT_FOUND(4212,"Böyle bir kullanıcı bulunamadı." ,HttpStatus.BAD_REQUEST),

    USER_NOT_CREATED(4213,"Kullanıcı oluşturulamadı." ,HttpStatus.BAD_REQUEST );



    int code;
    String message;
    HttpStatus httpStatus;
}
