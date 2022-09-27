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
 * Defines a field as read only.
 *
 * @author Pierre Adam
 * @since 20.06.23
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Constraint(validatedBy = ReadOnlyValidator.class)
@Repeatable(ReadOnly.List.class)
@Form.Display(name = "constraint.readOnly")
public @interface ReadOnly {
    String message() default ReadOnlyValidator.message;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Defines several {@code @ReadOnly} annotations on the same element.
     */
    @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
    @Retention(RUNTIME)
    public @interface List {
        ReadOnly[] value();
    }
}
