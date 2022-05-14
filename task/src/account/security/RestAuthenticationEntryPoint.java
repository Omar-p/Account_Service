package account.security;

import account.exceptions.ExceptionRepresentation;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    ObjectMapper objectMapper = new ObjectMapper();

    final String body = objectMapper.writeValueAsString(new ExceptionRepresentation(
        HttpServletResponse.SC_UNAUTHORIZED,
        "Unauthorized",
        (authException.getMessage().equals("User is disabled") ? "User account is locked" : authException.getMessage()),
        request.getServletPath()
    ));
    response.getWriter().write(body);
  }
}
