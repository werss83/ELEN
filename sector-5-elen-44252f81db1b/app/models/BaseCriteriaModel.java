package models;

import javax.persistence.MappedSuperclass;

/**
 * BaseCriteriaModel.
 *
 * @param <T> the type parameter
 * @author Lucas Stadelmann
 * @since 20.07.05
 */
@MappedSuperclass
public class BaseCriteriaModel<T extends BaseCriteriaModel<T>> extends BaseAttributeModel<T> {

    /**
     * Instantiates a new Base criteria model.
     *
     * @param tClass the t class
     */
    public BaseCriteriaModel(final Class<T> tClass) {
        super(tClass);
    }

    /**
     * Gets if the parent want a woman sitter.
     *
     * @return if the parent want a woman sitter
     */
    public boolean wantAWomanSitter() {
        return this.gender;
    }

    /**
     * Sets if the parent want a woman sitter.
     *
     * @param womanSitter if the parent want a woman sitter
     */
    public void setWomanSitter(final boolean womanSitter) {
        this.gender = womanSitter;
    }

    /**
     * Gets if the parent want a sitter with car.
     *
     * @return if the parent want a sitter with car
     */
    public boolean wantASitterWithCar() {
        return this.haveCar;
    }

    /**
     * Sets if the parent want a sitter with car.
     *
     * @param sitterWithCar the sitter with car
     */
    public void setSitterWithCar(final boolean sitterWithCar) {
        this.haveCar = sitterWithCar;
    }

    /**
     * Gets if the parent want a sitter that speaks english.
     *
     * @return if the parent want a sitter that speaks english
     */
    public boolean wantASitterThatSpeaksEnglish() {
        return this.speaksEnglish;
    }

    /**
     * Sets if the parent want a sitter that speaks english.
     *
     * @param sitterThatSpeaksEnglish if the parent want a sitter that speaks english
     */
    public void setSitterSpeakEnglish(final boolean sitterThatSpeaksEnglish) {
        this.speaksEnglish = sitterThatSpeaksEnglish;
    }

    /**
     * Gets if the parent want an experienced sitter.
     *
     * @return if the parent want an experienced sitter
     */
    public boolean wantAnExperiencedSitter() {
        return this.experienceWithChildren;
    }

    /**
     * Sets if the parent want an experienced sitter.
     *
     * @param experiencedSitter if the parent want an experienced sitter
     */
    public void setExperiencedSitter(final boolean experiencedSitter) {
        this.experienceWithChildren = experiencedSitter;
    }

    /**
     * Gets if the parent allow a smoking sitter.
     *
     * @return if the parent allow a smoking sitter
     */
    public boolean allowASmokingSitter() {
        return this.smoke;
    }

    /**
     * Sets if the parent allow a smoking sitter.
     *
     * @param smokingSitter the smoking sitter
     */
    public void setSmokingSitter(final boolean smokingSitter) {
        this.smoke = smokingSitter;
    }

    /**
     * Gets if the parent want a childhood trained sitter.
     *
     * @return if the parent want a childhood trained sitter
     */
    public boolean wantAChildhoodTrainedSitter() {
        return this.childhoodTraining;
    }

    /**
     * Sets if the parent want a childhood trained sitter.
     *
     * @param childhoodTrainedSitter if the parent want a childhood trained sitter
     */
    public void setChildhoodTrainedSitter(final boolean childhoodTrainedSitter) {
        this.childhoodTraining = childhoodTrainedSitter;
    }
}
