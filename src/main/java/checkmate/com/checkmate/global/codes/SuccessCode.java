package checkmate.com.checkmate.global.codes;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SuccessCode {
    // 일반적인 응답
    OK(HttpStatus.OK, "요청에 성공하였습니다."),

    // 인증 & 인가 & 로그인
    GOOGLE_LOGIN_SUCCESS(HttpStatus.OK, "구글 로그인에 성공하였습니다."),
    SIGNUP_SUCCESS(HttpStatus.OK, "회원가입에 성공하였습니다."),
    SIGNOUT_SUCCESS(HttpStatus.OK, "회원 탈퇴에 성공하였습니다."),
    REISSUE_TOKEN_SUCCESS(HttpStatus.OK, "토큰 갱신에 성공하였습니다."),
    LOGOUT_SUCCESS(HttpStatus.OK, "로그아웃에 성공하였습니다."),

    // 유저
    GET_USER_SUCCESS(HttpStatus.OK, "회원 정보 조회 요청에 성공하였습니다."),
    MODIFY_USER_SUCCESS(HttpStatus.OK, "회원 정보 수정에 성공하였습니다."),

    //행사
    POST_EVENT_SUCCESS(HttpStatus.OK, "행사 등록에 성공하였습니다."),
    GET_EVENT_DETAIL_SUCCESS(HttpStatus.OK, "행사 상세 조회에 성공하였습니다."),
    GET_EVENT_LIST_SUCCESS(HttpStatus.OK, "행사 목록 조회에 성공하였습니다."),
    MODIFY_EVENT_SUCCESS(HttpStatus.OK, "행사 수정에 성공하였습니다."),
    DELETE_EVENT_SUCCESS(HttpStatus.OK, "행사 삭제에 성공하였습니다."),
    REGISTER_EVENT_MANAGER_SUCCESS(HttpStatus.OK, "행사 담당자 등록에 성공하였습니다."),
    REGISTER_SUREY_URL_SUCCESS(HttpStatus.OK, "행사 설문조사 링크 등록에 성공하였습니다." ),


    //메일
    SEND_BEFORE_MAIL(HttpStatus.OK, "행사 리마인드 알람 전송에 성공하였습니다."),
    SEND_AFTER_MAIL(HttpStatus.OK, "행사 만족도조사 알람 전송에 성공하였습니다."),

    //출석체크
    GET_STUDENT_INFO_SUCCESS(HttpStatus.OK, "학생 정보 조회에 성공하였습니다."),
    ATTENDANCE_CHECK_SUCCESS(HttpStatus.OK,"출석 체크에 성공하였습니다."),

    //출석명단
    GET_ATTENDANCE_LIST_SUCCESS(HttpStatus.OK, "출석명단 조회에 성공하였습니다."),
    SEND_ATTENDACE_LIST_SUCCESS(HttpStatus.OK, "출석명단 전송에 성공하였습니다."),
    UPDATE_ATTENDNACE_LIST_SUCCESS(HttpStatus.OK, "출석명단 수정에 성공하였습니다."),
    REMOVE_ATTENDANCE_SUCCESS(HttpStatus.OK, "출석명단 일부 삭제에 성공하였습니다." );

    private final HttpStatus httpStatus;
    private final String message;
}