package models.sitter.enumeration;

import entities.multiselect.SelectData;
import interfaces.SelectableEnum;
import io.ebean.annotation.DbEnumType;
import io.ebean.annotation.DbEnumValue;

/**
 * SitterSpokenLanguage.
 *
 * @author Pierre Adam
 * @since 20.07.17
 */
public enum SitterSpokenLanguage {

    /**
     * French sitter spoken language.
     */
    FRENCH("models.sitter.enumeration.sitterSpokenLanguage.french"),

    /**
     * English sitter spoken language.
     */
    ENGLISH("models.sitter.enumeration.sitterSpokenLanguage.english"),

    /**
     * German sitter spoken language.
     */
    GERMAN("models.sitter.enumeration.sitterSpokenLanguage.german"),

    /**
     * Spanish sitter spoken language.
     */
    SPANISH("models.sitter.enumeration.sitterSpokenLanguage.spanish"),

    /**
     * Portuguese sitter spoken language.
     */
    PORTUGUESE("models.sitter.enumeration.sitterSpokenLanguage.portuguese"),

    /**
     * Italian sitter spoken language.
     */
    ITALIAN("models.sitter.enumeration.sitterSpokenLanguage.italian"),

    /**
     * Russian sitter spoken language.
     */
    RUSSIAN("models.sitter.enumeration.sitterSpokenLanguage.russian"),

    /**
     * Arab sitter spoken language.
     */
    ARAB("models.sitter.enumeration.sitterSpokenLanguage.arab"),

    /**
     * Mandarin sitter spoken language.
     */
    MANDARIN("models.sitter.enumeration.sitterSpokenLanguage.mandarin");

    /**
     * The Translation key.
     */
    private final String translationKey;

    /**
     * Instantiates a new SitterSpokenLanguage.
     *
     * @param translationKey the translation key
     */
    SitterSpokenLanguage(final String translationKey) {
        this.translationKey = translationKey;
    }

    /**
     * Select data select data.
     *
     * @return the select data
     */
    public static SelectData selectData() {
        return SelectableEnum.selectData(SitterSpokenLanguage.values(), SitterSpokenLanguage::getTranslationKey);
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
