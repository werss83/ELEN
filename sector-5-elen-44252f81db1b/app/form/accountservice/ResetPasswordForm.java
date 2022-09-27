package form.accountservice;

import form.AForm;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;

import java.util.List;

/**
 * ResetPasswordForm.
 *
 * @author Pierre Adam
 * @since 20.09.06
 */
@Constraints.Validate
public class ResetPasswordForm extends AForm implements Constraints.Validatable<List<ValidationError>> {

    /**
     * The Password.
     */
    @Constraints.Required
    @Constraints.MinLength(value = 8)
    @Constraints.MaxLength(value = 128)
    protected String password;

    /**
     * The Password confirmation.
     */
    @Constraints.Required
    @Constraints.MinLength(value = 8)
    @Constraints.MaxLength(value = 128)
    protected String passwordConfirmation;

    /**
     * Gets password.
     *
     * @return the password
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * Sets password.
     *
     * @param password the password
     */
    public void setPassword(final String password) {
        this.password = password;
    }

    /**
     * Gets password confirmation.
     *
     * @return the password confirmation
     */
    public String getPasswordConfirmation() {
        return this.passwordConfirmation;
    }

    /**
     * Sets password confirmation.
     *
     * @param passwordConfirmation the password confirmation
     */
    public void setPasswordConfirmation(final String passwordConfirmation) {
        this.passwordConfirmation = passwordConfirmation;
    }

    @Override
    public List<ValidationError> validate() {
        return this.easyListValidate(errors -> {
            // Check password
            if (this.password != null && !this.password.equals(this.passwordConfirmation)) {
                errors.add(new ValidationError("passwordConfirmation", "error.passwordConfirmation.doNotMatch"));
            }
        });
    }
}
