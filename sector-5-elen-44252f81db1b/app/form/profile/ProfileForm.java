package form.profile;

import behavior.constraints.NoAutoComplete;
import behavior.constraints.ReadOnly;
import form.AForm;
import io.ebean.ExpressionList;
import models.account.AccountModel;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;
import play.libs.typedmap.TypedKey;

import javax.validation.groups.Default;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * ProfileForm.
 *
 * @author Pierre Adam
 * @since 20.06.26
 */
@Constraints.ValidateWithPayload(groups = {Default.class, ProfileForm.Groups.EmailValidated.class})
public class ProfileForm extends AForm implements Constraints.ValidatableWithPayload<List<ValidationError>> {

    /**
     * The Email.
     */
    @Constraints.Required(groups = {Default.class})
    @Constraints.Email(groups = {Default.class})
    @Constraints.MaxLength(value = 172, groups = {Default.class})
    @ReadOnly(groups = Groups.EmailValidated.class)
    protected String email;

    /**
     * The First name.
     */
    @Constraints.Required(groups = {Default.class, Groups.EmailValidated.class})
    @Constraints.MinLength(value = 2, groups = {Default.class, Groups.EmailValidated.class})
    @Constraints.MaxLength(value = 128, groups = {Default.class, Groups.EmailValidated.class})
    protected String firstName;

    /**
     * The Last name.
     */
    @Constraints.Required(groups = {Default.class, Groups.EmailValidated.class})
    @Constraints.MinLength(value = 2, groups = {Default.class, Groups.EmailValidated.class})
    @Constraints.MaxLength(value = 128, groups = {Default.class, Groups.EmailValidated.class})
    protected String lastName;

    /**
     * The Address.
     */
    @Constraints.Required(groups = {Default.class, Groups.EmailValidated.class})
    @Constraints.MinLength(value = 2, groups = {Default.class, Groups.EmailValidated.class})
    @Constraints.MaxLength(value = 512, groups = {Default.class, Groups.EmailValidated.class})
    @NoAutoComplete(groups = {Default.class, Groups.EmailValidated.class})
    protected String address;

    /**
     * The Postal code.
     */
    @Constraints.Required(groups = {Default.class, Groups.EmailValidated.class})
    @Constraints.MinLength(value = 2, groups = {Default.class, Groups.EmailValidated.class})
    @Constraints.MaxLength(value = 16, groups = {Default.class, Groups.EmailValidated.class})
    @NoAutoComplete(groups = {Default.class, Groups.EmailValidated.class})
    protected String postalCode;

    /**
     * The City.
     */
    @Constraints.Required(groups = {Default.class, Groups.EmailValidated.class})
    @Constraints.MinLength(value = 2, groups = {Default.class, Groups.EmailValidated.class})
    @Constraints.MaxLength(value = 256, groups = {Default.class, Groups.EmailValidated.class})
    @NoAutoComplete(groups = {Default.class, Groups.EmailValidated.class})
    protected String city;

    /**
     * The Additional address.
     */
    @Constraints.MaxLength(value = 256, groups = {Default.class, Groups.EmailValidated.class})
    @NoAutoComplete(groups = {Default.class, Groups.EmailValidated.class})
    protected String additionalAddress;

    /**
     * The Phone number.
     */
    @Constraints.Required(groups = {Default.class, Groups.EmailValidated.class})
    @Constraints.Pattern(message = "error.phone", value = "^(\\+33)[0-9]{9}$", groups = {Default.class, Groups.EmailValidated.class})
    protected String phoneNumber;

    /**
     * Instantiates a new Profile form.
     */
    public ProfileForm() {
    }

    /**
     * Instantiates a new Profile form.
     *
     * @param account the account
     */
    public ProfileForm(final AccountModel account) {
        this.firstName = account.getFirstName();
        this.lastName = account.getLastName();
        this.email = account.getEmail();
        this.phoneNumber = account.getPhoneNumber();
        this.address = account.getAddress();
        this.postalCode = account.getZipCode();
        this.city = account.getCity();
        this.additionalAddress = account.getAdditionalAddress();
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
     * Gets postal code.
     *
     * @return the postal code
     */
    public String getPostalCode() {
        return this.postalCode;
    }

    /**
     * Sets postal code.
     *
     * @param postalCode the postal code
     */
    public void setPostalCode(final String postalCode) {
        this.postalCode = postalCode.trim();
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
            .replace(" ", "");
        if (phoneNumber.startsWith("0")) {
            this.phoneNumber = "+33" + this.phoneNumber.substring(1);
        }
    }

    @Override
    public List<ValidationError> validate(final Constraints.ValidationPayload payload) {
        final List<ValidationError> errors = new ArrayList<>();

        final Optional<AccountModel> optionalAccount = payload.getAttrs().getOptional(Attrs.ACCOUNT);

        if (this.email != null) {
            // Check that the email is not already used.
            final ExpressionList<AccountModel> query = AccountModel.find
                .query()
                .where()
                .eq("email", this.email);

            optionalAccount.ifPresent(accountModel -> query.ne("id", accountModel.getId()));

            final AccountModel searchedAccount = query.findOne();
            if (searchedAccount != null) {
                errors.add(new ValidationError("email", "error.email.alreadyUsed"));
            }
        }

        return errors.isEmpty() ? null : errors;
    }

    /**
     * Update the account according to the form.
     *
     * @param account the account
     */
    public void updateAccount(final AccountModel account) {
        account.setFirstName(this.firstName);
        account.setLastName(this.lastName);
        if (this.email != null && !this.email.isEmpty()) {
            account.setEmail(this.email);
        }
        account.setAddress(this.address);
        account.setZipCode(this.postalCode);
        account.setCity(this.city);
        account.setAdditionalAddress(this.additionalAddress);
        account.setPhoneNumber(this.phoneNumber);
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
            return account.getEmailVerified() ? EmailValidated.class : Default.class;
        }

        /**
         * The interface that is given to the form when the Email is already validated.
         */
        public interface EmailValidated {
        }
    }

    /**
     * The type Attrs.
     */
    public static class Attrs {
        /**
         * The account that is changing it's profile.
         */
        public static final TypedKey<AccountModel> ACCOUNT = TypedKey.create("ACCOUNT");
    }
}
