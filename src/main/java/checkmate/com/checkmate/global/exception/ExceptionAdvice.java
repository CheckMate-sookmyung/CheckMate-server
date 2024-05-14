package checkmate.com.checkmate.global.exception;


import checkmate.com.checkmate.global.codes.ErrorCode;
import checkmate.com.checkmate.global.responseDto.ErrorResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice  // 전역 예외 처리 핸들러
@Slf4j
// ResponseEntityExceptionHandler에는 스프링 예외에 대한 ExceptionHandler가 모두 구현되어 있으므로 이걸 상속받기
// 에러 메세지는 반환하지 않기 때문에 handleExceptionInternal을 오버라이드
public class ExceptionAdvice extends ResponseEntityExceptionHandler {

    // common 에러 처리 (대부분의 에러)
    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> exception(final Exception e) {
        log.warn("common 에러 처리");
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        String errorMessage = e.getMessage();

        return handleExceptionInternal(errorCode, errorMessage);
    }

    // custom한 예외 처리
    @ExceptionHandler(value = GeneralException.class)
    public ResponseEntity<Object> onThrowException(final GeneralException generalException) {
        log.warn("커스텀 에러 처리");
        ErrorCode errorCode = generalException.getErrorCode();

        return handleExceptionInternal(errorCode, errorCode.getMessage());
    }

    /**
     * IllegalArgumentException 처리
     * @param e the exception to handle
     * @param headers the headers to be written to the response
     * @param status the selected response status
     * @param request the current request
     * @return
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            final MethodArgumentNotValidException e,
            final HttpHeaders headers,
            final HttpStatusCode status,
            final WebRequest request
    ) {
        log.warn("handleMethodArgumentNotValid,", e);
        ErrorCode errorCode = ErrorCode.BAD_REQUEST;
        return handleExceptionInternal(errorCode, errorCode.getMessage());
    }

    /**
     * 실제로 에러 메세지를 보내는 메소드
     * @param errorCode
     * @param message
     * @return
     */
    private ResponseEntity<Object> handleExceptionInternal(ErrorCode errorCode, String message) {
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(makeErrorResponse(errorCode, message));
    }

    /**
     * 주어진 ErrorCode와 message로 ErrorResponseDto 만들기
     * @param errorCode
     * @param message
     * @return
     */
    private ErrorResponseDto makeErrorResponse(ErrorCode errorCode, String message) {
        return ErrorResponseDto.of(errorCode.getHttpStatus(), message);
    }

}