package checkmate.com.checkmate.global.responseDto;

import checkmate.com.checkmate.global.codes.ErrorCode;
import checkmate.com.checkmate.global.codes.SuccessCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@RequiredArgsConstructor
@ToString
@Schema(description = "Base Response")
public class BaseResponseDto<T> {

    @Schema(description = "성공 여부", example = "false")
    private final Boolean isSuccess;

    @Schema(description = "상태 코드", example = "POST_EVENT_SUCCESS")
    private final int statusCode;

    @Schema(description = "메세지", example = "행사 등록에 성공하였습니다.")
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