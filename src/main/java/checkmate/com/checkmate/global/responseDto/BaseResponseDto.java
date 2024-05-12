package checkmate.com.checkmate.global.responseDto;

import checkmate.com.checkmate.global.codes.ErrorCode;
import checkmate.com.checkmate.global.codes.SuccessCode;
import lombok.*;

@Getter
@RequiredArgsConstructor
@ToString
public class BaseResponseDto<T> {
    private final Boolean isSuccess;
    private final int statusCode;
    private final String message;
    private T result;

    public static BaseResponseDto<?> ofSuccess(SuccessCode code) {
        return DataResponseDto.of(code.getMessage());
    }

    public static <T> DataResponseDto<T> ofSuccess(SuccessCode code, T result) {
        return DataResponseDto.of(code.getMessage(), result);
    }

    public static BaseResponseDto ofFailure(ErrorCode code) {
        return ErrorResponseDto.of(code.getHttpStatus(), code.getMessage());
    }

}