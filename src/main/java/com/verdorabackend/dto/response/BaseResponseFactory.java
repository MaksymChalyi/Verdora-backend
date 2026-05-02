package com.verdorabackend.dto.response;

import java.time.OffsetDateTime;
import org.springframework.http.HttpStatus;

public class BaseResponseFactory {

    public static <T> BaseResponse<T> success(HttpStatus status, String message, T data) {
        return new BaseResponse<>(OffsetDateTime.now(), status.value(), message, data);
    }

    public static BaseResponse<Void> success(HttpStatus status, String message) {
        return new BaseResponse<>(OffsetDateTime.now(), status.value(), message, null);
    }

    public static BaseResponse<Void> error(HttpStatus status, String message) {
        return new BaseResponse<>(OffsetDateTime.now(), status.value(), message, null);
    }
}
