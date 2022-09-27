package utils.date;

import models.care.EffectiveCareModel;
import org.joda.time.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * DateChecker.
 *
 * @author Pierre Adam
 * @since 20.08.24
 */
public class DateChecker {

    /**
     * The Logger.
     */
    private static final Logger logger;
    /**
     * The Extra holidays.
     */
    private static List<LocalDate> extraHolidays;

    static {
        extraHolidays = new ArrayList<>();
        logger = LoggerFactory.getLogger(DateChecker.class);
    }

    /**
     * Sets extra holidays.
     *
     * @param extraHolidays the extra holidays
     */
    public static void setExtraHolidays(final List<LocalDate> extraHolidays) {
        DateChecker.extraHolidays = extraHolidays;
    }

    /**
     * Has night or bank holiday boolean.
     *
     * @param effectiveCare the effective care
     * @return the boolean
     */
    public static boolean hasNightOrBankHoliday(final EffectiveCareModel effectiveCare) {
        return hasNightOrBankHoliday(effectiveCare.getStartDate(), effectiveCare.getEndDate());
    }

    /**
     * Has night or bank holiday boolean.
     *
     * @param startDate the start date
     * @param endDate   the end date
     * @return the boolean
     */
    public static boolean hasNightOrBankHoliday(final DateTime startDate, final DateTime endDate) {
        final DateContext context = getContext(startDate, endDate);
        return context.night != 0 || context.bankHoliday != 0 || context.nightBankHoliday != 0;
    }

    /**
     * Gets context.
     *
     * @param effectiveCare the effective care
     * @return the context
     */
    public static DateContext getContext(final EffectiveCareModel effectiveCare) {
        return getContext(effectiveCare.getStartDate(), effectiveCare.getEndDate());
    }

    /**
     * Gets context.
     *
     * @param startDate the start date
     * @param endDate   the end date
     * @return the context
     */
    public static DateContext getContext(final DateTime startDate, final DateTime endDate) {
        if (endDate.isBefore(startDate)) {
            throw new RuntimeException("Invalid dates. End date is before start date.");
        }

        final LocalTime nightStart = new LocalTime(22, 0);
        final LocalTime nightEnd = new LocalTime(6, 0);
        DateTime date = new DateTime(startDate);
        long delta;
        final DateContext dateContext = new DateContext();

        while (!date.isEqual(endDate) || endDate.isAfter(date)) {
            logger.trace("{} => {}", date, endDate);
            final LocalTime localTime = date.toLocalTime();
            final LocalDate localDate = date.toLocalDate();
            final boolean bankHoliday = localDate.getDayOfWeek() == DateTimeConstants.SUNDAY || DateChecker.extraHolidays.contains(localDate);

            if (localTime.isBefore(nightEnd)) {
                // During the night (morning)
                delta = calculateDelta(date, nightEnd, endDate);
                if (bankHoliday) {
                    dateContext.addNightBankHolidayMillis(delta);
                } else {
                    dateContext.addNightMillis(delta);
                }
            } else if (localTime.isBefore(nightStart)) {
                // During the day
                delta = calculateDelta(date, nightStart, endDate);
                if (bankHoliday) {
                    dateContext.addBankHolidayMillis(delta);
                } else {
                    dateContext.addStandardMillis(delta);
                }
            } else {
                // During the night (evening)
                delta = calculateDeltaToNextDay(date, endDate);
                if (bankHoliday) {
                    dateContext.addNightBankHolidayMillis(delta);
                } else {
                    dateContext.addNightMillis(delta);
                }
            }
            logger.trace("Advancing by {}", delta / (60 * 1000));
            date = date.plus(delta);
        }
        return dateContext;
    }

    /**
     * Calculate delta long.
     *
     * @param date    the date
     * @param to      the to
     * @param maximum the maximum
     * @return the long
     */
    private static long calculateDelta(final DateTime date, final LocalTime to, final DateTime maximum) {
        final LocalTime time = date.toLocalTime();
        final DateTime max = date.withTime(to);
        if (max.isBefore(maximum) || max.equals(maximum)) {
            return to.getMillisOfDay() - time.getMillisOfDay();
        } else {
            return maximum.toLocalTime().getMillisOfDay() - time.getMillisOfDay();
        }
    }

    /**
     * Calculate delta to next date long.
     *
     * @param date    the date
     * @param maximum the maximum
     * @return the long
     */
    private static long calculateDeltaToNextDay(final DateTime date, final DateTime maximum) {
        final LocalTime time = date.toLocalTime();
        final DateTime max = date.plusDays(1).withMillisOfDay(0);
        if (max.isBefore(maximum) || max.isEqual(maximum)) {
            return 24 * 3600 * 1000 - time.getMillisOfDay();
        } else {
            return maximum.toLocalTime().getMillisOfDay() - time.getMillisOfDay();
        }
    }

    /**
     * The type Date context.
     */
    public static class DateContext {
        /**
         * The Standard.
         */
        private long standard;

        /**
         * The Night.
         */
        private long night;

        /**
         * The Bank holiday.
         */
        private long bankHoliday;

        /**
         * The Night bank holiday.
         */
        private long nightBankHoliday;

        /**
         * Instantiates a new Date context.
         */
        public DateContext() {
            this.standard = 0L;
            this.night = 0L;
            this.bankHoliday = 0L;
            this.nightBankHoliday = 0L;
        }

        /**
         * Gets night.
         *
         * @return the night
         */
        public Duration getStandard() {
            return new Duration(this.standard);
        }

        /**
         * Gets night.
         *
         * @return the night
         */
        public Duration getNight() {
            return new Duration(this.night);
        }

        /**
         * Gets bank holiday.
         *
         * @return the bank holiday
         */
        public Duration getBankHoliday() {
            return new Duration(this.bankHoliday);
        }

        /**
         * Gets night bank holiday.
         *
         * @return the night bank holiday
         */
        public Duration getNightBankHoliday() {
            return new Duration(this.nightBankHoliday);
        }

        /**
         * Add night millis.
         *
         * @param millis the millis
         */
        public void addStandardMillis(final long millis) {
            this.standard += millis;
        }

        /**
         * Add night millis.
         *
         * @param millis the millis
         */
        public void addNightMillis(final long millis) {
            this.night += millis;
        }

        /**
         * Add bank holiday millis.
         *
         * @param millis the millis
         */
        public void addBankHolidayMillis(final long millis) {
            this.bankHoliday += millis;
        }

        /**
         * Add night bank holiday millis.
         *
         * @param millis the millis
         */
        public void addNightBankHolidayMillis(final long millis) {
            this.nightBankHoliday += millis;
        }

        /**
         * Add context.
         *
         * @param dateContext the date context
         */
        public void addContext(final DateContext dateContext) {
            this.standard += dateContext.standard;
            this.night += dateContext.night;
            this.bankHoliday += dateContext.bankHoliday;
            this.nightBankHoliday += dateContext.nightBankHoliday;
        }

        /**
         * Gets total.
         *
         * @return the total
         */
        public Duration getTotal() {
            return new Duration(this.standard + this.night + this.bankHoliday + this.nightBankHoliday);
        }
    }
}
