package models.children.enumeration;

import io.ebean.annotation.DbEnumType;
import io.ebean.annotation.DbEnumValue;

/**
 * ChildCategory.
 *
 * @author Pierre Adam
 * @since 20.07.14
 */
public enum ChildCategory {

    /**
     * We don't know the birthday but he's under 3 years old.
     */
    UNDER_3_YEARS,

    /**
     * We don't know the birthday but he's over 3 years old.
     */
    UNDER_6_YEARS,

    /**
     * We don't know the birthday but he's over 3 years old.
     */
    OVER_6_YEARS,

    /**
     * We use the birthday.
     */
    BIRTHDAY;

    /**
     * Gets value.
     *
     * @return the value
     */
    @DbEnumValue(storage = DbEnumType.VARCHAR, length = 24)
    public String getValue() {
        return this.name();
    }
}
