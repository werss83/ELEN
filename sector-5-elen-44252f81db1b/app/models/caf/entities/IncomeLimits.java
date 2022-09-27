package models.caf.entities;

import com.fasterxml.jackson.annotation.*;
import entities.caf.IncomeLimitsAmount;
import interfaces.CafPercentage;

/**
 * IncomeLimits.
 *
 * @author Pierre Adam
 * @since 20.07.23
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class IncomeLimits implements CafPercentage {

    /**
     * The Base low.
     */
    private Long baseLow;

    /**
     * The Base high.
     */
    private Long baseHigh;

    /**
     * The Step low.
     */
    private Long stepLow;

    /**
     * The Step high.
     */
    private Long stepHigh;

    /**
     * The Single parent percentage increase.
     */
    private Double singleParent;

    /**
     * Instantiates a new Income limits.
     */
    public IncomeLimits() {
    }

    /**
     * Instantiates a new Income limits.
     *
     * @param baseLow      the base low
     * @param baseHigh     the base high
     * @param stepLow      the step low
     * @param stepHigh     the step high
     * @param singleParent the single parent percentage increase
     */
    @JsonCreator
    public IncomeLimits(@JsonProperty(value = "baseLow", required = true) final Long baseLow,
                        @JsonProperty(value = "baseHigh", required = true) final Long baseHigh,
                        @JsonProperty(value = "stepLow", required = true) final Long stepLow,
                        @JsonProperty(value = "stepHigh", required = true) final Long stepHigh,
                        @JsonProperty(value = "sppi", required = true) final Double singleParent) {
        this.baseLow = baseLow;
        this.baseHigh = baseHigh;
        this.stepLow = stepLow;
        this.stepHigh = stepHigh;
        this.singleParent = singleParent;
    }

    /**
     * Gets base low.
     *
     * @return the base low
     */
    @JsonGetter
    public Long getBaseLow() {
        return this.baseLow;
    }

    /**
     * Sets base low.
     *
     * @param baseLow the base low
     */
    @JsonSetter
    public void setBaseLow(final Long baseLow) {
        this.baseLow = baseLow;
    }

    /**
     * Gets base high.
     *
     * @return the base high
     */
    @JsonGetter
    public Long getBaseHigh() {
        return this.baseHigh;
    }

    /**
     * Sets base high.
     *
     * @param baseHigh the base high
     */
    @JsonSetter
    public void setBaseHigh(final Long baseHigh) {
        this.baseHigh = baseHigh;
    }

    /**
     * Gets step low.
     *
     * @return the step low
     */
    @JsonGetter
    public Long getStepLow() {
        return this.stepLow;
    }

    /**
     * Sets step low.
     *
     * @param stepLow the step low
     */
    @JsonSetter
    public void setStepLow(final Long stepLow) {
        this.stepLow = stepLow;
    }

    /**
     * Gets step high.
     *
     * @return the step high
     */
    @JsonGetter
    public Long getStepHigh() {
        return this.stepHigh;
    }

    /**
     * Sets step high.
     *
     * @param stepHigh the step high
     */
    @JsonSetter
    public void setStepHigh(final Long stepHigh) {
        this.stepHigh = stepHigh;
    }

    /**
     * Gets single parent percentage increase.
     *
     * @return the single parent percentage increase
     */
    @JsonGetter("sppi")
    public Double getSingleParent() {
        return this.singleParent;
    }

    /**
     * Sets single parent percentage increase.
     *
     * @param singleParent the single parent percentage increase
     */
    @JsonSetter("sppi")
    public void setSingleParent(final Double singleParent) {
        this.singleParent = singleParent;
    }

    /**
     * Gets income level.
     *
     * @param childrenNumber the children number
     * @param isSingleParent the single parent
     * @return the income level
     */
    @JsonIgnore
    public IncomeLimitsAmount getIncomeLevel(final int childrenNumber, final boolean isSingleParent) {
        return new IncomeLimitsAmount(
            this.calculateLimit(this.baseLow, this.stepLow, childrenNumber, isSingleParent),
            this.calculateLimit(this.baseHigh, this.stepHigh, childrenNumber, isSingleParent)
        );
    }

    /**
     * Calculate limit long.
     *
     * @param base           the base
     * @param isSingleParent the is single parent
     * @return the long
     */
    @JsonIgnore
    private Long calculateLimit(final Long base, final Long step, final int childrenNumber, final boolean isSingleParent) {
        Long limit = base;

        if (childrenNumber > 1) {
            limit += step * (childrenNumber - 1);
        }
        limit += this.addPercentage(base, this.singleParent, isSingleParent);
        return limit;
    }
}
