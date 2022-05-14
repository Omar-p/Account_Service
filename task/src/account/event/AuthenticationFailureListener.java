package account.event;

import account.model.Event;
import account.model.EventEntity;
import account.repositories.AdminRepository;
import account.repositories.UserDataRepository;
import account.security.ApplicationUserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AuthenticationFailureListener
    implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

  @Autowired
  private HttpServletRequest request;

  @Autowired
  private UserDataRepository userDataRepository;
  @Autowired
  private JdbcUserDetailsManager jdbcUserDetailsManager;
  @Autowired
  private SecurityEventPublisher securityEventPublisher;
  @Autowired
  private AdminRepository adminRepository;

  private final Logger LOGGER = LoggerFactory.getLogger(AuthenticationFailureListener.class);


  @Override
  public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
    LOGGER.info("555555555555555555555555555555555555555555555555555");
    String email = event.getAuthentication().getName().toLowerCase();
    LOGGER.info("{},   {}", email, this.jdbcUserDetailsManager.userExists(email));
    if (this.jdbcUserDetailsManager.userExists(email)) {
      int failedLoginAttempts = this.userDataRepository.failedLoginAttempts(email);
      if (failedLoginAttempts <= 5) {
        publishLoginFailedEvent(request, email);
        this.userDataRepository.increaseFailedLoginAttempts(email);
        LOGGER.info("failed Time {} ", this.userDataRepository.failedLoginAttempts(email));
        LOGGER.info("{}", userDataRepository.findAllFailed());
        if (failedLoginAttempts == 5) {
          this.securityEventPublisher.publishSecurityEvent(
              new EventEntity(
                  Event.BRUTE_FORCE,
                  email,
                  request.getServletPath(),
                  request.getServletPath()
              )
          );
          this.adminRepository.lockUser(email);
          this.securityEventPublisher.publishSecurityEvent(
              new EventEntity(
                  Event.LOCK_USER,
                  email,
                  "Lock user " + email,
                  request.getServletPath()
              )
          );
        }
      } else {

      }
    } else {
      publishLoginFailedEvent(request, email);
    }
  }





  private void publishLoginFailedEvent(HttpServletRequest request, String email) {
    this.securityEventPublisher.publishSecurityEvent(
        new EventEntity(
            Event.LOGIN_FAILED,
            email,
            request.getServletPath(),
            request.getServletPath()
        )
    );
  }

  @Autowired
  public void setUserDataRepository(UserDataRepository userDataRepository) {
    this.userDataRepository = userDataRepository;
  }
}

// && !this.jdbcUserDetailsManager.loadUserByUsername(email).getAuthorities().contains(ApplicationUserRole.ADMINISTRATOR.name())