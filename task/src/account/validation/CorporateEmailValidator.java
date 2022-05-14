package account.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CorporateEmailValidator implements ConstraintValidator<CorporateEmail, String> {

  private final String CORPORATE_DOMAIN = "acme.com";
  @Override
  public boolean isValid(String email, ConstraintValidatorContext context) {
    if (email == null) return false;
    String[] e = email.toLowerCase().split("@");
    return e.length == 2 && !e[0].isEmpty() && CORPORATE_DOMAIN.equals(e[1]);
  }
}
