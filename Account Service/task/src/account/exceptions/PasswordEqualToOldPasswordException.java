package account.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class PasswordEqualToOldPasswordException extends RuntimeException {
  public PasswordEqualToOldPasswordException(String message) {
    super(message);
  }
}
