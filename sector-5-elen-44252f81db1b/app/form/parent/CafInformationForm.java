package form.parent;

import form.AForm;
import models.parent.ParentModel;
import models.parent.ParentOptionModel;
import models.parent.enumeration.ParentIncomeGroup;
import models.parent.enumeration.ParentOptionKey;
import play.data.validation.Constraints;

import java.util.function.BiConsumer;

/**
 * CafInformationForm.
 *
 * @author Pierre Adam
 * @since 20.09.04
 */
public class CafInformationForm extends AForm {

    /**
     * The Caf number.
     */
    @Constraints.MaxLength(7)
    protected String cafNumber;

    /**
     * The Income.
     */
    protected ParentIncomeGroup income;

    /**
     * The Single parent.
     */
    protected boolean singleParent;

    /**
     * The Shared care.
     */
    protected boolean sharedCare;

    /**
     * The Working parent.
     */
    protected boolean workingParent;

    /**
     * The Disabled parent.
     */
    protected boolean disabledParent;

    /**
     * The Disabled child.
     */
    protected boolean disabledChild;

    /**
     * Instantiates a new Caf information form.
     */
    public CafInformationForm() {
    }

    /**
     * Instantiates a new Caf information form.
     *
     * @param parent the parent
     */
    public CafInformationForm(final ParentModel parent) {
        this.cafNumber = parent.getOption(ParentOptionKey.CAF_NUMBER).getValueAsString();
        this.income = parent.getOption(ParentOptionKey.INCOME_GROUP).getValueAsIncomeGroup();
        this.singleParent = parent.getOption(ParentOptionKey.SINGLE_PARENT).getValueAsBoolean();
        this.sharedCare = parent.getOption(ParentOptionKey.SHARED_CARE).getValueAsBoolean();
        this.workingParent = parent.getOption(ParentOptionKey.PARENT_WORKING).getValueAsBoolean();
        this.disabledParent = parent.getOption(ParentOptionKey.DISABLED_PARENT).getValueAsBoolean();
        this.disabledChild = parent.getOption(ParentOptionKey.DISABLED_CHILD_BENEFIT).getValueAsBoolean();
    }

    /**
     * Gets caf number.
     *
     * @return the caf number
     */
    public String getCafNumber() {
        return this.cafNumber;
    }

    /**
     * Sets caf number.
     *
     * @param cafNumber the caf number
     */
    public void setCafNumber(final String cafNumber) {
        this.cafNumber = cafNumber;
    }

    /**
     * Gets income.
     *
     * @return the income
     */
    public ParentIncomeGroup getIncome() {
        return this.income;
    }

    /**
     * Sets income.
     *
     * @param income the income
     */
    public void setIncome(final ParentIncomeGroup income) {
        this.income = income;
    }

    /**
     * Is single parent boolean.
     *
     * @return the boolean
     */
    public boolean isSingleParent() {
        return this.singleParent;
    }

    /**
     * Sets single parent.
     *
     * @param singleParent the single parent
     */
    public void setSingleParent(final boolean singleParent) {
        this.singleParent = singleParent;
    }

    /**
     * Is shared care boolean.
     *
     * @return the boolean
     */
    public boolean isSharedCare() {
        return this.sharedCare;
    }

    /**
     * Sets shared care.
     *
     * @param sharedCare the shared care
     */
    public void setSharedCare(final boolean sharedCare) {
        this.sharedCare = sharedCare;
    }

    /**
     * Is working parent boolean.
     *
     * @return the boolean
     */
    public boolean isWorkingParent() {
        return this.workingParent;
    }

    /**
     * Sets working parent.
     *
     * @param workingParent the working parent
     */
    public void setWorkingParent(final boolean workingParent) {
        this.workingParent = workingParent;
    }

    /**
     * Is disabled parent boolean.
     *
     * @return the boolean
     */
    public boolean isDisabledParent() {
        return this.disabledParent;
    }

    /**
     * Sets disabled parent.
     *
     * @param disabledParent the disabled parent
     */
    public void setDisabledParent(final boolean disabledParent) {
        this.disabledParent = disabledParent;
    }

    /**
     * Is disabled child boolean.
     *
     * @return the boolean
     */
    public boolean isDisabledChild() {
        return this.disabledChild;
    }

    /**
     * Sets disabled child.
     *
     * @param disabledChild the disabled child
     */
    public void setDisabledChild(final boolean disabledChild) {
        this.disabledChild = disabledChild;
    }

    /**
     * Update parent.
     *
     * @param parent the parent
     */
    public void updateParent(final ParentModel parent) {
        this.setAndSave(parent, ParentOptionKey.CAF_NUMBER, ParentOptionModel::setValueAsString, this.cafNumber);
        this.setAndSave(parent, ParentOptionKey.INCOME_GROUP, ParentOptionModel::setValueAsIncomeGroup, this.income);
        this.setAndSave(parent, ParentOptionKey.SINGLE_PARENT, ParentOptionModel::setValueAsBoolean, this.singleParent);
        this.setAndSave(parent, ParentOptionKey.SHARED_CARE, ParentOptionModel::setValueAsBoolean, this.sharedCare);
        this.setAndSave(parent, ParentOptionKey.PARENT_WORKING, ParentOptionModel::setValueAsBoolean, this.workingParent);
        this.setAndSave(parent, ParentOptionKey.DISABLED_PARENT, ParentOptionModel::setValueAsBoolean, this.disabledParent);
        this.setAndSave(parent, ParentOptionKey.DISABLED_CHILD_BENEFIT, ParentOptionModel::setValueAsBoolean, this.disabledChild);
    }

    /**
     * Sets and save.
     *
     * @param <T>    the type parameter
     * @param parent the parent
     * @param key    the key
     * @param setter the setter
     * @param value  the value
     */
    private <T> void setAndSave(final ParentModel parent, final ParentOptionKey key, final BiConsumer<ParentOptionModel, T> setter, final T value) {
        final ParentOptionModel option = parent.getOption(key);
        setter.accept(option, value);
        option.save();
    }
}
