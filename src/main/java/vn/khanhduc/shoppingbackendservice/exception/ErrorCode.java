package vn.khanhduc.shoppingbackendservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    USER_NOT_EXISTED(404, "User not existed", HttpStatus.NOT_FOUND),
    USER_EXISTED(404, "User existed", HttpStatus.NOT_FOUND),
    TOKEN_EXPIRED(400, "Token expired", HttpStatus.BAD_REQUEST),
    TOKEN_INVALID(400, "Token Invalid", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(401, "Unauthorized", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_INVALID(400, "Refresh Token Invalid", HttpStatus.BAD_REQUEST),
    PRODUCT_NOT_EXISTED(404, "Product not existed", HttpStatus.NOT_FOUND),
    VERIFICATION_CODE_INVALID(400, "Verification Code Invalid", HttpStatus.BAD_REQUEST),
    ;
    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

}
