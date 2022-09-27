package entities.export;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.ebean.ExpressionList;
import io.ebean.QueryIterator;
import models.account.AccountModel;
import models.care.EffectiveCareModel;
import models.care.enumeration.EffectiveCareStatus;
import models.parent.ParentModel;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import utils.date.DateChecker;

import java.io.Serializable;

/**
 * ParentHoursEntity.
 *
 * @author Pierre Adam
 * @since 20.08.25
 */
@JsonPropertyOrder({
    "id", "lastName", "firstName", "email", "phone",
    "address", "totalHours", "standardHours",
    "nightHours", "bankHolidayHours", "bankHolidayNightHours"
})
public class ParentHoursEntity implements Serializable {

    /**
     * The Id.
     */
    private final Long id;

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
    private final String email;

    /**
     * The Email.
     */
    private final String phone;

    /**
     * The Email.
     */
    private final String address;

    /**
     * The Total hours.
     */
    private final String totalHours;

    /**
     * The Standard hours.
     */
    private final String standardHours;

    /**
     * The Night hours.
     */
    private final String nightHours;

    /**
     * The Bank holiday hours.
     */
    private final String bankHolidayHours;

    /**
     * The Bank holiday night hours.
     */
    private final String bankHolidayNightHours;

    /**
     * The Has hours.
     */
    private final boolean hasHours;

    /**
     * Instantiates a new parent hours entity.
     *
     * @param parent the parent
     * @param start  the start
     * @param end    the end
     */
    public ParentHoursEntity(final ParentModel parent, final DateTime start, final DateTime end) {
        final AccountModel account = parent.getAccount();

        this.id = account.getId();
        this.lastName = account.getLastName();
        this.firstName = account.getFirstName();
        this.email = account.getEmail();
        this.phone = account.getPhoneNumber();
        this.address = String.format("%s%s - %s %s", account.getAddress(),
            (account.getAdditionalAddress() == null || account.getAdditionalAddress().isEmpty()) ? "" : " " + account.getAdditionalAddress(),
            account.getZipCode(), account.getCity());

        ExpressionList<EffectiveCareModel> query = EffectiveCareModel.find.query().where()
            .eq("bookedCare.parent", parent)
            .eq("status", EffectiveCareStatus.PERFORMED);

        if (start != null) {
            query = query.ge("startDate", start.withMillisOfDay(0));
        }
        if (end != null) {
            query = query.lt("startDate", end.withMillisOfDay(0));
        }

        final QueryIterator<EffectiveCareModel> iterator = query.findIterate();
        final DateChecker.DateContext globalContext = new DateChecker.DateContext();

        while (iterator.hasNext()) {
            final EffectiveCareModel care = iterator.next();
            globalContext.addContext(DateChecker.getContext(care));
        }

        this.hasHours = globalContext.getTotal().getMillis() != 0;
        this.totalHours = this.toHourFraction(globalContext.getTotal());
        this.standardHours = this.toHourFraction(globalContext.getStandard());
        this.nightHours = this.toHourFraction(globalContext.getNight());
        this.bankHolidayHours = this.toHourFraction(globalContext.getBankHoliday());
        this.bankHolidayNightHours = this.toHourFraction(globalContext.getNightBankHoliday());
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
     * Gets id.
     *
     * @return the id
     */
    public Long getId() {
        return this.id;
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
     * Gets email.
     *
     * @return the email
     */
    public String getEmail() {
        return this.email;
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
     * Gets total hours.
     *
     * @return the total hours
     */
    public String getTotalHours() {
        return this.totalHours;
    }

    /**
     * Gets standard hours.
     *
     * @return the standard hours
     */
    public String getStandardHours() {
        return this.standardHours;
    }

    /**
     * Gets night hours.
     *
     * @return the night hours
     */
    public String getNightHours() {
        return this.nightHours;
    }

    /**
     * Gets bank holiday hours.
     *
     * @return the bank holiday hours
     */
    public String getBankHolidayHours() {
        return this.bankHolidayHours;
    }

    /**
     * Gets bank holiday night hours.
     *
     * @return the bank holiday night hours
     */
    public String getBankHolidayNightHours() {
        return this.bankHolidayNightHours;
    }

    /**
     * Is has hours boolean.
     *
     * @return the boolean
     */
    public boolean hasHours() {
        return this.hasHours;
    }
}
