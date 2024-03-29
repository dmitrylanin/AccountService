package account.controller;

import account.service.AuditService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuditController {
    private final AuditService auditService;

    AuditController(AuditService auditService){
        this.auditService = auditService;
    }

    @GetMapping("/api/security/events/")
    public ResponseEntity<Object> signup(){
        return auditService.getAllEvents();
    }
}