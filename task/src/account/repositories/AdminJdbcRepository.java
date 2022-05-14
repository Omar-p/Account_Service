package account.repositories;

import account.security.ApplicationUserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class AdminJdbcRepository implements AdminRepository {

  private final Logger LOGGER = LoggerFactory.getLogger(AdminJdbcRepository.class);
  private final JdbcTemplate jdbcTemplate;
  private final JdbcUserDetailsManager jdbcUserDetailsManager;

  private final UserDataJdbcRepository userDataJdbcRepository;

  public AdminJdbcRepository(JdbcTemplate jdbcTemplate, JdbcUserDetailsManager jdbcUserDetailsManager, UserDataJdbcRepository userDataJdbcRepository) {
    this.jdbcTemplate = jdbcTemplate;
    this.jdbcUserDetailsManager = jdbcUserDetailsManager;
    this.userDataJdbcRepository = userDataJdbcRepository;
  }

  @Override
  public void deleteUser(String email) {
    int updatedRows = this.jdbcTemplate.update(String.format("delete from users where username = '%s'", email));
    LOGGER.info("payment  {}", updatedRows);
//    if (updatedRows == 0) {
//      updatedRows = this.jdbcTemplate.update(String.format("delete from user_data where email = '%s'", email));
//      LOGGER.info("user_data  {}", updatedRows);
//
//    }

  }

  @Override
  public void removeRole(UserDetails userDetails, String role) {
    final Set<String> updatedRoles = userDetails.getAuthorities()
        .stream()
        .map(GrantedAuthority::getAuthority)
        .filter(authority -> authority.startsWith("ROLE_"))
        .map(authority -> authority.substring(5))
        .filter(r -> !r.equals(role))
        .collect(Collectors.toSet());

    Set<? extends GrantedAuthority> newGrantedAuthorities = updatedRoles
        .stream()
        .map(ApplicationUserRole::valueOf)
        .map(ApplicationUserRole::getGrantedAuthorities)
        .flatMap(Set::stream)
        .collect(Collectors.toSet());

    final UserDetails updatedUser = User.builder()
        .username(userDetails.getUsername())
        .password(userDetails.getPassword())
        .accountExpired(!userDetails.isAccountNonExpired())
        .accountLocked(!userDetails.isAccountNonLocked())
        .credentialsExpired(!userDetails.isCredentialsNonExpired())
        .disabled(!userDetails.isEnabled())
        .authorities(newGrantedAuthorities)
        .build();

    LOGGER.info("auths before: {}", userDetails.getAuthorities());
    LOGGER.info("auths after: {}", updatedUser.getAuthorities());

    this.jdbcUserDetailsManager.updateUser(updatedUser);
  }

  @Override
  public void grantRole(String email, String role) {

  }

  @Override
  public void lockUser(String userEmail) {
    this.jdbcTemplate.update(
        "update users " +
            "set" +
            " enabled=false " +
            "where " +
            "username='" + userEmail + "'"
    );
  }

  @Override
  public void unlockUser(String userEmail) {
    this.jdbcTemplate.update(
        "update users " +
            "set" +
            " enabled=true " +
            "where " +
            "username='" + userEmail + "'"
    );
    this.userDataJdbcRepository.setFailedLoginAttemptToZero(userEmail);
  }
}
