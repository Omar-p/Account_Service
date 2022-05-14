package account.event;

import account.repositories.UserDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationSuccessListener
    implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

  private final Logger LOGGER = LoggerFactory.getLogger(AuthenticationFailureListener.class);


  private UserDataRepository userDataRepository;
  @Override
  public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
    String email = event.getAuthentication().getName().toLowerCase();
    LOGGER.info("succ {}", email);
   // this.userDataRepository.setFailedLoginAttemptToZero(email);
  }

  @Autowired
  public void setUserDataRepository(UserDataRepository userDataRepository) {
    this.userDataRepository = userDataRepository;
  }


}
