package account.validation;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PeriodValidator implements ConstraintValidator<PeriodValidation, String> {
  @Override
  public boolean isValid(String date, ConstraintValidatorContext context) {
    if (date == null) return false;
    return validatePeriod(date);
  }

  public boolean validatePeriod(String date) {
    final String[] monthAndYear = date.split("-");
    if (monthAndYear.length != 2 || monthAndYear[0].isEmpty() || monthAndYear[1].isEmpty())
      return false;

    int month = Integer.parseInt(monthAndYear[0], 10);

    if (month < 0 || month > 12)
      return false;

    int year = Integer.parseInt(monthAndYear[1], 10);
    if (year < 0)
      return false;

    return true;
  }
}
