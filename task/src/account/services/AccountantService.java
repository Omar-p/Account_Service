package account.services;

import account.model.Payment;
import account.repositories.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AccountantService {

  private final PaymentRepository paymentRepository;

  private final Logger LOGGER = LoggerFactory.getLogger(AccountantService.class);

  public AccountantService(PaymentRepository paymentRepository) {
    this.paymentRepository = paymentRepository;
  }

  public Map<String, String> addPayments(List<Payment> payments) {
    LOGGER.info("{}", payments);
    this.paymentRepository.saveAll(payments);
    return  Map.of("status", "Added successfully!");
  }

  public Map<String, String> updatePayment(Payment payment) {
    LOGGER.info("{}", payment);
    this.paymentRepository.update(payment);
    return  Map.of("status", "Updated successfully!");
  }
}
