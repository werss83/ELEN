package form.admin.cares;

import behavior.UUIDUtil;
import form.AForm;
import models.care.EffectiveCareModel;
import models.sitter.SitterModel;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;

import java.util.List;
import java.util.UUID;

/**
 * EditOneTimeCare.
 *
 * @author Pierre Adam
 * @since 20.08.04
 */
@Constraints.Validate
public class EditFutureSitterForm extends AForm implements Constraints.Validatable<List<ValidationError>> {

    /**
     * The Sitter.
     */
    @Constraints.Required
    protected String sitterUid;

    /**
     * The Sitter.
     */
    private SitterModel resolvedSitter;

    /**
     * Instantiates a new Edit one time care.
     */
    public EditFutureSitterForm() {
    }

    /**
     * Instantiates a new Edit one time care.
     *
     * @param effectiveCare the effective care
     */
    public EditFutureSitterForm(final EffectiveCareModel effectiveCare) {
        if (effectiveCare.getEffectiveUnavailability() != null &&
            effectiveCare.getEffectiveUnavailability().getPlannedUnavailability() != null &&
            effectiveCare.getEffectiveUnavailability().getPlannedUnavailability().getSitter() != null) {
            this.resolvedSitter = effectiveCare.getEffectiveUnavailability().getPlannedUnavailability().getSitter();
            this.sitterUid = this.resolvedSitter.getUid().toString();
        }
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
}
