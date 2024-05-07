package checkmate.com.checkmate.event.controller;

import checkmate.com.checkmate.event.domain.Event;
import checkmate.com.checkmate.event.dto.EventDetailResponseDto;
import checkmate.com.checkmate.event.dto.EventRequestDto;
import checkmate.com.checkmate.event.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/event")
public class EventController {

    @Autowired
    private final EventService eventService;

    @ResponseBody
    @PostMapping(value="/register/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> postEvent(@PathVariable Long userId,
                                       @RequestPart(value="eventImage")MultipartFile eventImage,
                                       @RequestPart(value="event") EventRequestDto eventRequestDto) throws IOException {
        EventDetailResponseDto savedEvent = eventService.postEvent(eventImage, eventRequestDto, userId);
        return ResponseEntity.ok().body(savedEvent);
    }

/*    @GetMapping(value="/list/{userId}")
    public ResponseEntity<?> getEventList(){

    }

    @GetMapping(value="/detail/{userId/{eventId}")
    public ResponseEntity<?> getEventDetail(){

    }

    @PutMapping(value="modify/{userId}/{eventId}")
    public ResponseEntity<?> putEvent(){

            }

    @DeleteMapping(value="delete/{userId}/{eventId}")
    public ResponseEntity<?> deleteEvent(){

    }*/


}
