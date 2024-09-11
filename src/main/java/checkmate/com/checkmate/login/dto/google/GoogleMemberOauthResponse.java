package checkmate.com.checkmate.login.dto.google;

import checkmate.com.checkmate.event.domain.Event;
import checkmate.com.checkmate.event.dto.EventDetailResponseDto;
import checkmate.com.checkmate.eventschedule.domain.EventSchedule;
import checkmate.com.checkmate.eventschedule.dto.EventScheduleResponseDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor(access = PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleMemberOauthResponse {

    public String id;
    public String name;
    public String email;
    public String accessToken;

    public static GoogleMemberOauthResponse of(GoogleMemberResponse googleMemberResponse, String accessToken) {
        return new GoogleMemberOauthResponse(
                googleMemberResponse.getId(),
                googleMemberResponse.getName(),
                googleMemberResponse.getEmail(),
                accessToken
        );
    }
}
