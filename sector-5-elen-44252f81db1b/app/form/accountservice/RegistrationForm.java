package form.accountservice;

import behavior.constraints.NoAutoComplete;
import behavior.constraints.NoRender;
import behavior.constraints.ReadOnly;
import form.AForm;
import models.account.AccountModel;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;
import play.libs.typedmap.TypedKey;

import javax.validation.groups.Default;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * RegistrationForm.
 *
 * @author Pierre Adam
 * @since 20.06.21
 */
@Constraints.ValidateWithPayload(groups = {Default.class, RegistrationForm.Groups.SocialForm.class, RegistrationForm.Groups.SocialFormWithEmail.class, RegistrationForm.Groups.SocialFormWithoutAddressAndNumber.class})
public class RegistrationForm extends AForm implements Constraints.ValidatableWithPayload<List<ValidationError>> {

    /**
     * The Email.
     */
    @Constraints.Required(groups = {Default.class, Groups.SocialFormWithEmail.class, Groups.SocialFormWithoutAddressAndNumber.class})
    @Constraints.Email(groups = {Default.class, Groups.SocialFormWithEmail.class, Groups.SocialFormWithoutAddressAndNumber.class})
    @Constraints.MaxLength(value = 172, groups = {Default.class, Groups.SocialFormWithEmail.class, Groups.SocialFormWithoutAddressAndNumber.class})
    @ReadOnly(groups = {Groups.SocialForm.class})
    protected String email;

    /**
     * The First name.
     */
    @Constraints.Required(groups = {Default.class, Groups.SocialForm.class, Groups.SocialFormWithEmail.class, Groups.SocialFormWithoutAddressAndNumber.class})
    @Constraints.MinLength(value = 2, groups = {Default.class, Groups.SocialForm.class, Groups.SocialFormWithEmail.class, Groups.SocialFormWithoutAddressAndNumber.class})
    @Constraints.MaxLength(value = 128, groups = {Default.class, Groups.SocialForm.class, Groups.SocialFormWithEmail.class, Groups.SocialFormWithoutAddressAndNumber.class})
    protected String firstName;

    /**
     * The Last name.
     */
    @Constraints.Required(groups = {Default.class, Groups.SocialForm.class, Groups.SocialFormWithEmail.class, Groups.SocialFormWithoutAddressAndNumber.class})
    @Constraints.MinLength(value = 2, groups = {Default.class, Groups.SocialForm.class, Groups.SocialFormWithEmail.class, Groups.SocialFormWithoutAddressAndNumber.class})
    @Constraints.MaxLength(value = 128, groups = {Default.class, Groups.SocialForm.class, Groups.SocialFormWithEmail.class, Groups.SocialFormWithoutAddressAndNumber.class})
    protected String lastName;

    /**
     * The Address.
     */
    @Constraints.Required(groups = {Default.class, Groups.SocialForm.class, Groups.SocialFormWithEmail.class})
    @Constraints.MinLength(value = 2, groups = {Default.class, Groups.SocialForm.class, Groups.SocialFormWithEmail.class})
    @Constraints.MaxLength(value = 512, groups = {Default.class, Groups.SocialForm.class, Groups.SocialFormWithEmail.class})
    @NoAutoComplete(groups = {Default.class, Groups.SocialForm.class, Groups.SocialFormWithEmail.class})
    protected String address;

    /**
     * The ZIP code.
     */
    @Constraints.Required(groups = {Default.class, Groups.SocialForm.class, Groups.SocialFormWithEmail.class})
    @Constraints.MinLength(value = 2, groups = {Default.class, Groups.SocialForm.class, Groups.SocialFormWithEmail.class})
    @Constraints.MaxLength(value = 16, groups = {Default.class, Groups.SocialForm.class, Groups.SocialFormWithEmail.class})
    @NoAutoComplete(groups = {Default.class, Groups.SocialForm.class, Groups.SocialFormWithEmail.class})
    protected String zipCode;

    /**
     * The City.
     */
    @Constraints.Required(groups = {Default.class, Groups.SocialForm.class, Groups.SocialFormWithEmail.class})
    @Constraints.MinLength(value = 2, groups = {Default.class, Groups.SocialForm.class, Groups.SocialFormWithEmail.class})
    @Constraints.MaxLength(value = 256, groups = {Default.class, Groups.SocialForm.class, Groups.SocialFormWithEmail.class})
    @NoAutoComplete(groups = {Default.class, Groups.SocialForm.class, Groups.SocialFormWithEmail.class})
    protected String city;

    /**
     * The Additional address.
     */
    @Constraints.MaxLength(value = 256, groups = {Default.class, Groups.SocialForm.class, Groups.SocialFormWithEmail.class})
    @NoAutoComplete(groups = {Default.class, Groups.SocialForm.class, Groups.SocialFormWithEmail.class})
    protected String additionalAddress;

    /**
     * The Phone number.
     */
    @Constraints.Required(groups = {Default.class, Groups.SocialForm.class, Groups.SocialFormWithEmail.class})
    @Constraints.Pattern(message = "error.phone", value = "^(?:\\+33|0)\\s*[1-9](?:[\\s.-]*\\d{2}){4}$", groups = {Default.class, Groups.SocialForm.class, Groups.SocialFormWithEmail.class})
    protected String phoneNumber;

    /**
     * The Password.
     */
    @Constraints.Required(groups = {Default.class})
    @Constraints.MinLength(value = 8, groups = {Default.class})
    @Constraints.MaxLength(value = 128, groups = {Default.class})
    @NoRender(groups = {Groups.SocialForm.class, Groups.SocialFormWithEmail.class})
    protected String password;

    /**
     * The Password confirmation.
     */
    @Constraints.Required(groups = {Default.class})
    @Constraints.MinLength(value = 8, groups = {Default.class})
    @Constraints.MaxLength(value = 128, groups = {Default.class})
    @NoRender(groups = {Groups.SocialForm.class, Groups.SocialFormWithEmail.class})
    protected String passwordConfirmation;

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
     * Gets first name.
     *
     * @return the first name
     */
    public String getFirstName() {
        return this.firstName;
    }

    /**
     * Sets first name.
     *
     * @param firstName the first name
     */
    public void setFirstName(final String firstName) {
        this.firstName = firstName.trim();
    }

    /**
     * Gets last name.
     *
     * @return the last name
     */
    public String getLastName() {
        return this.lastName;
    }

    /**
     * Sets last name.
     *
     * @param lastName the last name
     */
    public void setLastName(final String lastName) {
        this.lastName = lastName.trim();
    }

    /**
     * Gets address.
     *
     * @return the address
     */
    public String getAddress() {
        return this.address;
    }

    /**
     * Sets address.
     *
     * @param address the address
     */
    public void setAddress(final String address) {
        this.address = address.trim();
    }

    /**
     * Gets zip code.
     *
     * @return the zip code
     */
    public String getZipCode() {
        return this.zipCode;
    }

    /**
     * Sets zip code.
     *
     * @param zipCode the zip code
     */
    public void setZipCode(final String zipCode) {
        this.zipCode = zipCode;
    }

    /**
     * Gets city.
     *
     * @return the city
     */
    public String getCity() {
        return this.city;
    }

    /**
     * Sets city.
     *
     * @param city the city
     */
    public void setCity(final String city) {
        this.city = city.trim();
    }

    /**
     * Gets additional address.
     *
     * @return the additional address
     */
    public String getAdditionalAddress() {
        return this.additionalAddress;
    }

    /**
     * Sets additional address.
     *
     * @param additionalAddress the additional address
     */
    public void setAdditionalAddress(final String additionalAddress) {
        this.additionalAddress = additionalAddress.trim();
    }

    /**
     * Gets phone number.
     *
     * @return the phone number
     */
    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    /**
     * Sets phone number.
     *
     * @param phoneNumber the phone number
     */
    public void setPhoneNumber(final String phoneNumber) {
        // Only handle french phone for now.
        this.phoneNumber = phoneNumber.trim()
            .replace(".", "")
            .replace("-", "")
            .replace(" ", "");
        if (phoneNumber.startsWith("0")) {
            this.phoneNumber = "+33" + this.phoneNumber.substring(1);
        }
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
    public List<ValidationError> validate(final Constraints.ValidationPayload payload) {
        final List<ValidationError> errors = new ArrayList<>();

        final Optional<String> forcedEmail = payload.getAttrs().getOptional(Attrs.EMAIL);
        final String email = forcedEmail.orElseGet(this::getEmail);

        // Check password
        if (this.password != null && !this.password.equals(this.passwordConfirmation)) {
            errors.add(new ValidationError("passwordConfirmation", "error.passwordConfirmation.doNotMatch"));
        }

        // Check that the email is not already used.
        final AccountModel account = AccountModel.find
            .query()
            .where()
            .eq("email", email)
            .findOne();
        if (account != null) {
            errors.add(new ValidationError("email", "error.email.alreadyUsed"));
        }

        return errors.isEmpty() ? null : errors;
    }

    /**
     * Build account model from the form.
     *
     * @return the account model
     */
    public AccountModel buildAccount() {
        return this.buildAccount(this.getEmail());
    }

    /**
     * Build account model from the form.
     *
     * @param email the email
     * @return the account model
     */
    public AccountModel buildAccount(final String email) {
        final AccountModel account = new AccountModel(this.getFirstName(), this.getLastName(), email, this.getAddress(),
            this.getCity(), this.getZipCode(), this.getPhoneNumber());
        account.setAdditionalAddress(this.additionalAddress == null ? "" : this.additionalAddress);
        if (this.getPassword() != null) {
            account.setPassword(this.getPassword());
        }
        return account;
    }

    /**
     * The type Groups.
     */
    public static class Groups {
        /**
         * The interface Social form.
         */
        public interface SocialForm {
        }

        /**
         * The interface Social form with email.
         */
        public interface SocialFormWithEmail {
        }

        /**
         * The interface Social form without address and number.
         */
        public interface SocialFormWithoutAddressAndNumber {
        }
    }

    /**
     * The type Attrs.
     */
    public static class Attrs {
        /**
         * The constant EMAIL.
         */
        public static final TypedKey<String> EMAIL = TypedKey.create("EMAIL");
    }
}
