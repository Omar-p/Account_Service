package account.web;

import account.exceptions.ExceptionRepresentation;
import account.model.Payment;
import account.services.AccountantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import java.util.List;

@Validated
@RestController
@RequestMapping("api/acct")
public class AccountantController {

  private final AccountantService accountantService;


  public AccountantController(AccountantService accountantService) {
    this.accountantService = accountantService;
  }

  @PostMapping("payments")
  public ResponseEntity<?> addPayments(@RequestBody List<@Valid Payment> payments) {
    return ResponseEntity.ok(this.accountantService.addPayments(payments));
  }


  @PutMapping("payments")
  public ResponseEntity<?> updatePayment(@RequestBody @Valid Payment payment) {
    return ResponseEntity.ok(this.accountantService.updatePayment(payment));
  }


  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<?> handleException(RuntimeException ex, HttpServletRequest request) {
    return ResponseEntity.badRequest().body(new ExceptionRepresentation(
        HttpStatus.BAD_REQUEST.value(),
        "Bad Request",
        ex.getMessage(),
        request.getServletPath()
    ));
  }
}
