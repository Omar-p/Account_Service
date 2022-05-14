package account.web;

import account.model.PaymentView;
import account.services.EmployService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/empl/")
public class EmployController {
  private final Logger LOGGER = LoggerFactory.getLogger(EmployController.class);
  private final EmployService employService;

  public EmployController(EmployService employService) {
    this.employService = employService;
  }

  @GetMapping("payment")
  public ResponseEntity<?> getPayment(@RequestParam(value = "period", required = false) String period) {
    final List<PaymentView> payments = this.employService.getPayment(period);
    LOGGER.info("{}", payments);
    LOGGER.info("p {}", period);
    if (period != null) {
      if (payments.size() != 0)
        return ResponseEntity.ok(payments.get(0));

      return ResponseEntity.ok(null);
    } else {
      return ResponseEntity.ok(payments);
    }

  }
}
