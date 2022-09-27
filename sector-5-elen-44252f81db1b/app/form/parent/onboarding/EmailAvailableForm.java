package form.parent.onboarding;

import form.AForm;
import models.account.AccountModel;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;

import java.util.ArrayList;
import java.util.List;

/**
 * EmailAvailableForm.
 *
 * @author Lucas Stadelmann
 * @since 20.09.07
 */
@Constraints.Validate
public class EmailAvailableForm extends AForm implements Constraints.Validatable<List<ValidationError>> {

    /**
     * The Email.
     */
    @Constraints.Required(message = "")
    @Constraints.Email
    private String email;

    /**
     * Instantiates a new Email available form.
     */
    public EmailAvailableForm() {
    }

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

    @Override
    public List<ValidationError> validate() {
        final List<ValidationError> errors = new ArrayList<>();

        AccountModel.find.query()
            .where()
            .eq("email", this.email)
            .setMaxRows(1)
            .findOneOrEmpty()
            .ifPresent(account -> errors.add(new ValidationError("email", "error.email.alreadyUsed")));

        return errors.isEmpty() ? null : errors;
    }
}
