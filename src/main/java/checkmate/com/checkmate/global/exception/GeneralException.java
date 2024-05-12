package checkmate.com.checkmate.global.exception;

import checkmate.com.checkmate.global.codes.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class GeneralException extends RuntimeException {

    private final ErrorCode errorCode;
}
