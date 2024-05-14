package checkmate.com.checkmate.global.codes;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;

@Getter
@RequiredArgsConstructor
public enum StatusCode {
    OK(1000, HttpStatus.OK, "요청 성공"),

    BAD_REQUEST(2000, HttpStatus.BAD_REQUEST, "Bad request - 요청 구문이 잘못 되었습니다."),
    VALIDATION_ERROR(2001, HttpStatus.BAD_REQUEST, "Validation error - 타입이 맞지 않습니다."),
    NOT_FOUND(2002, HttpStatus.NOT_FOUND, "Not Found - 요청하는 값을 찾을 수 없습니다."),
    INTERNAL_ERROR(2003, HttpStatus.INTERNAL_SERVER_ERROR, "Internal error - 서버 에러"),
    DATA_ACCESS_ERROR(2004, HttpStatus.INTERNAL_SERVER_ERROR, "Data access error"),
    UNAUTHORIZED(2005, HttpStatus.UNAUTHORIZED, "User unauthorized");


    private final int code;
    private final HttpStatus httpStatus;
    private final String message;

    public String getMessage(Throwable e){
        return this.getMessage(this.getMessage() + " - " + e.getMessage());
    }

    public String getMessage(String message){
        return Optional.ofNullable(message).filter(Predicate.not(String::isBlank)).orElse(this.getMessage());
    }

    public static StatusCode valueOf(HttpStatus httpStatus) {
        if(httpStatus == null){
            //throw new GeneralException("HttpStatus is null.");
        }

        return Arrays.stream(values())
                .filter(errorCode -> errorCode.getHttpStatus() == httpStatus)
                .findFirst()
                .orElseGet(() -> {
                    if (httpStatus.is4xxClientError()) {
                        return StatusCode.BAD_REQUEST;
                    } else if (httpStatus.is5xxServerError()) {
                        return StatusCode.INTERNAL_ERROR;
                    } else {
                        return StatusCode.OK;
                    }
                });
    }
    @Override
    public String toString() {
        return String.format("%s (%d)", this.name(), this.getCode());
    }
}
