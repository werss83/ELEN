package form.parent.onboarding;

import entities.caf.IncomeType;
import models.children.enumeration.ChildCategory;
import play.data.validation.Constraints;

import java.util.List;
import java.util.stream.Collectors;

/**
 * MonthlySupportForm.
 *
 * @author Lucas Stadelmann
 * @since 20.09.16
 */
public class MonthlySupportForm {

    /**
     * The Income type.
     */
    @Constraints.Required
    private IncomeType income;

    /**
     * The Child categories.
     */
    @Constraints.Required
    private List<ChildrenForm> children;

    /**
     * The Parent working.
     */
    @Constraints.Required
    private boolean parentWorking;

    /**
     * The Single parent.
     */
    @Constraints.Required
    private boolean singleParent;

    /**
     * The Shared care.
     */
    @Constraints.Required
    private boolean sharedCare;

    /**
     * The Disabled parent.
     */
    @Constraints.Required
    private boolean disabledParent;

    /**
     * The Disabled child benefit.
     */
    @Constraints.Required
    private boolean disabledChildBenefit;

    /**
     * Instantiates a new Monthly support form.
     */
    public MonthlySupportForm() {
    }

    /**
     * Gets income.
     *
     * @return the income
     */
    public IncomeType getIncome() {
        return this.income;
    }

    /**
     * Sets income.
     *
     * @param income the income
     */
    public void setIncome(final IncomeType income) {
        this.income = income;
    }

    /**
     * Gets children.
     *
     * @return the children
     */
    public List<ChildrenForm> getChildren() {
        return this.children;
    }

    /**
     * Sets children.
     *
     * @param children the children
     */
    public void setChildren(final List<ChildrenForm> children) {
        this.children = children;
    }

    /**
     * Gets child categories.
     *
     * @return the child categories
     */
    public List<ChildCategory> getChildCategories() {
        return this.children.stream().map(ChildrenForm::getCategory).collect(Collectors.toList());
    }

    /**
     * Gets single parent.
     *
     * @return the single parent
     */
    public boolean getSingleParent() {
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
     * Gets parent working.
     *
     * @return the parent working
     */
    public boolean getParentWorking() {
        return this.parentWorking;
    }

    /**
     * Sets parent working.
     *
     * @param parentWorking the parent working
     */
    public void setParentWorking(final boolean parentWorking) {
        this.parentWorking = parentWorking;
    }

    /**
     * Gets shared care.
     *
     * @return the shared care
     */
    public boolean getSharedCare() {
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
     * Gets disabled parent.
     *
     * @return the disabled parent
     */
    public boolean getDisabledParent() {
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
     * Gets disabled child benefit.
     *
     * @return the disabled child benefit
     */
    public boolean getDisabledChildBenefit() {
        return this.disabledChildBenefit;
    }

    /**
     * Sets disabled child benefit.
     *
     * @param disabledChildBenefit the disabled child benefit
     */
    public void setDisabledChildBenefit(final boolean disabledChildBenefit) {
        this.disabledChildBenefit = disabledChildBenefit;
    }
}
