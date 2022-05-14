package account.model;

import account.validation.CorporateEmail;

import javax.validation.constraints.NotNull;

public class AdminOperation {
  @CorporateEmail
  private String user;
  @NotNull
  private String role;
  @NotNull
  private String operation;

  public AdminOperation() {
  }

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public String getOperation() {
    return operation;
  }

  public void setOperation(String operation) {
    this.operation = operation;
  }

  @Override
  public String toString() {
    return "AdminOperation{" +
        "user='" + user + '\'' +
        ", role='" + role + '\'' +
        ", operation='" + operation + '\'' +
        '}';
  }
}
