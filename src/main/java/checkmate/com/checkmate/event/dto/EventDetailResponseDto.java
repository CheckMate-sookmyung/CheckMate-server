package checkmate.com.checkmate.event.dto;

import checkmate.com.checkmate.event.domain.Event;
import checkmate.com.checkmate.eventschedule.domain.EventSchedule;
import checkmate.com.checkmate.eventschedule.dto.EventScheduleResponseDto;
import checkmate.com.checkmate.eventschedule.dto.StudentEventScheduleResponseDto;
import checkmate.com.checkmate.global.domain.EventTarget;
import checkmate.com.checkmate.global.domain.EventType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

@Getter
@RequiredArgsConstructor(access=PRIVATE)
@Schema(description = "Event Detail Response")
public class EventDetailResponseDto {

    @Schema(description = "행사 ID", example = "1")
    private final Long eventId;

    @Schema(description = "행사 제목", example = "제목")
    private final String eventTitle;

    @Schema(description = "행사 설명", example = "설명입니다.")
    private final String eventDetail;

    @Schema(description = "행사 사진 주소", example = "https://aws.com")
    private final String eventImage;

    @Schema(description = "행사 유형", example = "ONLINE")
    private final EventType eventType;

    @Schema(description = "행사 대상", example = "INTERNAL")
    private final EventTarget eventTarget;

    @Schema(description = "행사 일정")
    private final List<EventScheduleResponseDto> eventSchedules;

    @Schema(description = "행사 메일 발송 여부 (보낼것인지)", example = "true")
    private final boolean mailRequest;

    @Schema(description = "행사 웹사이트 주소", example = "https://checkmate.pe.kr")
    private final String eventUrl;

    @Schema(description = "행사 메일 발송 여부 (보냈는지)", example = "true")
    private final boolean mailResponse;

    @Schema(description = "행사 최소 이수 기준", example = "2")
    private final int completionTime;

    @Schema(description = "행사 출석명단 주소", example = "https://aws.com")
    private final String eventAttendanceFile;

    @Schema(description = "행사 담당자 이름", example = "김숙명")
    private final String managerName;

    @Schema(description = "행사 담당자 전화번호", example = "010-1234-5678")
    private final String managerPhoneNumber;

    @Schema(description = "행사 담당자 메일주소", example = "checkmatesmwu@gmail.com")
    private final String managerEmail;

    @Schema(description = "행사 평균 출석자 수", example = "13")
    private final int averageAttendees;

    @Schema(description = "행사 출석 예정자 수", example = "23")
    private final int totalAttendees;

    public static EventDetailResponseDto of(Event event, List<EventSchedule> eventSchedules, int averageAttendees, int totalAttendees) {
        List<EventScheduleResponseDto> eventSchedulesDto = eventSchedules.stream()
                .map(EventScheduleResponseDto::of)
                .collect(Collectors.toList());
        return new EventDetailResponseDto(
                event.getEventId(),
                event.getEventTitle(),
                event.getEventDetail(),
                event.getEventImage(),
                event.getEventType(),
                event.getEventTarget(),
                eventSchedulesDto,
                event.getMailRequest(),
                event.getEventUrl(),
                event.getMailResponse(),
                event.getCompletionTime(),
                event.getBeforeAttendanceListFile(),
                event.getManagerName(),
                event.getManagerPhoneNumber(),
                event.getManagerEmail(),
                averageAttendees,
                totalAttendees
        );
    }
}
    

