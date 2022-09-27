package form.parent.onboarding;

import entities.caf.IncomeType;
import form.parent.BookedCareForm;
import models.children.enumeration.ChildCategory;
import play.data.validation.Constraints;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ParentOnboardingForm.
 *
 * @author Lucas Stadelmann
 * @since 20.07.23
 */
public class OnboardingForm extends BookedCareForm {

    /**
     * The Search address.
     */
    private String searchAddress;

    /**
     * The Address.
     */
    @Constraints.Required
    private String address;

    /**
     * The Zip code.
     */
    @Constraints.Required
    private String zipCode;

    /**
     * The City.
     */
    @Constraints.Required
    private String city;

    /**
     * The Children.
     */
    private List<ChildrenForm> children;

    /**
     * The Single parent.
     */
    private boolean singleParent;

    /**
     * The Parent working.
     */
    private boolean parentWorking;

    /**
     * The Disabled parent.
     */
    private boolean disabledParent;

    /**
     * The Disabled child benefit.
     */
    private boolean disabledChildBenefit;

    /**
     * The Income.
     */
    @Constraints.Required
    private IncomeType income;

    /**
     * The Caf number.
     */
    @Constraints.MinLength(value = 7)
    @Constraints.MaxLength(value = 7)
    private String cafNumber;

    /**
     * Instantiates a new Onboarding form.
     */
    public OnboardingForm() {
    }

    /**
     * Gets search address.
     *
     * @return the search address
     */
    public String getSearchAddress() {
        return this.searchAddress;
    }

    /**
     * Sets search address.
     *
     * @param searchAddress the search address
     */
    public void setSearchAddress(final String searchAddress) {
        this.searchAddress = searchAddress;
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
     * Sets address.
     *
     * @param address the address
     */
    public void setAddress(final String address) {
        this.address = address.trim();
    }

    /**
     * Gets zip code.
     *
     * @return the zip code
     */
    public String getZipCode() {
        return this.zipCode;
    }

    /**
     * Sets zip code.
     *
     * @param zipCode the zip code
     */
    public void setZipCode(final String zipCode) {
        this.zipCode = zipCode.trim();
    }

    /**
     * Gets city.
     *
     * @return the city
     */
    public String getCity() {
        return this.city;
    }

    /**
     * Sets city.
     *
     * @param city the city
     */
    public void setCity(final String city) {
        this.city = city.trim();
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
     * Gets children categories.
     *
     * @return the children categories
     */
    public List<ChildCategory> getChildrenCategories() {
        return this.children.stream().map(ChildrenForm::getCategory).collect(Collectors.toList());
    }

    /**
     * Gets single parent.
     *
     * @return the single parent
     */
    public Boolean getSingleParent() {
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
    public Boolean getParentWorking() {
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
     * Gets disabled parent.
     *
     * @return the disabled parent
     */
    public Boolean getDisabledParent() {
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
    public Boolean getDisabledChildBenefit() {
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
     * Gets children of category.
     *
     * @param childCategory the child category
     * @return the children of category
     */
    public int getChildrenOfCategory(final ChildCategory childCategory) {
        int childCount = 0;

        if (childCategory.equals(ChildCategory.BIRTHDAY)) {
            return childCount;
        }

        for (final ChildrenForm child : this.children) {
            if (child.getCategory().equals(childCategory)) {
                childCount++;
            }
        }

        return childCount;
    }
}
