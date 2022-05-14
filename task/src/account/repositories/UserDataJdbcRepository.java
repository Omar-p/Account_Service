package account.repositories;

import account.model.UserData;
import account.security.ApplicationUserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class UserDataJdbcRepository implements UserDataRepository {

  private final JdbcTemplate jdbcTemplate;

  private final Logger LOGGER = LoggerFactory.getLogger(UserDataJdbcRepository.class);

  public UserDataJdbcRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public int insertUserData(UserData userData) {
    String insertOne = "insert into user_data(name, lastname, email) values ('%s', '%s', '%s')";

    return jdbcTemplate
        .update(String.format(insertOne, userData.getName(), userData.getLastname(), userData.getEmail()));

  }

  @Override
  public UserData findByEmail(String email) {
    String findBy = "select id, name, lastname, email from user_data where email = '%s'";
    final UserData userData = this.jdbcTemplate
        .query(String.format(findBy, email), new BeanPropertyRowMapper<>(UserData.class)).get(0);
    loadUserRoles(userData);
    return userData;
  }

  public SqlRowSet nameAndLastnameByEmail(String email) {
    final String getNameAndLastnameByEmail = "select name, lastname from user_data where email = '%s'";
    final SqlRowSet nameAndLastname = this.jdbcTemplate
        .queryForRowSet(String.format(getNameAndLastnameByEmail, email));
    System.out.println(nameAndLastname.next());

    return nameAndLastname;
  }

  @Override
  public boolean isAdminPersisted() {
    final int rows = this.jdbcTemplate
        .queryForObject("select count(id) from user_data", Integer.class);
    return rows >= 1;
  }

  public List<UserData> findAll() {
    String all = "select id, name, lastname, email from user_data order by id";
    List<UserData> users = jdbcTemplate.query(all, new BeanPropertyRowMapper<>(UserData.class));
    loadUsersRoles(users);
    return users;
  }



  private void loadUsersRoles(final List<UserData> users) {
    for (var u : users) {
      loadUserRoles(u);
    }
  }

  private void loadUserRoles(final UserData userData) {
    final SqlRowSet sqlRowSet =
        this.jdbcTemplate
            .queryForRowSet("select authority from authorities where username = '" + userData.getEmail() +
                "' and authority like 'ROLE_%'");
    List<String> roles = new ArrayList<>();
    while (sqlRowSet.next()) {
      roles.add(sqlRowSet.getString("authority").substring(5));
    }
    List<ApplicationUserRole> appRoles = roles.stream()
        .map(ApplicationUserRole::valueOf)
        .collect(Collectors.toList());
    userData.setRoles(appRoles);
  }

  @Override
  public void setFailedLoginAttemptToZero(String email) {
    this.jdbcTemplate.update(String.format("delete from failed_login_attempts where email='%s'", email));
  }

  @Override
  @Transactional
  public void increaseFailedLoginAttempts(String email) {
    if (failedLoginAttempts(email) == 0) {
      this.jdbcTemplate
          .update(String.format("insert into failed_login_attempts(email, attempts) values ('%s', 1)", email));
    } else {
      final int i = this.jdbcTemplate.update(
          String.format("update failed_login_attempts " +
              "set attempts = attempts + 1 " +
              "where email = '%s'", email
          )
      );
      LOGGER.info("ROW UPDATED {}", i);
    }

  }

  @Override
  public int failedLoginAttempts(String email) {
    final List<Integer> attempts = this.jdbcTemplate
        .queryForList(String.format("select attempts from failed_login_attempts where email='%s'", email), Integer.class);
    LOGGER.info("{}", findAllFailed());
    LOGGER.info("ret size  = {}", attempts.size());
    return attempts.size() != 0 ? attempts.get(0) : 0;
  }

  @Override
  public List<String> findAllFailed() {
    return this.jdbcTemplate.queryForList("select concat(attempts, ', ',email) from failed_login_attempts", String.class);
  }


}
