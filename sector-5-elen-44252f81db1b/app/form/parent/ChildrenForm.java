package form.parent;

import com.jackson42.play.form.databinders.joda.annotation.JodaDateTimeFormat;
import form.AForm;
import models.children.ChildrenModel;
import models.children.enumeration.ChildCategory;
import models.children.enumeration.UsualChildcare;
import models.parent.ParentModel;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * ChildrenForm.
 *
 * @author Pierre Adam
 * @since 20.07.18
 */
@Constraints.Validate
public class ChildrenForm extends AForm implements Constraints.Validatable<List<ValidationError>> {

    /**
     * The Uid.
     */
    protected UUID uid;

    /**
     * The Firstname.
     */
    @Constraints.Required
    protected String firstname;

    /**
     * The Birthday.
     */
    @Constraints.Required
    @JodaDateTimeFormat(patterns = "dd/MM/yyyy")
    protected DateTime birthday;

    /**
     * The Usual care.
     */
    @Constraints.Required
    protected UsualChildcare usualCare;

    /**
     * The Children.
     */
    private ChildrenModel children;

    /**
     * Instantiates a new Children form.
     */
    public ChildrenForm() {
        this.usualCare = UsualChildcare.OTHER;
    }

    /**
     * Instantiates a new Children form.
     *
     * @param children the children
     */
    public ChildrenForm(final ChildrenModel children) {
        this.children = children;
        this.uid = children.getUid();
        this.firstname = children.getFirstName();
        this.birthday = children.getBirthDate() == null ? null : children.getBirthDate().toDateTime(new LocalTime(0));
        this.usualCare = children.getUsualChildcare();
    }

    /**
     * Gets uid.
     *
     * @return the uid
     */
    public UUID getUid() {
        return this.uid;
    }

    /**
     * Sets uid.
     *
     * @param uid the uid
     */
    public void setUid(final UUID uid) {
        this.uid = uid;
    }

    /**
     * Gets firstname.
     *
     * @return the firstname
     */
    public String getFirstname() {
        return this.firstname;
    }

    /**
     * Sets firstname.
     *
     * @param firstname the firstname
     */
    public void setFirstname(final String firstname) {
        this.firstname = firstname;
    }

    /**
     * Gets birthday.
     *
     * @return the birthday
     */
    public DateTime getBirthday() {
        return this.birthday;
    }

    /**
     * Sets birthday.
     *
     * @param birthday the birthday
     */
    public void setBirthday(final DateTime birthday) {
        this.birthday = birthday;
    }

    /**
     * Gets usual care.
     *
     * @return the usual care
     */
    public UsualChildcare getUsualCare() {
        return this.usualCare;
    }

    /**
     * Sets usual care.
     *
     * @param usualCare the usual care
     */
    public void setUsualCare(final UsualChildcare usualCare) {
        this.usualCare = usualCare;
    }

    /**
     * Update or create children model.
     *
     * @param parent the parent
     * @return the children model
     */
    public ChildrenModel updateOrCreate(final ParentModel parent) {
        if (this.children == null) {
            this.children = new ChildrenModel(this.firstname, this.birthday.toLocalDate(), parent);
        } else {
            this.children.setBirthDate(this.birthday.toLocalDate());
            this.children.setCategory(ChildCategory.BIRTHDAY);
            this.children.setFirstName(this.firstname);
        }
        this.children.setUsualChildcare(this.usualCare);

        return this.children;
    }

    @Override
    public List<ValidationError> validate() {
        final List<ValidationError> errors = new ArrayList<>();

        if (this.birthday != null) {
            // Invalid date of birth if after now or before 18 years ago.
            if (this.birthday.isAfter(DateTime.now())) {
                errors.add(new ValidationError("birthday", "error.invalid"));
            }
            if (this.birthday.isBefore(DateTime.now().minusYears(21))) {
                errors.add(new ValidationError("birthday", "error.invalid.noMoreThan21"));
            }
        }

        if (this.uid != null) {
            // If the UID is present, solve the children from the uid.
            this.children = ChildrenModel.find.query().where().eq("uid", this.uid).findOne();
            if (this.children == null) {
                this.logger.warn("Invalid uid from the form. The UID was \"{}\"", this.uid);
                errors.add(new ValidationError("uid", "error.invalid"));
            }
        }

        return errors.isEmpty() ? null : errors;
    }
}
