package checkmate.com.checkmate.global.codes;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    //일반적인 응답
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 에러, 관리자에게 문의 바랍니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    IO_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "IO 예외입니다."),
    INVALID_AUTHORITY(HttpStatus.UNAUTHORIZED, "해당 리소스에 대한 접근 권한이 없습니다."),

    //인증&인가
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "유효한 토큰이 아닙니다."),
    UNSUPPORTED_TOKEN(HttpStatus.BAD_REQUEST, "지원하지 않는 토큰입니다."),
    MALFORMED_TOKEN(HttpStatus.BAD_REQUEST, "유효하지 않은 구성의 토큰입니다."),
    NULL_TOKEN(HttpStatus.BAD_REQUEST, "토큰이 존재하지 않습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "잘못된 리프레시 토큰입니다."),
    INVALID_ACCESS_TOKEN(HttpStatus.BAD_REQUEST, "잘못된 엑세스 토큰입니다."),
    NULL_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "리프레시 토큰이 빈 값입니다."),
    EXPIRED_TOKEN(HttpStatus.BAD_REQUEST, "만료된 토큰입니다."),
    NULL_AUTHORIZATION_HEADER(HttpStatus.UNAUTHORIZED, "Authorization Header가 빈 값입니다."),
    INVALID_OAUTH_TOKEN(HttpStatus.BAD_REQUEST, "토큰을 가져올 수 없습니다."),
    FAIL_VALIDATE_TOKEN(HttpStatus.BAD_REQUEST,  "토큰 유효성 검사 중 오류가 발생했습니다."),

    //유저
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."),
    USER_EXISTS(HttpStatus.BAD_REQUEST, "이미 존재하는 사용자입니다."),
    TOKEN_AND_USER_NOT_CORRESPONDS(HttpStatus.BAD_REQUEST, "토큰 정보와 유저 정보가 일치하지 않습니다"),
    INVALID_USER_INFO(HttpStatus.BAD_REQUEST,  "유저 정보를 가져올 수 없습니다."),


    //행사
    EVENT_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 이벤트입니다. userId와 eventId를 확인해주세요"),
    IMAGE_IS_NULL(HttpStatus.BAD_REQUEST, "이미지가 없습니다. 행사 이미지를 첨부해주세요"),
    FILE_IS_NULL(HttpStatus.BAD_REQUEST, "파일이 없습니다. 출석명단 파일을 첨부해주세요."),
    S3_UPLOAD_ERROR(HttpStatus.SERVICE_UNAVAILABLE, "S3 업로드에 실패하였습니다."),
    EVENT_LIST_NOT_FOUND(HttpStatus.NOT_FOUND, "행사 목록을 찾을 수 없습니다. userId를 확인해주세요."),
    FILE_READ_FAIL(HttpStatus.BAD_REQUEST, "파일 형식이 잘못되었습니다."),

    //출석체크
    STUDENT_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 학생 정보입니다."),
    STUDENT_ALREADY_CHECK(HttpStatus.CONFLICT, "이미 출석체크한 학생입니다.");

    private final HttpStatus httpStatus;
    private final String message;
}