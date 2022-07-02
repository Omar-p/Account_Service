package account.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "period: Wrong date!")
public class PeriodConstraintsViolationException extends RuntimeException {
  public PeriodConstraintsViolationException(String message) {
    super(message);
  }
}
