package models.sitter.enumeration;

import entities.multiselect.SelectData;
import interfaces.SelectableEnum;
import io.ebean.annotation.DbEnumType;
import io.ebean.annotation.DbEnumValue;

/**
 * SitterCarSituation.
 *
 * @author Pierre Adam
 * @since 20.07.17
 */
public enum SitterCarSituation {

    /**
     * Yes sitter car situation.
     */
    YES("models.sitter.enumeration.sitterCarSituation.yes"),

    /**
     * No but planned sitter car situation.
     */
    NO_BUT_PLANNED("models.sitter.enumeration.sitterCarSituation.noButPlanned"),

    /**
     * No sitter car situation.
     */
    NO("models.sitter.enumeration.sitterCarSituation.no");

    /**
     * The Translation key.
     */
    private final String translationKey;

    /**
     * Instantiates a new SitterCarSituation.
     *
     * @param translationKey the translation key
     */
    SitterCarSituation(final String translationKey) {
        this.translationKey = translationKey;
    }

    /**
     * Select data select data.
     *
     * @return the select data
     */
    public static SelectData selectData() {
        return SelectableEnum.selectData(SitterCarSituation.values(), SitterCarSituation::getTranslationKey);
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
