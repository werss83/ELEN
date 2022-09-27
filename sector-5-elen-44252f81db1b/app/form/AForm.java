package form;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.data.validation.ValidationError;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * AForm.
 *
 * @author Pierre Adam
 * @since 20.06.04
 */
public class AForm {

    /**
     * The Logger.
     */
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Easy list validate list.
     *
     * @param logic the logic
     * @return the list
     */
    protected List<ValidationError> easyListValidate(final Consumer<List<ValidationError>> logic) {
        final List<ValidationError> errors = new ArrayList<>();

        logic.accept(errors);

        return errors.isEmpty() ? null : errors;
    }
}
