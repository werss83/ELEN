package models.sitter.enumeration;

import entities.multiselect.SelectData;
import interfaces.SelectableEnum;
import io.ebean.annotation.DbEnumType;
import io.ebean.annotation.DbEnumValue;

/**
 * SitterExperienceType.
 *
 * @author Pierre Adam
 * @since 20.07.17
 */
public enum SitterExperienceType {

    /**
     * Baby sitter experience type.
     */
    BABY("models.sitter.enumeration.sitterExperienceType.baby"),

    /**
     * Under 3 yo sitter experience type.
     */
    UNDER_3_YO("models.sitter.enumeration.sitterExperienceType.under3yo"),

    /**
     * Under 6 yo sitter experience type.
     */
    UNDER_6_YO("models.sitter.enumeration.sitterExperienceType.under6yo"),

    /**
     * Under 10 yo sitter experience type.
     */
    UNDER_10_YO("models.sitter.enumeration.sitterExperienceType.under10yo"),

    /**
     * Over 10 yo sitter experience type.
     */
    OVER_10_YO("models.sitter.enumeration.sitterExperienceType.over10yo");

    /**
     * The Translation key.
     */
    private final String translationKey;

    /**
     * Instantiates a new SitterExperienceType.
     *
     * @param translationKey the translation key
     */
    SitterExperienceType(final String translationKey) {
        this.translationKey = translationKey;
    }

    /**
     * Select data select data.
     *
     * @return the select data
     */
    public static SelectData selectData() {
        return SelectableEnum.selectData(SitterExperienceType.values(), SitterExperienceType::getTranslationKey);
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
