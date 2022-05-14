package account.repositories;

import account.model.EventEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditingRepository {
  List<EventEntity> findAll();

  void addEvent(EventEntity e);
}
