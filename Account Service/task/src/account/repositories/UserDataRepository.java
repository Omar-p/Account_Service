package account.repositories;

import account.model.UserData;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDataRepository {
  int insertUserData(UserData userData);

  UserData findByEmail(String email);

  SqlRowSet nameAndLastnameByEmail(String email);

  boolean isAdminPersisted();

  List<UserData> findAll();

  void setFailedLoginAttemptToZero(String email);

  void increaseFailedLoginAttempts(String email);

  int failedLoginAttempts(String email);

  List<String> findAllFailed();

}
