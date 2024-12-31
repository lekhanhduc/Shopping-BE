package vn.khanhduc.shoppingbackendservice.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import vn.khanhduc.shoppingbackendservice.dto.http.ErrorResponse;
import vn.khanhduc.shoppingbackendservice.dto.http.ResponseData;
import java.time.LocalDateTime;
import java.util.List;

@ControllerAdvice
public class GlobalException {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponse> handleAppException(AppException ex, HttpServletRequest request) {

        ErrorCode errorCode = ex.getErrorCode();
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(errorCode.getCode())
                .error(errorCode.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex,
                                                                               HttpServletRequest request) {

        var bindResult = ex.getBindingResult();
        List<FieldError> fieldErrors = bindResult.getFieldErrors();
        List<String> errors = fieldErrors.stream().map(FieldError::getDefaultMessage).toList();
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(errors.size() > 1 ? String.valueOf(errors) : errors.getFirst())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ResponseData<Object>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {

        ResponseData<Object> responseData = ResponseData.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message("Id invalid, id must be a number greater than 0 ")
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(responseData);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ResponseData<Object>> handleConstraintViolationException(ConstraintViolationException ex) {

        List<String> errors = ex.getConstraintViolations()
                .stream().map(violation -> String.format("Field '%s' : %s",
                        violation.getPropertyPath(), violation.getMessage())
                ).toList();

        ResponseData<Object> responseData = ResponseData.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .data(errors)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(responseData);
    }

}
