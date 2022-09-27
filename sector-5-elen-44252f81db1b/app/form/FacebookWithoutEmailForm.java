package form;

import play.data.validation.Constraints;
import play.data.validation.ValidationError;

import java.util.ArrayList;
import java.util.List;

/**
 * FacebookWithoutEmailForm.
 *
 * @author Pierre Adam
 * @since 20.06.04
 */
public class FacebookWithoutEmailForm extends AForm implements Constraints.Validatable<List<ValidationError>> {

    /**
     * The Email.
     */
    @Constraints.Email
    protected String email;

    /**
     * The Email confirmation.
     */
    @Constraints.Email
    protected String emailConfirmation;

    /**
     * Sets email.
     *
     * @param email the email
     */
    public void setEmail(final String email) {
        this.email = email.trim();
    }

    /**
     * Sets email confirmation.
     *
     * @param emailConfirmation the email confirmation
     */
    public void setEmailConfirmation(final String emailConfirmation) {
        this.emailConfirmation = emailConfirmation.trim();
    }

    /**
     * Gets email.
     *
     * @return the email
     */
    public String getEmail() {
        return this.email;
    }

    @Override
    public List<ValidationError> validate() {
        final List<ValidationError> errors = new ArrayList<>();

        if (!this.email.equals(this.emailConfirmation)) {
            errors.add(new ValidationError("emailConfirmation", "Does not match the email"));
        }

        return errors.isEmpty() ? null : errors;
    }
}
