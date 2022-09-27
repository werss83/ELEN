package form.accountservice;

import form.AForm;
import play.data.validation.Constraints;

/**
 * ForgotPasswordForm.
 *
 * @author Pierre Adam
 * @since 20.09.06
 */
public class ForgotPasswordForm extends AForm {

    /**
     * The Email.
     */
    @Constraints.Required
    @Constraints.Email
    @Constraints.MaxLength(172)
    protected String email;

    /**
     * Gets email.
     *
     * @return the email
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * Sets email.
     *
     * @param email the email
     */
    public void setEmail(final String email) {
        this.email = email;
    }
}
