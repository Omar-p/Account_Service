package account.security;

public enum ApplicationUserAuthority {
  PAYMENT_READ("payment:read"),
  PAYMENT_WRITE("payment:write"),
  USER_READ("user:write"),
  USER_WRITE("user:write"),
  PASSWORD_CHANGE("password:write"),

  EVENT_READ("event:read");

  private final String authority;

  ApplicationUserAuthority(String authority) {
    this.authority = authority;
  }

  public String getAuthority() {
    return authority;
  }
}
