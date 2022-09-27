package behavior.constraints;


import play.data.Form;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Defines a field that does not have auto-complete.
 *
 * @author Pierre Adam
 * @since 20.06.23
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Constraint(validatedBy = NoAutoCompleterValidator.class)
@Repeatable(NoAutoComplete.List.class)
@Form.Display(name = "constraint.noAutoComplete")
public @interface NoAutoComplete {
    String message() default NoAutoCompleterValidator.message;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Defines several {@code @ReadOnly} annotations on the same element.
     */
    @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
    @Retention(RUNTIME)
    public @interface List {
        NoAutoComplete[] value();
    }
}
