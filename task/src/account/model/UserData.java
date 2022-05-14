package account.model;

import account.security.ApplicationUserRole;

import java.util.ArrayList;
import java.util.List;

public class UserData {
  private int id;
  private String name;
  private String lastname;
  private String email;

  private List<ApplicationUserRole> roles = new ArrayList<>();

  public UserData() {
  }

  public UserData(String name, String lastname, String email) {
    this.name = name;
    this.lastname = lastname;
    this.email = email;
  }

  public UserData(int id, String name, String lastname, String email) {
    this.id = id;
    this.name = name;
    this.lastname = lastname;
    this.email = email;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getLastname() {
    return lastname;
  }

  public void setLastname(String lastname) {
    this.lastname = lastname;
  }

  public String getEmail() {
    return email.toLowerCase();
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public List<ApplicationUserRole> getRoles() {
    return roles;
  }

  public void setRoles(List<ApplicationUserRole> roles) {
    this.roles = roles;
  }

  public void addRole(ApplicationUserRole role) {
    this.getRoles().add(role);
  }

  @Override
  public String toString() {
    return "UserData{" +
        ", name='" + name + '\'' +
        ", lastname='" + lastname + '\'' +
        ", email='" + email + '\'' +
        ", roles=" + roles +
        '}';
  }
}
