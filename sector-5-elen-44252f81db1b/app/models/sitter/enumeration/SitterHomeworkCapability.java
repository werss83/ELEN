package models.sitter.enumeration;

import entities.multiselect.SelectData;
import interfaces.SelectableEnum;
import io.ebean.annotation.DbEnumType;
import io.ebean.annotation.DbEnumValue;

/**
 * SitterHomeworkCapability.
 *
 * @author Pierre Adam
 * @since 20.07.17
 */
public enum SitterHomeworkCapability {

    /**
     * None sitter homework capability.
     */
    NONE("models.sitter.enumeration.sitterHomeworkCapability.none"),

    /**
     * Primary school sitter homework capability.
     */
    PRIMARY_SCHOOL("models.sitter.enumeration.sitterHomeworkCapability.primarySchool"),

    /**
     * Middle school sitter homework capability.
     */
    MIDDLE_SCHOOL("models.sitter.enumeration.sitterHomeworkCapability.middleSchool");

    /**
     * The Translation key.
     */
    private final String translationKey;

    /**
     * Instantiates a new SitterHomeworkCapability.
     *
     * @param translationKey the translation key
     */
    SitterHomeworkCapability(final String translationKey) {
        this.translationKey = translationKey;
    }

    /**
     * Select data select data.
     *
     * @return the select data
     */
    public static SelectData selectData() {
        return SelectableEnum.selectData(SitterHomeworkCapability.values(), SitterHomeworkCapability::getTranslationKey);
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
