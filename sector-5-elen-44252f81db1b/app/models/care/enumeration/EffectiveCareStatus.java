package models.care.enumeration;

import io.ebean.annotation.DbEnumType;
import io.ebean.annotation.DbEnumValue;

/**
 * EffectiveCareStatus.
 *
 * @author Lucas Stadelmann
 * @since 20.06.06
 */
public enum EffectiveCareStatus {

    /**
     * A Planned care.
     */
    PLANNED("models.care.enumeration.careStatus.planned"),

    /**
     * An Assigned care (assigned to a sitter).
     */
    ASSIGNED("models.care.enumeration.careStatus.assigned"),

    /**
     * A performed care (performed by a sitter).
     */
    PERFORMED("models.care.enumeration.careStatus.performed"),

    /**
     * A Cancelled care (cancelled by a parent, a sitter or an admin).
     */
    CANCELLED("models.care.enumeration.careStatus.cancelled"),

    /**
     * An Unsuccessful care (a care which didn't find a matching sitter).
     */
    UNSUCCESSFUL("models.care.enumeration.careStatus.unsuccessful");

    /**
     * The Translation key.
     */
    private final String translationKey;

    /**
     * Instantiates a new Effective care status.
     *
     * @param translationKey the translation key
     */
    EffectiveCareStatus(final String translationKey) {
        this.translationKey = translationKey;
    }

    /**
     * Get the integer value assigned to this enum.
     *
     * @return The database integer value
     */
    @DbEnumValue(storage = DbEnumType.VARCHAR)
    public String getValue() {
        return this.name();
    }

    /**
     * Gets translation key.
     *
     * @return the translation key
     */
    public String getTranslationKey() {
        return this.translationKey;
    }
}
