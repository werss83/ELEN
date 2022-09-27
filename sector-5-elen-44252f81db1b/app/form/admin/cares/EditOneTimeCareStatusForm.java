package form.admin.cares;

import form.AForm;
import models.care.EffectiveCareModel;
import models.care.enumeration.EffectiveCareStatus;
import play.data.validation.Constraints;

/**
 * EditOneTimeCareStatusForm.
 *
 * @author Pierre Adam
 * @since 20.08.06
 */
public class EditOneTimeCareStatusForm extends AForm {

    /**
     * The Sitter.
     */
    @Constraints.Required
    protected EffectiveCareStatus status;

    /**
     * Instantiates a new Edit one time care.
     */
    public EditOneTimeCareStatusForm() {
    }

    /**
     * Instantiates a new Edit one time care.
     *
     * @param effectiveCare the effective care
     */
    public EditOneTimeCareStatusForm(final EffectiveCareModel effectiveCare) {
        this.status = effectiveCare.getStatus();
    }

    /**
     * Gets status.
     *
     * @return the status
     */
    public EffectiveCareStatus getStatus() {
        return this.status;
    }

    /**
     * Sets status.
     *
     * @param status the status
     */
    public void setStatus(final EffectiveCareStatus status) {
        this.status = status;
    }
}
