package account.service;

import account.entity.Action;
import account.entity.Event;
import account.repository.EventRepository;
import org.springframework.stereotype.Service;

@Service
public class AuditActionsWriter {
    private final EventRepository eventRepository;

    public AuditActionsWriter(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public void auditActionNotice(Action action,
                                  String subject,
                                  String object,
                                  String path){


        if (subject == null){
            subject = "Anonymous";
        }
        eventRepository.save(new Event(action.getAction(), subject, object, path));
    }
}
