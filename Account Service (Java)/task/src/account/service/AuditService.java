package account.service;

import account.entity.Event;
import account.repository.EventRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditService {
    private final EventRepository eventRepository;

    AuditService(EventRepository eventRepository){
        this.eventRepository = eventRepository;
    }

    public ResponseEntity getAllEvents(){
        List<Event> events = eventRepository.findAllByOrderById();
        return ResponseEntity.status(HttpStatus.OK)
                .body(events);
    }
}
