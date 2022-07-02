package account.services;

import account.exceptions.PeriodConstraintsViolationException;
import account.model.PaymentView;
import account.repositories.PaymentRepository;
import account.repositories.UserDataRepository;
import account.validation.PeriodValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.text.DateFormatSymbols;
import java.util.List;

@Service
public class EmployService {

  private final Logger LOGGER = LoggerFactory.getLogger(EmployService.class);
  private final UserDataRepository userDataRepository;
  private final PaymentRepository paymentRepository;
  private final String SALARY_TEMPLATE = "%d dollar(s) %d cent(s)";
  private final String PERIOD_FORMAT = "%s-%s";
  private final String[] MONTHS = DateFormatSymbols.getInstance().getMonths();

  public EmployService(UserDataRepository userDataRepository, PaymentRepository paymentRepository) {
    this.userDataRepository = userDataRepository;
    this.paymentRepository = paymentRepository;
  }

  public List<PaymentView> getPayment(String period) {
    final String email = SecurityContextHolder.getContext()
        .getAuthentication()
        .getName();

    LOGGER.info("username : {}", email);

    if (period == null) {
      final List<PaymentView> paymentViews = findAllPayments(email);
      paymentViews.forEach(this::modifyView);
      return paymentViews;
    } else {
      return List.of(findPaymentByEmailAndPeriod(email, period));
    }
  }

  private List<PaymentView> findAllPayments(String email) {
    return this.paymentRepository.findAllByEmployeeEmail(email);
  }

  private PaymentView findPaymentByEmailAndPeriod(String email, String period) {
    PeriodValidator validator = new PeriodValidator();
    if (!validator.validatePeriod(period))
      throw new PeriodConstraintsViolationException("period: Wrong date!");
    final PaymentView payment = this.paymentRepository
        .findByEmployeeEmailAndPeriod(email, period);
    return this.modifyView(payment);
  }


  private PaymentView modifyView(PaymentView paymentView) {
    int salary = Integer.parseInt(paymentView.getSalary());
    int dollars = salary / 100;
    int cents = salary % 100;
    String[] monthAndYear = paymentView.getPeriod().split("-");
    final int month = Integer.parseInt(monthAndYear[0], 10);

    paymentView.setSalary(String.format(this.SALARY_TEMPLATE, dollars, cents));
    paymentView.setPeriod(String.format(this.PERIOD_FORMAT, MONTHS[month-1], monthAndYear[1]));

    return paymentView;
  }



}
