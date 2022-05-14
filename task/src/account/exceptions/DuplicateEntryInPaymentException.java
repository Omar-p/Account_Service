package account.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Duplicated entry in payment list")
public class DuplicateEntryInPaymentException extends RuntimeException {
}
