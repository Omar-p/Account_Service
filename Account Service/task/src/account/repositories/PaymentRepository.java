package account.repositories;

import account.model.Payment;
import account.model.PaymentView;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository {
  void save(Payment payment);

  void saveAll(List<Payment> payments);

  PaymentView findByEmployeeEmailAndPeriod(String email, String period);

  List<PaymentView> findAllByEmployeeEmail(String email);

  void update(Payment payment);


}
