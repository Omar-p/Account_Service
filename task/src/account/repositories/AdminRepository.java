package account.repositories;


import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository {
  void deleteUser(String email);

  void removeRole(UserDetails userDetails, String role);

  void grantRole(String email, String role);

  void lockUser(String userEmail);

  void unlockUser(String userEmail);
}
