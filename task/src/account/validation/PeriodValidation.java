package account.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Constraint(validatedBy = PeriodValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER } )
@Retention(RetentionPolicy.RUNTIME)
public @interface PeriodValidation {

  String message() default "months or year are invalid";

  Class<?>[] groups() default { };

  Class<? extends Payload>[] payload() default { };
}
