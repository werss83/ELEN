package services;

import com.typesafe.config.Config;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.TimeZone;

/**
 * TimezoneService.
 *
 * @author Pierre Adam
 * @since 20.07.24
 */
@Singleton
public class TimezoneService {

    /**
     * The constant timezoneStr.
     */
    private static String timezoneStr;

    /**
     * The constant timezone.
     */
    private static TimeZone timezone;

    /**
     * The constant dateTimeZone.
     */
    private static DateTimeZone dateTimeZone;

    /**
     * Instantiates a new Timezone service.
     *
     * @param config the config
     */
    @Inject
    public TimezoneService(final Config config) {
        timezoneStr = config.getString("settings.displayTimezone");
        timezone = TimeZone.getTimeZone(timezoneStr);
        dateTimeZone = DateTimeZone.forTimeZone(timezone);
    }

    /**
     * Gets timezone representation.
     *
     * @return the timezone representation
     */
    public static String getTimezoneRepresentation() {
        return timezoneStr;
    }

    /**
     * Gets timezone.
     *
     * @return the timezone
     */
    public static TimeZone getTimezone() {
        return timezone;
    }

    /**
     * Gets date time zone.
     *
     * @return the date time zone
     */
    public static DateTimeZone getDateTimeZone() {
        return dateTimeZone;
    }

    /**
     * Convert to zone date time.
     *
     * @param dateTime the date time
     * @return the date time
     */
    public static DateTime convertToZone(final DateTime dateTime) {
        return dateTime.withZone(dateTimeZone);
    }

    /**
     * From zone input date time.
     *
     * @param dateTime the date time
     * @return the date time
     */
    public static DateTime fromZoneInput(final DateTime dateTime) {
        return dateTime.withZoneRetainFields(dateTimeZone);
    }
}
