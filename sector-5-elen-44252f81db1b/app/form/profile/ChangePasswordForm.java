package form.profile;

import behavior.constraints.ReadOnly;
import form.AForm;
import models.account.AccountModel;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;
import play.libs.typedmap.TypedKey;

import javax.validation.groups.Default;
import java.util.ArrayList;
import java.util.List;

/**
 * ChangePasswordForm.
 *
 * @author Pierre Adam
 * @since 20.07.12
 */
@Constraints.ValidateWithPayload
public class ChangePasswordForm extends AForm implements Constraints.ValidatableWithPayload<List<ValidationError>> {

    /**
     * The Current password.
     */
    @Constraints.Required(groups = {Default.class})
    @Constraints.MinLength(value = 8, groups = {Default.class})
    @Constraints.MaxLength(value = 128, groups = {Default.class})
    @ReadOnly(groups = {Groups.NoCurrentPassword.class})
    protected String currentPassword;

    /**
     * The New password.
     */
    @Constraints.Required(groups = {Default.class, Groups.NoCurrentPassword.class})
    @Constraints.MinLength(value = 8, groups = {Default.class, Groups.NoCurrentPassword.class})
    @Constraints.MaxLength(value = 128, groups = {Default.class, Groups.NoCurrentPassword.class})
    protected String newPassword;

    /**
     * The New password confirmation.
     */
    @Constraints.Required(groups = {Default.class, Groups.NoCurrentPassword.class})
    @Constraints.MinLength(value = 8, groups = {Default.class, Groups.NoCurrentPassword.class})
    @Constraints.MaxLength(value = 128, groups = {Default.class, Groups.NoCurrentPassword.class})
    protected String newPasswordConfirmation;

    /**
     * Gets current password.
     *
     * @return the current password
     */
    public String getCurrentPassword() {
        return this.currentPassword;
    }

    /**
     * Sets current password.
     *
     * @param currentPassword the current password
     */
    public void setCurrentPassword(final String currentPassword) {
        this.currentPassword = currentPassword;
    }

    /**
     * Gets new password.
     *
     * @return the new password
     */
    public String getNewPassword() {
        return this.newPassword;
    }

    /**
     * Sets new password.
     *
     * @param newPassword the new password
     */
    public void setNewPassword(final String newPassword) {
        this.newPassword = newPassword;
    }

    /**
     * Gets new password confirmation.
     *
     * @return the new password confirmation
     */
    public String getNewPasswordConfirmation() {
        return this.newPasswordConfirmation;
    }

    /**
     * Sets new password confirmation.
     *
     * @param newPasswordConfirmation the new password confirmation
     */
    public void setNewPasswordConfirmation(final String newPasswordConfirmation) {
        this.newPasswordConfirmation = newPasswordConfirmation;
    }

    @Override
    public List<ValidationError> validate(final Constraints.ValidationPayload payload) {
        final List<ValidationError> errors = new ArrayList<>();

        final AccountModel account = payload.getAttrs().get(Attrs.ACCOUNT);

        // Check password
        if (this.currentPassword != null && !account.checkPassword(this.currentPassword)) {
            errors.add(new ValidationError("currentPassword", "error.invalidPassword"));
        }

        if (this.newPassword != null && !this.newPassword.equals(this.newPasswordConfirmation)) {
            errors.add(new ValidationError("newPasswordConfirmation", "error.passwordConfirmation.doNotMatch"));
        }

        return errors.isEmpty() ? null : errors;
    }

    /**
     * The type Groups.
     */
    public static class Groups {
        /**
         * Smart group class.
         *
         * @param account the account
         * @return the class
         */
        public static Class<?> smartGroup(final AccountModel account) {
            return account.hasPassword() ? Default.class : NoCurrentPassword.class;
        }

        /**
         * The account doesn't have a password currently.
         */
        public interface NoCurrentPassword {
        }
    }

    /**
     * The type Attrs.
     */
    public static class Attrs {
        /**
         * The constant ACCOUNT.
         */
        public static final TypedKey<AccountModel> ACCOUNT = TypedKey.create("ACCOUNT");
    }
}
