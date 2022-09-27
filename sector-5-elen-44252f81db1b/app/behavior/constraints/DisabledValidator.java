package behavior.constraints;

import play.data.validation.Constraints;

import javax.validation.ConstraintValidator;

import static play.libs.F.Tuple;

/**
 * DisabledValidator.
 *
 * @author Pierre Adam
 * @since 20.06.23
 */
public class DisabledValidator extends Constraints.Validator<Object>
    implements ConstraintValidator<Disabled, Object> {

    public static final String message = "error";

    @Override
    public void initialize(final Disabled constraintAnnotation) {
    }

    @Override
    public boolean isValid(final Object object) {
        return true;
    }

    @Override
    public Tuple<String, Object[]> getErrorMessageKey() {
        return Tuple(message, new Object[]{});
    }
}
