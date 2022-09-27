package models.unavailability.enumeration;

import io.ebean.annotation.DbEnumType;
import io.ebean.annotation.DbEnumValue;

/**
 * PlannedUnavailabilityType.
 *
 * @author Lucas Stadelmann
 * @since 20.06.06
 */
public enum PlannedUnavailabilityType {

    /**
     * A One time unavailability.
     */
    ONE_TIME,

    /**
     * A Recurrent unavailability.
     */
    RECURRENT;

    /**
     * Get the integer value assigned to this enum.
     *
     * @return The database integer value
     */
    @DbEnumValue(storage = DbEnumType.VARCHAR)
    public String getValue() {
        return this.name();
    }
}
