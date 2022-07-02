package account.event;

import account.model.Event;
import account.model.EventEntity;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class SecurityEventPublisher {

  private final ApplicationEventPublisher applicationEventPublisher;

  public SecurityEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
    this.applicationEventPublisher = applicationEventPublisher;
  }

  public void publishSecurityEvent(final EventEntity event) {
    SecurityEvent securityEvent = new SecurityEvent(this, event);
    this.applicationEventPublisher.publishEvent(securityEvent);
  }

}
