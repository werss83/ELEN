package models.care.enumeration;

import io.ebean.annotation.DbEnumType;
import io.ebean.annotation.DbEnumValue;

/**
 * BookedCareType.
 *
 * @author Lucas Stadelmann
 * @since 20.06.05
 */
public enum BookedCareType {

    /**
     * A One time care.
     */
    ONE_TIME,

    /**
     * A Recurrent care.
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
