package account.services;

import account.event.SecurityEventPublisher;
import account.exceptions.PasswordConstraintsViolationException;
import account.exceptions.PasswordEqualToOldPasswordException;
import account.exceptions.UserExistException;
import account.model.Event;
import account.model.EventEntity;
import account.model.User;
import account.model.UserData;
import account.repositories.UserDataRepository;
import account.security.UserSecurity;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

import static account.security.ApplicationUserRole.ADMINISTRATOR;
import static account.security.ApplicationUserRole.USER;

@Service
public class RegistrationService {

  private final Logger LOGGER = LoggerFactory.getLogger(RegistrationService.class);
  private final JdbcUserDetailsManager jdbcUserDetailsManager;
  private final UserDataRepository userDataRepository;
  private final PasswordEncoder passwordEncoder;

  private final SecurityEventPublisher securityEventPublisher;
  private final PasswordService passwordService = new PasswordService();
  @Lazy
  public RegistrationService(JdbcUserDetailsManager jdbcUserDetailsManager, UserDataRepository userDataRepository, PasswordEncoder passwordEncoder, SecurityEventPublisher securityEventPublisher) {
    this.jdbcUserDetailsManager = jdbcUserDetailsManager;
    this.userDataRepository = userDataRepository;
    this.passwordEncoder = passwordEncoder;
    this.securityEventPublisher = securityEventPublisher;
  }

  public UserData signup(User user) {

    if (this.jdbcUserDetailsManager.userExists(user.getEmail()))
      throw new UserExistException();

    final String hashedPassword = passwordService.hashPassword(user.getPassword());
    user.setPassword(hashedPassword);

    if (this.userDataRepository.isAdminPersisted()) {
      user.getRoles().add(USER);
    } else {
      user.getRoles().add(ADMINISTRATOR);
    }
    LOGGER.info("{}", user);
    UserSecurity us = new UserSecurity(user);
    this.jdbcUserDetailsManager.createUser(us);

    this.userDataRepository
        .insertUserData(new UserData(user.getName(), user.getLastname(), user.getEmail()));


    this.securityEventPublisher.publishSecurityEvent(new EventEntity(Event.CREATE_USER,
        "Anonymous",
        user.getEmail().toLowerCase(),
        "/api/auth/signup"));

    return this.userDataRepository.findByEmail(user.getEmail());
  }

  public Map<String, String> changePass(Map<String, String> body) {
    final Authentication authentication = SecurityContextHolder.getContext()
        .getAuthentication();
    final String email = authentication.getName();
    LOGGER.info("email from authentication {}", email);
    final UserDetails userDetails = this.jdbcUserDetailsManager.loadUserByUsername(email);
    LOGGER.info("email from authentication {}", userDetails.getUsername());
    final String oldPassword = userDetails.getPassword();
    final String newPassword = body.get("new_password");
    if (this.passwordEncoder.matches(newPassword, oldPassword)) {
      throw new PasswordEqualToOldPasswordException("The passwords must be different!");
    }

    String newHashedPassword = this.passwordService.hashPassword(newPassword);
    this.jdbcUserDetailsManager.changePassword(oldPassword, newHashedPassword);

    this.securityEventPublisher.publishSecurityEvent(new EventEntity(Event.CHANGE_PASSWORD,
        email.toLowerCase(),
        email.toLowerCase(),
        "/api/auth/changepass"));
    return Map.of("email", userDetails.getUsername().toLowerCase(), "status", "The password has been updated successfully");

  }


  final class PasswordService {
    private final Set<String> BREACHED_PASSWORDS =
        Sets.newHashSet("PasswordForJanuary", "PasswordForFebruary", "PasswordForMarch", "PasswordForApril",
            "PasswordForMay", "PasswordForJune", "PasswordForJuly", "PasswordForAugust",
            "PasswordForSeptember", "PasswordForOctober", "PasswordForNovember", "PasswordForDecember");
    private final static int MINIMUM_LENGTH = 12;
    private final static String LENGTH_VIOLATION_MESSAGE = "Password length must be 12 chars minimum!";
    private final static String BREACHED_PASSWORD_MESSAGE = "The password is in the hacker's database!";


    private void checkPasswordConstraints(String password) {
      if (password.length() < MINIMUM_LENGTH)
        throw new PasswordConstraintsViolationException(LENGTH_VIOLATION_MESSAGE);

      if (BREACHED_PASSWORDS.contains(password))
        throw new PasswordConstraintsViolationException(BREACHED_PASSWORD_MESSAGE);

    }

    private String hashPassword(String password) {
      checkPasswordConstraints(password);

      return passwordEncoder.encode(password);
    }
  }
}
