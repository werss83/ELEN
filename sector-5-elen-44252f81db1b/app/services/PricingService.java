package services;

import form.parent.onboarding.CareForm;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.date.DateChecker;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

/**
 * PricingService.
 *
 * @author Lucas Stadelmann
 * @since 20.09.04
 */
@Singleton
public class PricingService {

    /**
     * The Logger.
     */
    protected final Logger logger;

    /**
     * The constant FULL_HOUR_COST.
     */
    public static final Long FULL_HOUR_COST = 2500L;

    /**
     * The constant REDUCE_HOUR_COST.
     */
    public static final Long REDUCE_HOUR_COST = 2200L;

    /**
     * The constant ELEN_FULL_HOUR_COST.
     */
    public static final Long ELEN_FULL_HOUR_COST = 373L;

    /**
     * The constant ELEN_RECUCE_HOUR_COST.
     */
    public static final Long ELEN_RECUCE_HOUR_COST = 100L;

    /**
     * The constant DUTY_HOUR_COST.
     */
    public static final Long DUTY_HOUR_COST = 227L;

    /**
     * The constant PAYROLL_HOUR_COST.
     */
    public static final Long PAYROLL_HOUR_COST = 900L;

    /**
     * The constant SALARY_HOUR_COST.
     */
    public static final Long SALARY_HOUR_COST = 1000L;

    /**
     * The constant KILOMETER_CHARGE_COST.
     */
    public static final Long KILOMETER_CHARGE_COST = 54L;

    /**
     * The constant FULL_HOUR_LIMIT.
     */
    public static final Double FULL_HOUR_LIMIT = 20d;

    /**
     * The constant NIGHT_MULTIPLIER.
     */
    public static final Double NIGHT_MULTIPLIER = 1.30d;

    /**
     * The constant BANK_HOLIDAY_MULTIPLIER.
     */
    public static final Double BANK_HOLIDAY_MULTIPLIER = 1.50d;

    /**
     * Instantiates a new Pricing service.
     */
    @Inject
    public PricingService() {
        this.logger = LoggerFactory.getLogger(PricingService.class);
    }

    /**
     * Gets one time care cost.
     *
     * @param care the care
     * @return the one time care cost
     */
    public double getOneTimeCareCost(final CareForm care) {
        return this.getCareCost(
            care.getStartDate(),
            care.getEndDate(),
            true
        );
    }

    /**
     * Gets recurrent care cost.
     *
     * @param cares the cares
     * @return the recurrent care cost
     */
    public double getRecurrentCareCost(final List<CareForm> cares) {
        double totalHour = 0;
        double cost = 0;

        for (final CareForm care : cares) {
            if (totalHour < PricingService.FULL_HOUR_LIMIT) {
                if (totalHour + this.toHourFraction(care.getDuration()) > PricingService.FULL_HOUR_LIMIT) {
                    final double fullDuration = PricingService.FULL_HOUR_LIMIT - totalHour;

                    cost += this.getCareCost(
                        care.getStartDate(),
                        care.getEndDate().minusMillis(this.fromHourToMillis(fullDuration)),
                        true
                    );

                    cost += this.getCareCost(
                        care.getStartDate().plusMillis(this.fromHourToMillis(fullDuration)),
                        care.getEndDate(),
                        false
                    );
                } else {
                    cost += this.getCareCost(
                        care.getStartDate(),
                        care.getEndDate(),
                        true
                    );
                }
                totalHour += this.toHourFraction(care.getDuration());
            } else {
                cost += this.getCareCost(
                    care.getStartDate(),
                    care.getEndDate(),
                    false
                );
            }
        }
        return cost;
    }

    /**
     * Gets care cost.
     *
     * @param startDate the start date
     * @param endDate   the end date
     * @param fullHour  the full hour
     * @return the care cost
     */
    private double getCareCost(final DateTime startDate,
                               final DateTime endDate,
                               final boolean fullHour) {
        final long baseCost = fullHour ? PricingService.FULL_HOUR_COST : PricingService.REDUCE_HOUR_COST;
        final long duration = endDate.minusMillis(startDate.getMillisOfDay()).getMillisOfDay();
        final DateChecker.DateContext context = DateChecker.getContext(startDate, endDate);

        final long normalDuration = duration - context.getNight().getMillis() - context.getBankHoliday().getMillis() - context.getNightBankHoliday().getMillis();

        double cost = 0;
        cost += this.toHourFraction(normalDuration) * baseCost;
        cost += this.toHourFraction(context.getNight().getMillis()) * baseCost * PricingService.NIGHT_MULTIPLIER;
        cost += this.toHourFraction(context.getBankHoliday().getMillis()) * baseCost * PricingService.BANK_HOLIDAY_MULTIPLIER;
        cost += this.toHourFraction(context.getNightBankHoliday().getMillis()) * baseCost * PricingService.BANK_HOLIDAY_MULTIPLIER;

        return cost;
    }

    /**
     * To hour fraction.
     *
     * @param duration the duration
     * @return the double
     */
    private double toHourFraction(final long duration) {
        return (double) duration / (1000 * 3600);
    }

    /**
     * From hour to millis.
     *
     * @param duration the duration
     * @return the int
     */
    private int fromHourToMillis(final double duration) {
        return (int) duration * 3600 * 1000;
    }
}
