package account.event;

import account.services.AuditingService;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class SecurityEventListener implements ApplicationListener<SecurityEvent> {
  private final AuditingService auditingService;

  public SecurityEventListener(AuditingService auditingService) {
    this.auditingService = auditingService;
  }

  @Override
  public void onApplicationEvent(SecurityEvent event) {
    this.auditingService.addEvent(event);
  }
}
