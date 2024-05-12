package checkmate.com.checkmate.global.responseDto;


import checkmate.com.checkmate.global.codes.SuccessCode;
import lombok.Getter;

/* 통신이 제대로 됐을 때 Response DTO */
@Getter
public class DataResponseDto<T> extends BaseResponseDto {
    private T result;

    // 생성자 without 메시지
    private DataResponseDto(T result){
        super(true, SuccessCode.OK.getHttpStatus().value(), SuccessCode.OK.getMessage());
        this.result = result;
    }

    // 생성자 with 특정 메시지
    private DataResponseDto(String message, T result) {
        super(true, SuccessCode.OK.getHttpStatus().value(), message);
        this.result = result;
    }

    // 새로운 생성자 without result
    private DataResponseDto(SuccessCode successCode) {
        super(true, successCode.getHttpStatus().value(), successCode.getMessage());
    }

    // 새로운 생성자 with result
    private DataResponseDto(SuccessCode successCode, T result) {
        super(true, successCode.getHttpStatus().value(), successCode.getMessage());
        this.result = result;
    }

    public static <T> DataResponseDto<T> of(T result) {
        return new DataResponseDto<>(result);
    }

    public static <T> DataResponseDto<T> of(String message, T result) {
        return new DataResponseDto<>(message, result);
    }

    public static <T> DataResponseDto<T> of(String message){
        return new DataResponseDto<>(message, null);
    }

    // new
    public static <T> DataResponseDto<T> of(SuccessCode successCode) {
        return new DataResponseDto<>(successCode);
    }

    // new
    public static <T> DataResponseDto<T> of(SuccessCode successCode, T result) {
        return new DataResponseDto<>(successCode, result);
    }

    public static <T> DataResponseDto<T> empty() {
        return new DataResponseDto<>(null);
    }
}