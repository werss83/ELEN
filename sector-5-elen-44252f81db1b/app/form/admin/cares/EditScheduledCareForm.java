package form.admin.cares;

import behavior.constraints.ReadOnly;
import com.jackson42.play.form.databinders.joda.annotation.JodaDateTimeFormat;
import form.AForm;
import models.care.BookedCareModel;
import org.joda.time.DateTime;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;

import java.util.List;

/**
 * EditOneTimeCare.
 *
 * @author Pierre Adam
 * @since 20.08.04
 */
@Constraints.Validate
public class EditScheduledCareForm extends AForm implements Constraints.Validatable<List<ValidationError>> {

    /**
     * The Start date.
     */
    @ReadOnly
    @JodaDateTimeFormat(patterns = "dd/MM/YYYY HH:mm")
    protected DateTime startDate;

    /**
     * The End date.
     */
    @Constraints.Required
    @JodaDateTimeFormat(patterns = "dd/MM/YYYY HH:mm")
    protected DateTime endDate;

    /**
     * Instantiates a new Edit one time care.
     */
    public EditScheduledCareForm() {
    }

    /**
     * Instantiates a new Edit one time care.
     *
     * @param bookedCare the effective care
     */
    public EditScheduledCareForm(final BookedCareModel bookedCare) {
        this.startDate = bookedCare.getStartDate();
        this.endDate = bookedCare.getEndDate();
    }

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

    @Override
    public List<ValidationError> validate() {
        return this.easyListValidate(validationErrors -> {
            if (this.startDate != null && this.endDate != null && this.endDate.isBefore(this.startDate)) {
                validationErrors.add(new ValidationError("endDate", "error.invalid"));
            }
        });
    }

    /**
     * Update booked care model.
     *
     * @param bookedCare the booked care
     */
    public void updateBookedCareModel(final BookedCareModel bookedCare) {
        //bookedCare.setStartDate(this.startDate);
        bookedCare.setEndDate(this.endDate);
    }
}
