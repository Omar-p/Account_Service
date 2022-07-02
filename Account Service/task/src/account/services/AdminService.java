package account.services;

import account.event.SecurityEventPublisher;
import account.exceptions.IllegalOperationException;
import account.exceptions.RoleNotFoundException;
import account.exceptions.UserEmailNotFoundException;
import account.model.AdminOperation;
import account.model.Event;
import account.model.EventEntity;
import account.model.UserData;
import account.repositories.AdminRepository;
import account.repositories.UserDataRepository;
import account.security.ApplicationUserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminService {

  private final Logger LOGGER = LoggerFactory.getLogger(AdminService.class);
  private final UserDataRepository userDataRepository;
  private final AdminRepository adminRepository;

  private final SecurityEventPublisher securityEventPublisher;
  private final JdbcUserDetailsManager jdbcUserDetailsManager;

  private final SecurityEventPublisher eventPublisher;
  private final Set<String> ADMIN_ROLES = Set.of("ADMINISTRATOR");
  private final Set<String> BUSINESS_ROLES = Set.of("ACCOUNTANT", "USER", "AUDITOR");

  public AdminService(UserDataRepository userDataRepository, AdminRepository adminRepository, SecurityEventPublisher securityEventPublisher, JdbcUserDetailsManager jdbcUserDetailsManager, SecurityEventPublisher eventPublisher) {
    this.userDataRepository = userDataRepository;
    this.adminRepository = adminRepository;
    this.securityEventPublisher = securityEventPublisher;
    this.jdbcUserDetailsManager = jdbcUserDetailsManager;
    this.eventPublisher = eventPublisher;
  }

  public UserData apply(AdminOperation adminOperation) {

    if (!this.jdbcUserDetailsManager.userExists(adminOperation.getUser())) {

      throw new UserEmailNotFoundException();
    }

    if (!ADMIN_ROLES.contains(adminOperation.getRole()) && !BUSINESS_ROLES.contains(adminOperation.getRole()))
      throw new RoleNotFoundException();

    final String GRANT = "GRANT";
    final String REMOVE = "REMOVE";
    if (GRANT.equals(adminOperation.getOperation())) {
      return grantRole(adminOperation);
    } else if (REMOVE.equals(adminOperation.getOperation())) {
      if (ADMIN_ROLES.contains(adminOperation.getRole()))
        throw new IllegalOperationException("Can't remove ADMINISTRATOR role!");
      return removeRole(adminOperation);
    } else {
      throw new IllegalOperationException("Not Supported");
    }


  }

  private UserData removeRole(AdminOperation adminOperation) {
    final UserDetails userDetails = this.jdbcUserDetailsManager.loadUserByUsername(adminOperation.getUser());
    final String role = adminOperation.getRole();
    final boolean isRoleExist = userDetails.getAuthorities()
        .stream()
        .map(GrantedAuthority::getAuthority)
        .filter(authority -> authority.contains(role))
        .count() == 1;

    if (!isRoleExist) {
      throw new IllegalOperationException("The user does not have a role!");
    }

    final boolean isUserHasOneRole = userDetails.getAuthorities()
        .stream()
        .map(GrantedAuthority::getAuthority)
        .filter(authority -> authority.contains("ROLE"))
        .count() == 1;

    if (isUserHasOneRole) {
      throw new IllegalOperationException("The user must have at least one role!");
    }

    this.adminRepository.removeRole(userDetails, role);
    String adminEmail = SecurityContextHolder.getContext().getAuthentication().getName().toLowerCase();
    this.eventPublisher.publishSecurityEvent(
        new EventEntity(
            Event.REMOVE_ROLE,
            adminEmail,
            "Remove role " + role + " from " + adminOperation.getUser().toLowerCase(),
            "/api/admin/user/role"

        )
    );

    return this.userDataRepository.findByEmail(adminOperation.getUser());
  }

  private UserData grantRole(AdminOperation adminOperation) {
    final UserDetails userDetails = this.jdbcUserDetailsManager.loadUserByUsername(adminOperation.getUser());

    final Set<String> currentUserRoles = userDetails.getAuthorities()
        .stream()
        .map(GrantedAuthority::getAuthority)
        .filter(authority -> authority.startsWith("ROLE_"))
        .map(role -> role.substring(5))
        .collect(Collectors.toSet());

    boolean isNewRoleBusinessRole = BUSINESS_ROLES.contains(adminOperation.getRole());
    boolean isUserInBusinessGroup = currentUserRoles.stream()
        .map(BUSINESS_ROLES::contains)
        .reduce(true, (r1, r2) -> r1 && r2);

    if ((isNewRoleBusinessRole && isUserInBusinessGroup) || (!isNewRoleBusinessRole && !isUserInBusinessGroup)) {
      ApplicationUserRole newRole = ApplicationUserRole.valueOf(adminOperation.getRole());
      Set<GrantedAuthority> newAuthorities = new HashSet<>(userDetails.getAuthorities());
      newAuthorities.addAll(newRole.getGrantedAuthorities());
      LOGGER.info("========");
      LOGGER.info("{}", newAuthorities);

      final UserDetails updatedUser = User.builder()
          .username(userDetails.getUsername())
          .password(userDetails.getPassword())
          .accountExpired(!userDetails.isAccountNonExpired())
          .accountLocked(!userDetails.isAccountNonLocked())
          .credentialsExpired(!userDetails.isCredentialsNonExpired())
          .disabled(!userDetails.isEnabled())
          .authorities(newAuthorities)
          .build();
      this.jdbcUserDetailsManager.updateUser(updatedUser);
      String adminEmail = SecurityContextHolder.getContext().getAuthentication().getName().toLowerCase();
      this.eventPublisher.publishSecurityEvent(
          new EventEntity(
              Event.GRANT_ROLE,
              adminEmail,
              "Grant role " + adminOperation.getRole() + " to " + adminOperation.getUser().toLowerCase(),
              "/api/admin/user/role"

          )
      );
      return this.userDataRepository.findByEmail(adminOperation.getUser());
    } else {
      throw new IllegalOperationException("The user cannot combine administrative and business roles!");
    }


  }

  public Map<String, String> deleteUser(String email) {
    email = email.toLowerCase();
    if (!this.jdbcUserDetailsManager.userExists(email)) {
      throw new UserEmailNotFoundException();
    }
    LOGGER.info("DELETE USER {}", email);

    final Collection<? extends GrantedAuthority> authorities = this.jdbcUserDetailsManager
        .loadUserByUsername(email)
        .getAuthorities();

    if (authorities.contains(new SimpleGrantedAuthority("ROLE_ADMINISTRATOR"))) {
      throw new IllegalOperationException("Can't remove ADMINISTRATOR role!");
    }

    this.adminRepository.deleteUser(email);
    String adminEmail = SecurityContextHolder.getContext().getAuthentication().getName().toLowerCase();
    this.eventPublisher.publishSecurityEvent(
        new EventEntity(
            Event.DELETE_USER,
            adminEmail,
            email.toLowerCase(),
            "/api/admin/user"

        )
    );

    return Map.of("user", email, "status", "Deleted successfully!");
  }

  public List<UserData> findAllUsers() {
    return this.userDataRepository.findAll();
  }

  public Map<String, String> applyOperation(AdminOperation operation) {
    if (!this.jdbcUserDetailsManager.userExists(operation.getUser())) {
      throw new UserEmailNotFoundException();
    }

    final String userEmail = operation.getUser().toLowerCase();
    final String adminEmail = SecurityContextHolder.getContext()
        .getAuthentication()
        .getName()
        .toLowerCase();

    if(adminEmail.equals(userEmail)) {
      throw new IllegalOperationException("Can't lock the ADMINISTRATOR!");
    }
    final String LOCK = "LOCK";
    final String UNLOCK = "UNLOCK";
    if (LOCK.equals(operation.getOperation())) {
      return lockUser(userEmail, adminEmail);
    } else if (UNLOCK.equals(operation.getOperation())) {
      return unlockUser(userEmail, adminEmail);
    } else {
      throw new IllegalOperationException("Not Supported");
    }
  }

  private Map<String, String> lockUser(String emailToBeLocked, String adminEmail) {
    this.adminRepository.lockUser(emailToBeLocked);
    this.securityEventPublisher.publishSecurityEvent(
        new EventEntity(
            Event.LOCK_USER,
            adminEmail,
            "Lock user " +emailToBeLocked,
            "/api/admin/user/access"
        )
    );
    return Map.of("status", "User " + emailToBeLocked + " locked!");
  }


  private Map<String, String> unlockUser(String emailToBeUnlocked, String adminEmail) {
    this.adminRepository.unlockUser(emailToBeUnlocked);
    this.securityEventPublisher.publishSecurityEvent(
        new EventEntity(
            Event.UNLOCK_USER,
            adminEmail,
            "Unlock user " + emailToBeUnlocked,
            "/api/admin/user/access"
        )
    );
    return Map.of("status", "User " + emailToBeUnlocked + " unlocked!");
  }
}
