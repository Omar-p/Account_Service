package account.model;

import account.security.ApplicationUserRole;
import account.validation.CorporateEmail;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@JsonFilter("view")
public class User {

  @NotBlank
  private String name;

  @NotBlank
  private String lastname;


  @CorporateEmail
  private String email;

  @NotBlank
  private String password;

  @JsonIgnore
  private List <ApplicationUserRole> roles = new ArrayList<>();

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
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
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
    return "User{" +
        "name='" + name + '\'' +
        ", lastName='" + lastname + '\'' +
        ", email='" + email + '\'' +
        ", password='" + password + '\'' +
        '}';
  }


}
