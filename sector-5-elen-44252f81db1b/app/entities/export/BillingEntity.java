package entities.export;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import models.account.AccountModel;
import models.parent.ParentModel;
import models.sitter.SitterModel;
import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.io.Serializable;

/**
 * ParentHoursEntity.
 *
 * @author Pierre Adam
 * @since 20.08.25
 */
@JsonPropertyOrder({
    "lastName", "firstName", "address", "phone", "startDate", "endDate",
    "standardHours", "nightHours", "bankHolidayHours",
    "sitter", "sitterSocialSecurityNumber"
})
public class BillingEntity implements Serializable {

    /**
     * The Last name.
     */
    private final String lastName;

    /**
     * The First name.
     */
    private final String firstName;

    /**
     * The Email.
     */
    private final String address;

    /**
     * The Email.
     */
    private final String phone;

    /**
     * The First name.
     */
    private final String sitter;

    /**
     * The First name.
     */
    private final String sitterSocialSecurityNumber;

    /**
     * The Start date.
     */
    private final DateTime startDate;

    /**
     * The End date.
     */
    private final DateTime endDate;

    /**
     * The Standard hours.
     */
    private Duration standardHours;

    /**
     * The Night hours.
     */
    private Duration nightHours;

    /**
     * The Bank holiday hours.
     */
    private Duration bankHolidayHours;

    /**
     * Instantiates a new parent hours entity.
     *
     * @param parent    the parent
     * @param sitter    the sitter
     * @param startDate the start date
     * @param endDate   the end date
     */
    public BillingEntity(final ParentModel parent, final SitterModel sitter, final DateTime startDate, final DateTime endDate) {
        final AccountModel account = parent.getAccount();
        this.lastName = account.getLastName();
        this.firstName = account.getFirstName();
        this.phone = account.getPhoneNumber();
        this.address = String.format("%s%s - %s %s", account.getAddress(),
            (account.getAdditionalAddress() == null || account.getAdditionalAddress().isEmpty()) ? "" : " " + account.getAdditionalAddress(),
            account.getZipCode(), account.getCity());
        this.sitter = sitter.getAccount().getFullName();
        this.sitterSocialSecurityNumber = sitter.getSitterCharacteristic().getSocialSecurityNumber();
        this.standardHours = new Duration(0);
        this.nightHours = new Duration(0);
        this.bankHolidayHours = new Duration(0);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * To hour fraction string.
     *
     * @param duration the duration
     * @return the string
     */
    private String toHourFraction(final Duration duration) {
        return String.format("%.1f", (double) duration.getMillis() / (1000 * 3600));
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
     * Gets first name.
     *
     * @return the first name
     */
    public String getFirstName() {
        return this.firstName;
    }

    /**
     * Gets phone.
     *
     * @return the phone
     */
    public String getPhone() {
        return this.phone;
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
     * Gets start date.
     *
     * @return the start date
     */
    public String getStartDate() {
        return this.startDate.toString("dd-MM-YYYY HH:mm");
    }

    /**
     * Gets end date.
     *
     * @return the end date
     */
    public String getEndDate() {
        return this.endDate.toString("dd-MM-YYYY HH:mm");
    }

    /**
     * Gets standard hours.
     *
     * @return the standard hours
     */
    public String getStandardHours() {
        return this.toHourFraction(this.standardHours);
    }

    /**
     * Add standard hours.
     *
     * @param hours the hours
     */
    public void addStandardHours(final Duration hours) {
        this.standardHours = this.standardHours.plus(hours);
    }

    /**
     * Gets night hours.
     *
     * @return the night hours
     */
    public String getNightHours() {
        return this.toHourFraction(this.nightHours);
    }

    /**
     * Add night hours.
     *
     * @param hours the hours
     */
    public void addNightHours(final Duration hours) {
        this.nightHours = this.nightHours.plus(hours);
    }

    /**
     * Gets bank holiday hours.
     *
     * @return the bank holiday hours
     */
    public String getBankHolidayHours() {
        return this.toHourFraction(this.bankHolidayHours);
    }

    /**
     * Add bank holiday hours.
     *
     * @param hours the hours
     */
    public void addBankHolidayHours(final Duration hours) {
        this.bankHolidayHours = this.bankHolidayHours.plus(hours);
    }

    /**
     * Gets sitter.
     *
     * @return the sitter
     */
    public String getSitter() {
        return this.sitter;
    }

    /**
     * Gets sitter social security number.
     *
     * @return the sitter social security number
     */
    public String getSitterSocialSecurityNumber() {
        return this.sitterSocialSecurityNumber;
    }
}
