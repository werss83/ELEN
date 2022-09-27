package actions;

import play.mvc.With;
import security.Role;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * SessionRequired.
 *
 * @author Pierre Adam
 * @since 20.06.04
 */
@With(SessionRequiredImpl.class)
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SessionRequired {
    Role value() default Role.NONE;
}
