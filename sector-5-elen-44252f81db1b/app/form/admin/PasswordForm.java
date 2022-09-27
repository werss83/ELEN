package form.admin;

import form.AForm;
import models.account.AccountModel;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;

import javax.validation.groups.Default;
import java.util.ArrayList;
import java.util.List;

/**
 * RoleForm.
 *
 * @author Pierre Adam
 * @since 20.07.04
 */
@Constraints.Validate(groups = {Default.class})
public class PasswordForm extends AForm implements Constraints.Validatable<List<ValidationError>> {

    /**
     * The Roles.
     */
    @Constraints.Required(groups = {Default.class, Groups.NoConfirmation.class})
    @Constraints.MinLength(value = 8, groups = {Default.class, Groups.NoConfirmation.class})
    @Constraints.MaxLength(value = 128, groups = {Default.class, Groups.NoConfirmation.class})
    protected String password;

    /**
     * The Password confirmation.
     */
    @Constraints.Required(groups = Default.class)
    @Constraints.MinLength(value = 8, groups = {Default.class})
    @Constraints.MaxLength(value = 128, groups = {Default.class})
    protected String passwordConfirmation;

    /**
     * Instantiates a new Role form.
     */
    public PasswordForm() {
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
     * Update account.
     *
     * @param account the account
     */
    public void updateAccount(final AccountModel account) {
        account.setPassword(this.password);
    }

    @Override
    public List<ValidationError> validate() {
        final List<ValidationError> errors = new ArrayList<>();

        // Check password
        if (this.password != null && !this.password.equals(this.passwordConfirmation)) {
            errors.add(new ValidationError("passwordConfirmation", "error.passwordConfirmation.doNotMatch"));
        }

        return errors.isEmpty() ? null : errors;
    }

    /**
     * The type Groups.
     */
    public static class Groups {
        /**
         * The interface No confirmation.
         */
        public interface NoConfirmation {
        }
    }
}
