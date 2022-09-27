package behavior.constraints;

import play.data.validation.Constraints;
import play.libs.F;

import javax.validation.ConstraintValidator;

import static play.libs.F.Tuple;

/**
 * ReadOnlyValidator.
 *
 * @author Pierre Adam
 * @since 20.06.23
 */
public class ReadOnlyValidator extends Constraints.Validator<Object>
    implements ConstraintValidator<ReadOnly, Object> {

    public static final String message = "error";

    @Override
    public void initialize(final ReadOnly constraintAnnotation) {
    }

    @Override
    public boolean isValid(final Object object) {
        return true;
    }

    @Override
    public F.Tuple<String, Object[]> getErrorMessageKey() {
        return Tuple(message, new Object[]{});
    }
}
