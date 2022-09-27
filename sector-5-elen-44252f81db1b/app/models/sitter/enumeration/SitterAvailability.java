package models.sitter.enumeration;

import entities.multiselect.SelectData;
import interfaces.SelectableEnum;
import io.ebean.annotation.DbEnumType;
import io.ebean.annotation.DbEnumValue;

/**
 * SitterAvailability.
 *
 * @author Pierre Adam
 * @since 20.07.17
 */
public enum SitterAvailability {

    /**
     * Mornings sitter availability.
     */
    MORNINGS("models.sitter.enumeration.sitterAvailability.mornings"),

    /**
     * Afternoons sitter availability.
     */
    AFTERNOONS("models.sitter.enumeration.sitterAvailability.afternoons"),

    /**
     * School exits sitter availability.
     */
    SCHOOL_EXITS("models.sitter.enumeration.sitterAvailability.schoolExits"),

    /**
     * Evenings sitter availability.
     */
    EVENINGS("models.sitter.enumeration.sitterAvailability.evenings"),

    /**
     * Wednesdays sitter availability.
     */
    WEDNESDAYS("models.sitter.enumeration.sitterAvailability.wednesdays");

    /**
     * The Translation key.
     */
    private final String translationKey;

    /**
     * Instantiates a new SitterAvailability.
     *
     * @param translationKey the translation key
     */
    SitterAvailability(final String translationKey) {
        this.translationKey = translationKey;
    }

    /**
     * Select data select data.
     *
     * @return the select data
     */
    public static SelectData selectData() {
        return SelectableEnum.selectData(SitterAvailability.values(), SitterAvailability::getTranslationKey);
    }

    /**
     * Gets value.
     *
     * @return the value
     */
    @DbEnumValue(storage = DbEnumType.VARCHAR, length = 24)
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
