package form.parent;

import form.AForm;
import form.parent.onboarding.CareForm;
import models.care.enumeration.BookedCareType;

import java.util.List;

/**
 * BookedCareForm.
 *
 * @author Lucas Stadelmann
 * @since 20.09.10
 */
public class BookedCareForm extends AForm {

    /**
     * The Care type.
     */
    private BookedCareType careType;

    /**
     * The One time care.
     */
    private CareForm oneTimeCare;

    /**
     * The Recurrent care.
     */
    private List<CareForm> recurrentCare;

    /**
     * Instantiates a new Booked care form.
     */
    public BookedCareForm() {
    }

    /**
     * Gets care type.
     *
     * @return the care type
     */
    public BookedCareType getCareType() {
        return this.careType;
    }

    /**
     * Sets care type.
     *
     * @param careType the care type
     */
    public void setCareType(final BookedCareType careType) {
        this.careType = careType;
    }

    /**
     * Gets one time care.
     *
     * @return the one time care
     */
    public CareForm getOneTimeCare() {
        return this.oneTimeCare;
    }

    /**
     * Sets one time care.
     *
     * @param oneTimeCare the one time care
     */
    public void setOneTimeCare(final CareForm oneTimeCare) {
        this.oneTimeCare = oneTimeCare;
    }

    /**
     * Gets recurrent care.
     *
     * @return the recurrent care
     */
    public List<CareForm> getRecurrentCare() {
        return this.recurrentCare;
    }

    /**
     * Sets recurrent care.
     *
     * @param recurrentCare the recurrent care
     */
    public void setRecurrentCare(final List<CareForm> recurrentCare) {
        this.recurrentCare = recurrentCare;
    }
}
