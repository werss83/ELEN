package models.sitter.enumeration;

import entities.multiselect.SelectData;
import interfaces.SelectableEnum;
import io.ebean.annotation.DbEnumType;
import io.ebean.annotation.DbEnumValue;

/**
 * SitterGuardType.
 *
 * @author Pierre Adam
 * @since 20.07.17
 */
public enum SitterGuardType {

    /**
     * Casual sitter guard type.
     */
    CASUAL("models.sitter.enumeration.sitterGuardType.casual"),

    /**
     * Part time sitter guard type.
     */
    PART_TIME("models.sitter.enumeration.sitterGuardType.partTime"),

    /**
     * Full time sitter guard type.
     */
    FULL_TIME("models.sitter.enumeration.sitterGuardType.fullTime");

    /**
     * The Translation key.
     */
    private final String translationKey;

    /**
     * Instantiates a new SitterGuardType.
     *
     * @param translationKey the translation key
     */
    SitterGuardType(final String translationKey) {
        this.translationKey = translationKey;
    }

    /**
     * Select data select data.
     *
     * @return the select data
     */
    public static SelectData selectData() {
        return SelectableEnum.selectData(SitterGuardType.values(), SitterGuardType::getTranslationKey);
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
