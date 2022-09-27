package models.parent.enumeration;

import entities.caf.IncomeType;
import io.ebean.annotation.DbEnumType;
import io.ebean.annotation.DbEnumValue;

/**
 * ParentIncomeGroup.
 *
 * @author Lucas Stadelmann
 * @since 20.06.29
 */
public enum ParentIncomeGroup {

    /**
     * Below min income group.
     */
    BELOW_MIN("models.parent.enumeration.incomeBelowMin"),

    /**
     * Below max income group.
     */
    BELOW_MAX("models.parent.enumeration.incomeBelowMax"),

    /**
     * Above max income group.
     */
    ABOVE_MAX("models.parent.enumeration.incomeAboveMax");

    /**
     * The Transalation key.
     */
    private final String transalationKey;

    /**
     * Instantiates a new Parent income group.
     *
     * @param transalationKey the transalation key
     */
    ParentIncomeGroup(final String transalationKey) {
        this.transalationKey = transalationKey;
    }

    /**
     * Gets appropriate income group.
     *
     * @param incomeType the income type
     * @return the appropriate income group
     */
    public static ParentIncomeGroup getAppropriateIncomeGroup(final IncomeType incomeType) {
        if (incomeType.equals(IncomeType.LOW)) {
            return BELOW_MIN;
        } else if (incomeType.equals(IncomeType.MEDIUM)) {
            return BELOW_MAX;
        } else if (incomeType.equals(IncomeType.HIGH)) {
            return ABOVE_MAX;
        }
        return null;
    }

    /**
     * Gets transalation key.
     *
     * @return the transalation key
     */
    public String getTransalationKey() {
        return this.transalationKey;
    }

    /**
     * Gets value.
     *
     * @return the value
     */
    @DbEnumValue(storage = DbEnumType.VARCHAR)
    public String getValue() {
        return this.name();
    }
}
