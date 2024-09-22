package checkmate.com.checkmate.global.domain;

import checkmate.com.checkmate.event.dto.EventDetailResponseDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

import static lombok.AccessLevel.PRIVATE;

@Getter
@RequiredArgsConstructor(access=PRIVATE)
public class CsvResultDto {
    private final Map<Integer, Integer> studentTimeMap;
    private final List<Map<String, String>> failedTimeMap;

    public static CsvResultDto of(Map<Integer, Integer> studentTimeMap, List<Map<String, String>> failedTimeMap) {
        return new CsvResultDto(studentTimeMap, failedTimeMap);
    }

}

