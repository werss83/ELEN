package form.parent.onboarding;

import models.children.enumeration.ChildCategory;
import models.children.enumeration.UsualChildcare;

/**
 * ChildForm.
 *
 * @author Lucas Stadelmann
 * @since 20.08.24
 */
public class ChildrenForm {

    /**
     * The Category.
     */
    private ChildCategory category;

    /**
     * The Childcare.
     */
    private UsualChildcare childcare;

    /**
     * Instantiates a new Children form.
     */
    public ChildrenForm() {
    }

    /**
     * Gets category.
     *
     * @return the category
     */
    public ChildCategory getCategory() {
        return this.category;
    }

    /**
     * Sets category.
     *
     * @param category the category
     */
    public void setCategory(final ChildCategory category) {
        this.category = category;
    }

    /**
     * Gets childcare.
     *
     * @return the childcare
     */
    public UsualChildcare getChildcare() {
        return this.childcare;
    }

    /**
     * Sets childcare.
     *
     * @param childcare the childcare
     */
    public void setChildcare(final String childcare) {
        if ("null".equals(childcare)) {
            this.childcare = UsualChildcare.OTHER;
        } else {
            this.childcare = UsualChildcare.valueOf(childcare);
        }
    }

    /**
     * Sets child care.
     *
     * @param childCare the child care
     */
    public void setUsualChildcare(final UsualChildcare childCare) {
        this.childcare = childCare;
    }
}
