package checkmate.com.checkmate.event.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
public class EventManagerRequestDto {
    private final String managerName;
    private final String managerPhoneNumber;
    private final String managerEmail;
}
