package form.parent.onboarding;

import play.data.validation.Constraints;

/**
 * IncomeLimitForm.
 *
 * @author Lucas Stadelmann
 * @since 20.08.17
 */
public class IncomeLimitForm {

    /**
     * The Single parent.
     */
    @Constraints.Required
    private boolean singleParent;

    /**
     * The Children number.
     */
    @Constraints.Required
    private int childrenNumber;

    /**
     * Instantiates a new Income limit form.
     */
    public IncomeLimitForm() {
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
     * Gets children number.
     *
     * @return the children number
     */
    public int getChildrenNumber() {
        return this.childrenNumber;
    }

    /**
     * Sets children number.
     *
     * @param childrenNumber the children number
     */
    public void setChildrenNumber(final int childrenNumber) {
        this.childrenNumber = childrenNumber;
    }
}
