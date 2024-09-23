package checkmate.com.checkmate.event.dto;

import checkmate.com.checkmate.event.domain.Event;
import checkmate.com.checkmate.eventschedule.domain.EventSchedule;
import checkmate.com.checkmate.eventschedule.dto.EventScheduleRequestDto;
import checkmate.com.checkmate.global.domain.EventType;
import checkmate.com.checkmate.global.domain.EventTarget;
import checkmate.com.checkmate.member.domain.Member;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@Schema(description = "Event Request")
public class EventRequestDto {

    @NotBlank(message="행사명을 입력해주세요.")
    @Size(max = 30, message = "행사명은 30자를 초과할 수 없습니다.")
    @Schema(description = "행사 제목", example = "제목")
    private final String eventTitle;

    @Size(max = 250, message = "행사 내용은 250자를 초과할 수 없습니다.")
    @Schema(description = "행사 설명", example = "설명입니다.")
    private final String eventDetail;

    @NotBlank(message="행사 일정은 하나 이상 입력해주세요.")
    @Schema(description = "행사 일정")
    private final List<EventScheduleRequestDto> eventSchedules;

    @Min(value = 1, message = "최소이수기준은 1 이상이어야 합니다.")
    @NotBlank(message="최소 이수기준을 입력해주세요.")
    @Schema(description = "행사 최소 이수 기준", example = "2")
    private final int completionTimes;

    @NotBlank(message="행사 유형을 선택해주세요.")
    @Schema(description = "행사 유형", example = "ONLINE")
    private final EventType eventType;

    @NotBlank(message="행사 대상을 선택해주세요.")
    @Schema(description = "행사 대상", example = "INTERNAL")
    private final EventTarget eventTarget;

    public Event toEntity(Member member){
        return Event.builder()
                .member(member)
                .eventTitle(eventTitle)
                .eventDetail(eventDetail)
                .eventType(eventType)
                .completionTime(completionTimes)
                .eventType(eventType)
                .eventTarget(eventTarget)
                .managerEmail(member.getMemberEmail())
                .build();
    }
}
