package models.options;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * OptionDefaultValue.
 *
 * @author Lucas Stadelmann
 * @since 20.06.08
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Option {

    /**
     * The default value.
     *
     * @return The default value
     */
    String defaultValue() default "";

    /**
     * The class of the default value.
     *
     * @return The class of the default value
     */
    Class<?>[] type() default {String.class};
}
