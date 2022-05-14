package account.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class PasswordConstraintsViolationException extends RuntimeException {
  public PasswordConstraintsViolationException(String message) {
    super(message);
  }
}
