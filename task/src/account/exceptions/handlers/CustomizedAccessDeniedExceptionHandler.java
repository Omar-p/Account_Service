package account.exceptions.handlers;

import account.event.SecurityEventPublisher;
import account.exceptions.ExceptionRepresentation;
import account.model.Event;
import account.model.EventEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomizedAccessDeniedExceptionHandler implements AccessDeniedHandler {

  private SecurityEventPublisher securityEventPublisher;


  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
    ObjectMapper mapper = new ObjectMapper();
    var resBody =
        mapper.writeValueAsString(new ExceptionRepresentation(HttpStatus.FORBIDDEN.value(),
            "Forbidden" ,
            "Access Denied!",
            request.getServletPath())
        );

    response.getWriter().write(resBody);
    response.setStatus(HttpStatus.FORBIDDEN.value());
    publishAccessDeniedEvent(request);
  }

  private void publishAccessDeniedEvent(HttpServletRequest request) {
    String email = SecurityContextHolder.getContext().getAuthentication().getName().toLowerCase();
    this.securityEventPublisher.publishSecurityEvent(new EventEntity(
        Event.ACCESS_DENIED,
        email,
        request.getServletPath(),
        request.getServletPath()
    ));
  }

  @Autowired
  public void setSecurityEventPublisher(SecurityEventPublisher securityEventPublisher) {
    this.securityEventPublisher = securityEventPublisher;
  }
}
