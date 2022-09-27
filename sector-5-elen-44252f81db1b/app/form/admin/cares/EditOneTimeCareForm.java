package form.admin.cares;

import behavior.UUIDUtil;
import behavior.constraints.Disabled;
import behavior.constraints.ReadOnly;
import com.jackson42.play.form.databinders.joda.annotation.JodaDateTimeFormat;
import form.AForm;
import models.care.EffectiveCareModel;
import models.care.enumeration.EffectiveCareStatus;
import models.sitter.SitterModel;
import org.joda.time.DateTime;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;

import javax.validation.groups.Default;
import java.util.List;
import java.util.UUID;

/**
 * EditOneTimeCare.
 *
 * @author Pierre Adam
 * @since 20.08.04
 */
@Constraints.Validate
public class EditOneTimeCareForm extends AForm implements Constraints.Validatable<List<ValidationError>> {

    /**
     * The Start date.
     */
    @Constraints.Required(groups = {Default.class, Groups.PastCare.class})
    @JodaDateTimeFormat(patterns = "dd/MM/YYYY HH:mm")
    protected DateTime startDate;

    /**
     * The End date.
     */
    @Constraints.Required(groups = {Default.class, Groups.PastCare.class})
    @JodaDateTimeFormat(patterns = "dd/MM/YYYY HH:mm")
    protected DateTime endDate;

    /**
     * The Sitter.
     */
    @Constraints.Required(groups = {Default.class})
    @ReadOnly(groups = {Groups.PastCare.class})
    @Disabled(groups = {Groups.PastCare.class})
    protected String sitterUid;

    /**
     * The Sitter.
     */
    private SitterModel resolvedSitter;

    /**
     * Instantiates a new Edit one time care.
     */
    public EditOneTimeCareForm() {
    }

    /**
     * Instantiates a new Edit one time care.
     *
     * @param effectiveCare the effective care
     */
    public EditOneTimeCareForm(final EffectiveCareModel effectiveCare) {
        this.startDate = effectiveCare.getStartDate();
        this.endDate = effectiveCare.getEndDate();
        if (effectiveCare.getEffectiveUnavailability() != null &&
            effectiveCare.getEffectiveUnavailability().getPlannedUnavailability() != null &&
            effectiveCare.getEffectiveUnavailability().getPlannedUnavailability().getSitter() != null) {
            this.resolvedSitter = effectiveCare.getEffectiveUnavailability().getPlannedUnavailability().getSitter();
            this.sitterUid = this.resolvedSitter.getUid().toString();
        }
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

    /**
     * Gets sitter uid.
     *
     * @return the sitter uid
     */
    public String getSitterUid() {
        return this.sitterUid;
    }

    /**
     * Sets sitter uid.
     *
     * @param sitterUid the sitter uid
     */
    public void setSitterUid(final String sitterUid) {
        this.sitterUid = sitterUid;
    }

    /**
     * Gets sitter.
     *
     * @return the sitter
     */
    public SitterModel getResolvedSitter() {
        return this.resolvedSitter;
    }

    @Override
    public List<ValidationError> validate() {
        return this.easyListValidate(validationErrors -> {
            if (this.startDate != null && this.endDate != null && this.endDate.isBefore(this.startDate)) {
                validationErrors.add(new ValidationError("endDate", "error.invalid"));
            }
            if (this.sitterUid != null) {
                if (!this.sitterUid.equals(UUIDUtil.ZERO_UUID.toString())) {
                    this.resolvedSitter = SitterModel.find.query().where().eq("uid", UUID.fromString(this.sitterUid)).findOne();
                    if (this.resolvedSitter == null) {
                        validationErrors.add(new ValidationError("sitterUid", "error.invalid"));
                    }
                } else {
                    this.resolvedSitter = null;
                }
            }
        });
    }

    /**
     * The type Groups.
     */
    public static class Groups {
        public static Class<?> smartGroup(final EffectiveCareModel effectiveCare) {
            return effectiveCare.getStatus() == EffectiveCareStatus.PLANNED || effectiveCare.getStatus() == EffectiveCareStatus.ASSIGNED ? Default.class : PastCare.class;
        }

        /**
         * The interface Social form.
         */
        public interface PastCare {
        }
    }
}
