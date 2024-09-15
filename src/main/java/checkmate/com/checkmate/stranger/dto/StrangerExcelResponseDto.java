package checkmate.com.checkmate.stranger.dto;

import checkmate.com.checkmate.student.dto.StudentExcelResponseDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@Getter
@RequiredArgsConstructor(access=PRIVATE)
public class StrangerExcelResponseDto {
    private final String strangerName;
    private final String strangerPhoneNumber;
    private final String strangerEmail;
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
