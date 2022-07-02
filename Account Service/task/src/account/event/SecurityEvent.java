package account.event;

import account.model.EventEntity;
import org.springframework.context.ApplicationEvent;

public class SecurityEvent extends ApplicationEvent {

  private final EventEntity eventEntity;
  /**
   * Create a new {@code ApplicationEvent}.
   *
   * @param source the object on which the event initially occurred or with
   *               which the event is associated (never {@code null})
   */
  public SecurityEvent(Object source, EventEntity eventEntity) {
    super(source);
    this.eventEntity = eventEntity;
  }

  public EventEntity getEventEntity() {
    return eventEntity;
  }
}
