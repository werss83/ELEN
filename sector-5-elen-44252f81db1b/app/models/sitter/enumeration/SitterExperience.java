package models.sitter.enumeration;

import entities.multiselect.SelectData;
import interfaces.SelectableEnum;
import io.ebean.annotation.DbEnumType;
import io.ebean.annotation.DbEnumValue;

/**
 * SitterExperience.
 *
 * @author Pierre Adam
 * @since 20.07.17
 */
public enum SitterExperience {

    /**
     * No sitter experience.
     */
    NO_EXPERIENCE("models.sitter.enumeration.sitterExperience.noExperience"),

    /**
     * Family sitter experience.
     */
    FAMILY_EXPERIENCE("models.sitter.enumeration.sitterExperience.familyExperience"),

    /**
     * Professional sitter experience.
     */
    PROFESSIONAL_EXPERIENCE("models.sitter.enumeration.sitterExperience.professionalExperience");

    /**
     * The Translation key.
     */
    private final String translationKey;

    /**
     * Instantiates a new SitterExperience.
     *
     * @param translationKey the translation key
     */
    SitterExperience(final String translationKey) {
        this.translationKey = translationKey;
    }

    /**
     * Select data select data.
     *
     * @return the select data
     */
    public static SelectData selectData() {
        return SelectableEnum.selectData(SitterExperience.values(), SitterExperience::getTranslationKey);
    }

    /**
     * Gets value.
     *
     * @return the value
     */
    @DbEnumValue(storage = DbEnumType.VARCHAR, length = 48)
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
