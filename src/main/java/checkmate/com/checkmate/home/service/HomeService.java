package checkmate.com.checkmate.home.service;

import checkmate.com.checkmate.event.domain.repository.EventRepository;
import checkmate.com.checkmate.eventAttendance.domain.repository.EventAttendanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HomeService {

    @Autowired
    private final EventRepository eventRepository;
    @Autowired
    private final EventAttendanceRepository eventAttendanceRepository;

    public long getEventTimes() {
        return eventRepository.count();
    }

    public long geAttendanceTimes(){
        return eventAttendanceRepository.count();
    }
}
