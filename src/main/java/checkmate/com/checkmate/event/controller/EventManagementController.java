package checkmate.com.checkmate.event.controller;

import checkmate.com.checkmate.auth.Auth;
import checkmate.com.checkmate.auth.domain.Accessor;
import checkmate.com.checkmate.event.dto.EventManagerRequestDto;
import checkmate.com.checkmate.event.service.EventService;
import checkmate.com.checkmate.eventschedule.dto.StudentEventScheduleResponseDto;
import checkmate.com.checkmate.global.responseDto.BaseResponseDto;
import checkmate.com.checkmate.global.responseDto.ErrorResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static checkmate.com.checkmate.global.codes.SuccessCode.REGISTER_EVENT_MANAGER_SUCCESS;
import static checkmate.com.checkmate.global.codes.SuccessCode.REGISTER_SUREY_URL_SUCCESS;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
@RequestMapping("/api/v1/events")
@Tag(name="행사 관리 API", description="행사를 관리할 수 있습니다.")
public class EventManagementController {

    @Autowired
    private final EventService eventService;

    @ResponseBody
    @PutMapping(value="/manager/{eventId}")
    @Operation(summary = "행사 담당자 등록")
    public BaseResponseDto<?> registerManager(@Parameter(hidden = true) @Auth final Accessor accessor,
                                              @PathVariable("eventId") Long eventId,
                                              @RequestBody EventManagerRequestDto eventManagerRequestDto){
        eventService.registerManager(accessor, eventId, eventManagerRequestDto);
        return BaseResponseDto.ofSuccess(REGISTER_EVENT_MANAGER_SUCCESS);
    }

}
