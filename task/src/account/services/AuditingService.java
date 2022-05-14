package account.services;

import account.event.SecurityEvent;
import account.model.EventEntity;
import account.repositories.AuditingRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditingService {
  private final AuditingRepository auditingRepository;

  public AuditingService(AuditingRepository auditingRepository) {
    this.auditingRepository = auditingRepository;
  }

  public List<EventEntity> findAll() {
    return this.auditingRepository.findAll();
  }

  public void addEvent(SecurityEvent e) {
    this.auditingRepository.addEvent(e.getEventEntity());
  }
}
