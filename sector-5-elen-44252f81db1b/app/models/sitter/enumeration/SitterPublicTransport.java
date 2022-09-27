package models.sitter.enumeration;

import entities.multiselect.SelectData;
import interfaces.SelectableEnum;
import io.ebean.annotation.DbEnumType;
import io.ebean.annotation.DbEnumValue;

/**
 * SitterPublicTransport.
 *
 * @author Pierre Adam
 * @since 20.07.17
 */
public enum SitterPublicTransport {

    /**
     * None sitter public transport.
     */
    NONE("models.sitter.enumeration.sitterPublicTransport.none"),

    /**
     * Bus sitter public transport.
     */
    BUS("models.sitter.enumeration.sitterPublicTransport.bus"),

    /**
     * Metro sitter public transport.
     */
    METRO("models.sitter.enumeration.sitterPublicTransport.metro");

    /**
     * The Translation key.
     */
    private final String translationKey;

    /**
     * Instantiates a new SitterPublicTransport.
     *
     * @param translationKey the translation key
     */
    SitterPublicTransport(final String translationKey) {
        this.translationKey = translationKey;
    }

    /**
     * Select data select data.
     *
     * @return the select data
     */
    public static SelectData selectData() {
        return SelectableEnum.selectData(SitterPublicTransport.values(), SitterPublicTransport::getTranslationKey);
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
