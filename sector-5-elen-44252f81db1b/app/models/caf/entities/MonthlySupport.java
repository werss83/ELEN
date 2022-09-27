package models.caf.entities;

import com.fasterxml.jackson.annotation.*;
import entities.caf.ChildType;
import entities.caf.IncomeType;
import interfaces.CafPercentage;

/**
 * MonthlySupport.
 *
 * @author Pierre Adam
 * @since 20.07.23
 */
public class MonthlySupport implements CafPercentage {

    /**
     * The Under 3.
     */
    private AmountPerIncome under3;

    /**
     * The Under 6.
     */
    private AmountPerIncome under6;

    /**
     * The Shared care.
     */
    private Double sharedCare;

    /**
     * The Night or bank holiday care.
     */
    private Double nightOrBankHolidayCare;

    /**
     * The Handicapped parent.
     */
    private Double handicappedParent;

    /**
     * The Handicapped children.
     */
    private Double handicappedChildren;

    /**
     * The Single parent.
     */
    private Double singleParent;

    /**
     * Instantiates a new Monthly support.
     */
    public MonthlySupport() {
    }

    /**
     * Instantiates a new Monthly support.
     *
     * @param under3                 the under 3
     * @param under6                 the under 6
     * @param sharedCare             the shared care
     * @param nightOrBankHolidayCare the night or bank holiday care
     * @param handicappedParent      the handicapped parent
     * @param handicappedChildren    the handicapped children
     * @param singleParent           the single parent
     */
    public MonthlySupport(@JsonProperty(value = "under3", required = true) final AmountPerIncome under3,
                          @JsonProperty(value = "under6", required = true) final AmountPerIncome under6,
                          @JsonProperty(value = "sc", required = true) final Double sharedCare,
                          @JsonProperty(value = "nobhc", required = true) final Double nightOrBankHolidayCare,
                          @JsonProperty(value = "hp", required = true) final Double handicappedParent,
                          @JsonProperty(value = "hc", required = true) final Double handicappedChildren,
                          @JsonProperty(value = "sp", required = true) final Double singleParent) {
        this.under3 = under3;
        this.under6 = under6;
        this.sharedCare = sharedCare;
        this.nightOrBankHolidayCare = nightOrBankHolidayCare;
        this.handicappedParent = handicappedParent;
        this.handicappedChildren = handicappedChildren;
        this.singleParent = singleParent;
    }

    /**
     * Gets under 3.
     *
     * @return the under 3
     */
    @JsonGetter
    public AmountPerIncome getUnder3() {
        return this.under3;
    }

    /**
     * Sets under 3.
     *
     * @param under3 the under 3
     */
    @JsonSetter
    public void setUnder3(final AmountPerIncome under3) {
        this.under3 = under3;
    }

    /**
     * Gets under 6.
     *
     * @return the under 6
     */
    @JsonGetter
    public AmountPerIncome getUnder6() {
        return this.under6;
    }

    /**
     * Sets under 6.
     *
     * @param under6 the under 6
     */
    @JsonSetter
    public void setUnder6(final AmountPerIncome under6) {
        this.under6 = under6;
    }

    /**
     * Gets shared care.
     *
     * @return the shared care
     */
    @JsonGetter("sc")
    public Double getSharedCare() {
        return this.sharedCare;
    }

    /**
     * Sets shared care.
     *
     * @param sharedCare the shared care
     */
    @JsonSetter("sc")
    public void setSharedCare(final Double sharedCare) {
        this.sharedCare = sharedCare;
    }

    /**
     * Gets night or bank holiday care.
     *
     * @return the night or bank holiday care
     */
    @JsonGetter("nobhc")
    public Double getNightOrBankHolidayCare() {
        return this.nightOrBankHolidayCare;
    }

    /**
     * Sets night or bank holiday care.
     *
     * @param nightOrBankHolidayCare the night or bank holiday care
     */
    @JsonSetter("nobhc")
    public void setNightOrBankHolidayCare(final Double nightOrBankHolidayCare) {
        this.nightOrBankHolidayCare = nightOrBankHolidayCare;
    }

    /**
     * Gets handicapped parent.
     *
     * @return the handicapped parent
     */
    @JsonGetter("hp")
    public Double getHandicappedParent() {
        return this.handicappedParent;
    }

    /**
     * Sets handicapped parent.
     *
     * @param handicappedParent the handicapped parent
     */
    @JsonSetter("hp")
    public void setHandicappedParent(final Double handicappedParent) {
        this.handicappedParent = handicappedParent;
    }

    /**
     * Gets handicapped children.
     *
     * @return the handicapped children
     */
    @JsonGetter("hc")
    public Double getHandicappedChildren() {
        return this.handicappedChildren;
    }

    /**
     * Sets handicapped children.
     *
     * @param handicappedChildren the handicapped children
     */
    @JsonSetter("hc")
    public void setHandicappedChildren(final Double handicappedChildren) {
        this.handicappedChildren = handicappedChildren;
    }

    /**
     * Gets single parent.
     *
     * @return the single parent
     */
    @JsonGetter("sp")
    public Double getSingleParent() {
        return this.singleParent;
    }

    /**
     * Sets single parent.
     *
     * @param singleParent the single parent
     */
    @JsonSetter("sp")
    public void setSingleParent(final Double singleParent) {
        this.singleParent = singleParent;
    }

    /**
     * Gets support amount.
     *
     * @param incomeType the income type
     * @return the support amount
     */
    @JsonIgnore
    public Long getSupportAmount(final IncomeType incomeType,
                                 final ChildType childType,
                                 final boolean isSharedCare,
                                 final boolean isSingleParent,
                                 final boolean isNightOrBankHolidayCare,
                                 final boolean isHandicappedParent,
                                 final boolean isHandicappedChildren) {
        Long base;

        switch (childType) {
            case UNDER_3:
                base = this.under3.getFor(incomeType);
                break;
            case UNDER_6:
                base = this.under6.getFor(incomeType);
                break;
            default:
                return 0L;
        }

        // If the parent is single. Applying the single parent multiplier.
        base += this.addPercentage(base, this.sharedCare, isSharedCare);

        // Fixing the base amount.
        Long amount = base;

        // Adding potential bonus.
        amount += this.addPercentage(base, this.singleParent, isSingleParent);
        amount += this.addPercentage(base, this.nightOrBankHolidayCare, isNightOrBankHolidayCare);
        amount += this.addPercentage(base, this.handicappedParent, isHandicappedParent);
        amount += this.addPercentage(base, this.handicappedChildren, isHandicappedChildren);

        return amount;
    }

    /**
     * The type Amount per income.
     */
    public static class AmountPerIncome {
        /**
         * The Low.
         */
        private Long low;

        /**
         * The Medium.
         */
        private Long medium;

        /**
         * The High.
         */
        private Long high;

        /**
         * Instantiates a new Amount per income.
         */
        public AmountPerIncome() {
        }

        /**
         * Instantiates a new Amount per income.
         *
         * @param low    the low
         * @param medium the medium
         * @param high   the high
         */
        @JsonCreator
        public AmountPerIncome(@JsonProperty(value = "low", required = true) final Long low,
                               @JsonProperty(value = "medium", required = true) final Long medium,
                               @JsonProperty(value = "high", required = true) final Long high) {
            this.low = low;
            this.medium = medium;
            this.high = high;
        }

        /**
         * Gets low.
         *
         * @return the low
         */
        @JsonGetter
        public Long getLow() {
            return this.low;
        }

        /**
         * Sets low.
         *
         * @param low the low
         */
        @JsonSetter
        public void setLow(final Long low) {
            this.low = low;
        }

        /**
         * Gets medium.
         *
         * @return the medium
         */
        @JsonGetter
        public Long getMedium() {
            return this.medium;
        }

        /**
         * Sets medium.
         *
         * @param medium the medium
         */
        @JsonSetter
        public void setMedium(final Long medium) {
            this.medium = medium;
        }

        /**
         * Gets high.
         *
         * @return the high
         */
        @JsonGetter
        public Long getHigh() {
            return this.high;
        }

        /**
         * Sets high.
         *
         * @param high the high
         */
        @JsonSetter
        public void setHigh(final Long high) {
            this.high = high;
        }

        /**
         * Gets for.
         *
         * @param incomeType the income type
         * @return the for
         */
        @JsonIgnore
        public Long getFor(final IncomeType incomeType) {
            switch (incomeType) {
                case LOW:
                    return this.low;
                case MEDIUM:
                    return this.medium;
                default:
                    return this.high;
            }
        }
    }
}
