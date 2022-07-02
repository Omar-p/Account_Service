package account.repositories;

import account.exceptions.DuplicateEntryInPaymentException;
import account.model.Payment;
import account.model.PaymentView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class PaymentJdbcRepository implements PaymentRepository {

  private final Logger LOGGER = LoggerFactory.getLogger(PaymentJdbcRepository.class);

  private final JdbcTemplate jdbcTemplate;
  private UserDataRepository userDataRepository;

  public PaymentJdbcRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  final String insert = "insert into payment(employee_email, period, salary)" +
      " values ('%s', '%s', %d)";


  final String findByEmail = "select period, salary from payment where employee_email = '%s' order by period desc";
  final String findByEmailAndPeriod = "select salary from payment " +
      "where employee_email = '%s' and period = '%s'";


  @Override
  public void save(Payment payment) {
    try {
      this.jdbcTemplate
          .execute(String.format(insert, payment.getEmployeeEmail(), payment.getPeriod(), payment.getSalary()));
    } catch (Exception e)  {
      throw new DuplicateEntryInPaymentException();
    }

  }

  @Override
  @Transactional
  public void saveAll(List<Payment> payments) {
    payments.forEach(this::save);
  }

  @Override
  public PaymentView findByEmployeeEmailAndPeriod(String email, String period) {
    final SqlRowSet nameAndLastname = nameAndLastnameByEmail(email);
    LOGGER.info("{}", String.format(findByEmailAndPeriod, email, period));
    final int salary = this.jdbcTemplate
        .queryForObject(String.format(findByEmailAndPeriod, email, period), Integer.class);


    return new PaymentView(nameAndLastname.getString("name"),
        nameAndLastname.getString("lastname"),
        period,
        salary);
  }

  @Override
  public List<PaymentView> findAllByEmployeeEmail(String email) {
    final SqlRowSet nameAndLastname = nameAndLastnameByEmail(email);
    final String name = nameAndLastname.getString("name");
    final String lastname = nameAndLastname.getString("lastname");
    final List<PaymentView> payments = this.jdbcTemplate
        .query(String.format(findByEmail, email), new BeanPropertyRowMapper<>(PaymentView.class));

    payments.forEach(p -> {
      p.setName(name);
      p.setLastname(lastname);
    });

    return payments;
  }

  @Override
  public void update(Payment payment) {
    final String updateSalary = "update payment " +
        "set salary=" + payment.getSalary() + " " +
        "where employee_email='"+ payment.getEmployeeEmail() + "' and period='" + payment.getPeriod() + "'";
    final int i = this.jdbcTemplate.update(updateSalary);
    LOGGER.info("salary update {}", i);
  }

  private SqlRowSet nameAndLastnameByEmail(String email) {
    return this.userDataRepository.nameAndLastnameByEmail(email);
  }

  @Autowired
  @Lazy
  public void setUserDataRepository(UserDataRepository userDataRepository) {
    this.userDataRepository = userDataRepository;
  }
}
