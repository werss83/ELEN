package form.admin;

import com.jackson42.play.form.databinders.joda.annotation.JodaDateTimeFormat;
import form.AForm;
import org.joda.time.DateTime;

/**
 * ExportForm.
 *
 * @author Pierre Adam
 * @since 20.08.31
 */
public class ExportForm extends AForm {

    /**
     * The Start date.
     */
    @JodaDateTimeFormat(patterns = "dd/MM/YYYY")
    protected DateTime startDate;

    /**
     * The End date.
     */
    @JodaDateTimeFormat(patterns = "dd/MM/YYYY")
    protected DateTime endDate;

    /**
     * The Flavor.
     */
    protected String flavor;

    /**
     * Gets start date.
     *
     * @return the start date
     */
    public DateTime getStartDate() {
        return this.startDate;
    }

    /**
     * Sets start date.
     *
     * @param startDate the start date
     */
    public void setStartDate(final DateTime startDate) {
        this.startDate = startDate;
    }

    /**
     * Gets end date.
     *
     * @return the end date
     */
    public DateTime getEndDate() {
        return this.endDate;
    }

    /**
     * Sets end date.
     *
     * @param endDate the end date
     */
    public void setEndDate(final DateTime endDate) {
        this.endDate = endDate;
    }

    /**
     * Gets flavor.
     *
     * @return the flavor
     */
    public String getFlavor() {
        return this.flavor;
    }

    /**
     * Sets flavor.
     *
     * @param flavor the flavor
     */
    public void setFlavor(final String flavor) {
        this.flavor = flavor;
    }
}
