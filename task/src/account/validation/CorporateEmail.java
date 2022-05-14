package account.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = CorporateEmailValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD } )
@Retention(RetentionPolicy.RUNTIME)
public @interface CorporateEmail {

  String message() default "Our service accept only corporate emails that end with @acme.com";

  Class<?>[] groups() default { };

  Class<? extends Payload>[] payload() default { };
}
