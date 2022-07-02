package account.repositories;

import account.model.EventEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AuditingJdbcRepository implements AuditingRepository {

  private final Logger LOGGER = LoggerFactory.getLogger(AuditingJdbcRepository.class);
  private final JdbcTemplate jdbcTemplate;

  public AuditingJdbcRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public List<EventEntity> findAll() {
    return this.jdbcTemplate
        .query("select * from event order by id",
            new BeanPropertyRowMapper<>(EventEntity.class));
  }

  @Override
  public void addEvent(EventEntity e) {
    final int u = this.jdbcTemplate
        .update(String.format(
            "insert into event(date,\n" +
                "action,\n" +
                "subject,\n" +
                "object,\n" +
                "path) values('%s','%s','%s','%s','%s')",
            e.getDate().toString(), e.getAction().name(), e.getSubject(), e.getObject(), e.getPath()));
    LOGGER.info("21------ {} ----- {} ---------------------------------", u, e);
  }
}
