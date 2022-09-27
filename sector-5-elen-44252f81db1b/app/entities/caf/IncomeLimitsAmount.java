package entities.caf;

/**
 * IncomeLevel.
 *
 * @author Pierre Adam
 * @since 20.07.23
 */
public class IncomeLimitsAmount {

    /**
     * The Low Limit.
     */
    private final Long lowLimit;

    /**
     * The High Limit.
     */
    private final Long highLimit;

    /**
     * Instantiates a new Income level.
     *
     * @param lowLimit  the low limit
     * @param highLimit the high limit
     */
    public IncomeLimitsAmount(final Long lowLimit, final Long highLimit) {
        this.lowLimit = lowLimit;
        this.highLimit = highLimit;
    }

    /**
     * Gets low limit.
     *
     * @return the low limit
     */
    public Long getLowLimit() {
        return this.lowLimit;
    }

    /**
     * Gets high limit.
     *
     * @return the high limit
     */
    public Long getHighLimit() {
        return this.highLimit;
    }

    /**
     * Gets income type.
     *
     * @param annualIncome the annual income
     * @return the income type
     */
    public IncomeType getIncomeType(final Long annualIncome) {
        if (annualIncome < this.lowLimit) {
            return IncomeType.LOW;
        } else if (annualIncome < this.highLimit) {
            return IncomeType.MEDIUM;
        } else {
            return IncomeType.HIGH;
        }
    }
}
