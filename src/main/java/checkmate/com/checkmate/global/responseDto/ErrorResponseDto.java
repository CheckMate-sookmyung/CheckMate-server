package checkmate.com.checkmate.global.responseDto;

import org.springframework.http.HttpStatus;

/* 통신 실패했을 때 Response Dto */
public class ErrorResponseDto extends BaseResponseDto {

    private ErrorResponseDto(HttpStatus httpStatus, String message) {
        super(false, httpStatus.value(), message);
    }

    public static ErrorResponseDto of(HttpStatus httpStatus, String message){
        return new ErrorResponseDto(httpStatus, message);
    }

}