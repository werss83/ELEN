package form.parent.onboarding;

import com.jackson42.play.form.databinders.joda.annotation.JodaDateTimeFormat;
import form.AForm;
import org.joda.time.DateTime;
import play.data.validation.Constraints;

/**
 * CareForm.
 *
 * @author Lucas Stadelmann
 * @since 20.08.24
 */
public class CareForm extends AForm {

    /**
     * The Date.
     */
    @Constraints.Required
    @JodaDateTimeFormat(patterns = "dd/MM/YYYY")
    private DateTime date;

    /**
     * The Start hour.
     */
    @Constraints.Required
    @JodaDateTimeFormat(patterns = "HH:mm")
    private DateTime startHour;

    /**
     * The Duration.
     */
    @Constraints.Required
    private Double duration;

    /**
     * Instantiates a new Care form.
     */
    public CareForm() {
        this.duration = 0d;
    }

    /**
     * Gets date.
     *
     * @return the date
     */
    public DateTime getDate() {
        return this.date;
    }

    /**
     * Sets date.
     *
     * @param date the date
     */
    public void setDate(final DateTime date) {
        this.date = date;
    }

    /**
     * Gets start hour.
     *
     * @return the start hour
     */
    public DateTime getStartHour() {
        return this.startHour;
    }

    /**
     * Sets start hour.
     *
     * @param startHour the start hour
     */
    public void setStartHour(final DateTime startHour) {
        this.startHour = startHour;
        if (this.startHour != null) {
            this.startHour.withMinuteOfHour(0);
        }
    }

    /**
     * Gets duration.
     *
     * @return the duration
     */
    public long getDuration() {
        return Math.round(this.duration * 3600000L);
    }

    public double getDurationInHours() {
        return this.duration;
    }

    /**
     * Sets duration.
     *
     * @param duration the duration
     */
    public void setDuration(final Double duration) {
        this.duration = duration;
    }

    /**
     * Gets start date.
     *
     * @return the start date
     */
    public DateTime getStartDate() {
        return this.date.withMillisOfDay(this.startHour.getMillisOfDay());
    }

    /**
     * Gets end date.
     *
     * @return the end date
     */
    public DateTime getEndDate() {
        return this.getStartDate().plusMillis(Math.toIntExact(this.getDuration()));
    }
}
