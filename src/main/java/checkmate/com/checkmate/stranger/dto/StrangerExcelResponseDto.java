package checkmate.com.checkmate.stranger.dto;

import checkmate.com.checkmate.student.dto.StudentExcelResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@Getter
@RequiredArgsConstructor(access=PRIVATE)
@Schema(description = "Stranger Excel Response")
public class StrangerExcelResponseDto {

    @Schema(description = "외부인 이름", example = "홍길동")
    private final String strangerName;

    @Schema(description = "외부인 전화번호", example = "010-1234-5678")
    private final String strangerPhoneNumber;

    @Schema(description = "외부인 메일주소", example = "abcd@gmail.com")
    private final String strangerEmail;

    @Schema(description = "외부인 소속", example = "LG")
    private final String strangerAffiliation;

    public static StrangerExcelResponseDto of(String strangerName, String strangerPhoneNumber, String strangerEmail, String strangerAffiliation){
        return new StrangerExcelResponseDto(
                strangerName,
                strangerPhoneNumber,
                strangerEmail,
                strangerAffiliation
        );
    }
}
