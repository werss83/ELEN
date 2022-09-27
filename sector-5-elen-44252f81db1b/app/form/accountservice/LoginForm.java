package form.accountservice;

import form.AForm;
import models.account.AccountModel;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;

import java.util.ArrayList;
import java.util.List;

/**
 * RegistrationForm.
 *
 * @author Pierre Adam
 * @since 20.06.21
 */
@Constraints.Validate
public class LoginForm extends AForm implements Constraints.Validatable<List<ValidationError>> {

    /**
     * The Email.
     */
    @Constraints.Required
    @Constraints.Email
    @Constraints.MaxLength(172)
    protected String email;

    /**
     * The Password.
     */
    @Constraints.Required
    protected String password;

    /**
     * The Account.
     */
    private AccountModel resolvedAccount;

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
        this.email = email.trim();
    }

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
     * Gets resolved account.
     *
     * @return the resolved account
     */
    public AccountModel getResolvedAccount() {
        return this.resolvedAccount;
    }

    @Override
    public List<ValidationError> validate() {
        final List<ValidationError> errors = new ArrayList<>();

        // Retrieve the account from the email.
        this.resolvedAccount = AccountModel.find
            .query()
            .where()
            .eq("email", this.email)
            .findOne();
        if (this.resolvedAccount != null) {
            if (this.resolvedAccount.checkPassword(this.password)) {
                // The account exists and the password match. Returning null for no errors.
                return null;
            }
        }

        errors.add(new ValidationError("password", "error.login.badCredentials"));
        return errors;
    }
}
