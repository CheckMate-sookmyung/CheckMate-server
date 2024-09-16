package checkmate.com.checkmate.event.dto;

import checkmate.com.checkmate.event.domain.Event;
import checkmate.com.checkmate.eventschedule.domain.EventSchedule;
import checkmate.com.checkmate.eventschedule.dto.EventScheduleRequestDto;
import checkmate.com.checkmate.global.domain.EventType;
import checkmate.com.checkmate.global.domain.EventTarget;
import checkmate.com.checkmate.member.domain.Member;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Getter
@AllArgsConstructor
@Data
//@RequiredArgsConstructor
public class EventRequestDto {

    @NotBlank(message="행사명을 입력해주세요.")
    @Size(max = 30, message = "행사명은 30자를 초과할 수 없습니다.")
    @JsonProperty("eventTitle")  // JSON 필드와 매핑
    private final String eventTitle;

    @Size(max = 250, message = "행사 내용은 250자를 초과할 수 없습니다.")
    @JsonProperty("eventDetail")
    private final String eventDetail;

    @NotBlank(message="행사 일정은 하나 이상 입력해주세요.")
    @JsonProperty("eventSchedules")
    private final List<EventScheduleRequestDto> eventSchedules;

    @Min(value = 1, message = "최소이수기준은 1 이상이어야 합니다.")
    @JsonProperty("completionTimes")
    private final int completionTimes;

    @JsonProperty("eventUrl")
    private final String eventUrl;

    @NotNull(message="리마인드 알람 전송 여부를 입력해주세요.")
    @JsonProperty("alarmRequest")
    private final Boolean alarmRequest;

    @NotBlank(message="행사 유형을 선택해주세요.")
    @JsonProperty("eventType")
    private final EventType eventType;

    @NotBlank(message="행사 대상을 선택해주세요.")
    @JsonProperty("eventTarget")
    private final EventTarget eventTarget;

    public Event toEntity(Member member){
        return Event.builder()
                .member(member)
                .eventTitle(eventTitle)
                .eventDetail(eventDetail)
                .eventType(eventType)
                .completionTime(completionTimes)
                .eventUrl(eventUrl)
                .eventType(eventType)
                .eventTarget(eventTarget)
                .managerEmail(member.getMemberEmail())
                .build();
    }
}
