package account.security;

import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.collect.Sets;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

import static account.security.ApplicationUserAuthority.*;

public enum ApplicationUserRole {
  USER(Sets.newHashSet(PAYMENT_READ, PASSWORD_CHANGE)),
  ACCOUNTANT(Sets.newHashSet(PAYMENT_READ, PAYMENT_WRITE, PASSWORD_CHANGE)),
  ADMINISTRATOR(Sets.newHashSet(USER_READ, USER_WRITE, PASSWORD_CHANGE)),

  AUDITOR(Sets.newHashSet(EVENT_READ));

  private final Set<ApplicationUserAuthority> authorities;


  ApplicationUserRole(Set<ApplicationUserAuthority> authorities) {
    this.authorities = authorities;
  }

  public Set<ApplicationUserAuthority> getAuthorities() {
    return authorities;
  }

  public Set<? extends GrantedAuthority> getGrantedAuthorities() {
    final Set<SimpleGrantedAuthority> grantedAuthorities = getAuthorities().stream()
        .map(ApplicationUserAuthority::getAuthority)
        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toSet());

    grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));

    return grantedAuthorities;
  }

  @JsonValue
  public String getName() {
    return "ROLE_" + this.name();
  }
}
