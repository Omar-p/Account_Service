package account.web;

import account.model.EventEntity;
import account.services.AuditingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/security/events")
public class AuditingController {

  private final AuditingService auditingService;

  public AuditingController(AuditingService auditingService) {
    this.auditingService = auditingService;
  }

  @GetMapping
  public List<EventEntity> retrieveEvents() {
    return this.auditingService.findAll();
  }
}
