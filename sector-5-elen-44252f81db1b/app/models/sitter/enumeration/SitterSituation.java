package models.sitter.enumeration;

import entities.multiselect.SelectData;
import interfaces.SelectableEnum;
import io.ebean.annotation.DbEnumType;
import io.ebean.annotation.DbEnumValue;

/**
 * SitterSituation.
 *
 * @author Pierre Adam
 * @since 20.07.17
 */
public enum SitterSituation {

    /**
     * Student sitter situation.
     */
    STUDENT("models.sitter.enumeration.sitterCarSituation.student"),

    /**
     * Active with children sitter situation.
     */
    ACTIVE_WITH_CHILDREN("models.sitter.enumeration.sitterCarSituation.activeWithChildren"),

    /**
     * Active without children sitter situation.
     */
    ACTIVE_WITHOUT_CHILDREN("models.sitter.enumeration.sitterCarSituation.activeWithoutChildren"),

    /**
     * Senior sitter situation.
     */
    SENIOR("models.sitter.enumeration.sitterCarSituation.senior");

    /**
     * The Translation key.
     */
    private final String translationKey;

    /**
     * Instantiates a new SitterSituation.
     *
     * @param translationKey the translation key
     */
    SitterSituation(final String translationKey) {
        this.translationKey = translationKey;
    }

    /**
     * Select data select data.
     *
     * @return the select data
     */
    public static SelectData selectData() {
        return SelectableEnum.selectData(SitterSituation.values(), SitterSituation::getTranslationKey);
    }

    /**
     * Gets value.
     *
     * @return the value
     */
    @DbEnumValue(storage = DbEnumType.VARCHAR, length = 32)
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
