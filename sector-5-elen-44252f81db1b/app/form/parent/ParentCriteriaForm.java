package form.parent;

import form.AForm;
import models.BaseCriteriaModel;
import models.parent.ParentCriteriaModel;
import models.parent.ParentModel;

/**
 * ParentPreferences.
 *
 * @author Pierre Adam
 * @since 20.07.12
 */
public class ParentCriteriaForm extends AForm {

    /**
     * The Woman sitter.
     */
    protected boolean womanSitter;

    /**
     * The Sitter with car.
     */
    protected boolean sitterWithCar;

    /**
     * The Speak english.
     */
    protected boolean speakEnglish;

    /**
     * The Allowed smoking sitter.
     */
    protected boolean allowedSmokingSitter;

    /**
     * The Experienced sitter.
     */
    protected boolean experiencedSitter;

    /**
     * The Trained sitter.
     */
    protected boolean trainedSitter;

    /**
     * Instantiates a new Parent preferences form.
     */
    public ParentCriteriaForm() {
    }

    /**
     * Instantiates a new Parent preferences form.
     *
     * @param parent the parent
     */
    public ParentCriteriaForm(final ParentModel parent) {
        if (parent == null) {
            return;
        }
        final ParentCriteriaModel criteria = parent.getParentCriteria();
        this.womanSitter = criteria.wantAWomanSitter();
        this.sitterWithCar = criteria.wantASitterWithCar();
        this.speakEnglish = criteria.wantASitterThatSpeaksEnglish();
        this.allowedSmokingSitter = criteria.allowASmokingSitter();
        this.experiencedSitter = criteria.wantAnExperiencedSitter();
        this.trainedSitter = criteria.wantAChildhoodTrainedSitter();
    }

    /**
     * Is woman sitter boolean.
     *
     * @return the boolean
     */
    public boolean isWomanSitter() {
        return this.womanSitter;
    }

    /**
     * Sets woman sitter.
     *
     * @param womanSitter the woman sitter
     */
    public void setWomanSitter(final boolean womanSitter) {
        this.womanSitter = womanSitter;
    }

    /**
     * Is sitter with car boolean.
     *
     * @return the boolean
     */
    public boolean isSitterWithCar() {
        return this.sitterWithCar;
    }

    /**
     * Sets sitter with car.
     *
     * @param sitterWithCar the sitter with car
     */
    public void setSitterWithCar(final boolean sitterWithCar) {
        this.sitterWithCar = sitterWithCar;
    }

    /**
     * Is speak english boolean.
     *
     * @return the boolean
     */
    public boolean isSpeakEnglish() {
        return this.speakEnglish;
    }

    /**
     * Sets speak english.
     *
     * @param speakEnglish the speak english
     */
    public void setSpeakEnglish(final boolean speakEnglish) {
        this.speakEnglish = speakEnglish;
    }

    /**
     * Is allowed smoking sitter boolean.
     *
     * @return the boolean
     */
    public boolean isAllowedSmokingSitter() {
        return this.allowedSmokingSitter;
    }

    /**
     * Sets allowed smoking sitter.
     *
     * @param allowedSmokingSitter the allowed smoking sitter
     */
    public void setAllowedSmokingSitter(final boolean allowedSmokingSitter) {
        this.allowedSmokingSitter = allowedSmokingSitter;
    }

    /**
     * Is experienced sitter boolean.
     *
     * @return the boolean
     */
    public boolean isExperiencedSitter() {
        return this.experiencedSitter;
    }

    /**
     * Sets experienced sitter.
     *
     * @param experiencedSitter the experienced sitter
     */
    public void setExperiencedSitter(final boolean experiencedSitter) {
        this.experiencedSitter = experiencedSitter;
    }

    /**
     * Is trained sitter boolean.
     *
     * @return the boolean
     */
    public boolean isTrainedSitter() {
        return this.trainedSitter;
    }

    /**
     * Sets trained sitter.
     *
     * @param trainedSitter the trained sitter
     */
    public void setTrainedSitter(final boolean trainedSitter) {
        this.trainedSitter = trainedSitter;
    }

    /**
     * Update criteria.
     *
     * @param criteria the criteria
     */
    public void updateCriteria(final BaseCriteriaModel<?> criteria) {
        criteria.setChildhoodTrainedSitter(this.trainedSitter);
        criteria.setExperiencedSitter(this.experiencedSitter);
        criteria.setSitterSpeakEnglish(this.speakEnglish);
        criteria.setSitterWithCar(this.sitterWithCar);
        criteria.setSmokingSitter(this.allowedSmokingSitter);
        criteria.setWomanSitter(this.womanSitter);
        criteria.save();
    }
}
