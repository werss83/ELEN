package models.sitter.enumeration;

import entities.multiselect.SelectData;
import interfaces.SelectableEnum;
import io.ebean.annotation.DbEnumType;
import io.ebean.annotation.DbEnumValue;

/**
 * SitterCertification.
 *
 * @author Pierre Adam
 * @since 20.07.17
 */
public enum SitterCertification {

    /**
     * First aid sitter certification.
     */
    FIRST_AID("models.sitter.enumeration.sitterCertification.firstAid"),

    /**
     * Bafa sitter certification.
     */
    BAFA("models.sitter.enumeration.sitterCertification.bafa"),

    /**
     * Young children training sitter certification.
     */
    YOUNG_CHILDREN_TRAINING("models.sitter.enumeration.sitterCertification.youngChildrenTraining");

    /**
     * The Translation key.
     */
    private final String translationKey;

    /**
     * Instantiates a new SitterCertification.
     *
     * @param translationKey the translation key
     */
    SitterCertification(final String translationKey) {
        this.translationKey = translationKey;
    }

    /**
     * Select data select data.
     *
     * @return the select data
     */
    public static SelectData selectData() {
        return SelectableEnum.selectData(SitterCertification.values(), SitterCertification::getTranslationKey);
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
