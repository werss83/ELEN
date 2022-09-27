package models.children.enumeration;

import entities.multiselect.SelectData;
import interfaces.SelectableEnum;
import io.ebean.annotation.DbEnumType;
import io.ebean.annotation.DbEnumValue;

/**
 * UsualChildcare.
 *
 * @author Lucas Stadelmann
 * @since 20.08.24
 */
public enum UsualChildcare {

    /**
     * Kindergarten usual childcare.
     */
    KINDERGARTEN("models.children.enumeration.usualChildcare.kindergarten", true),

    /**
     * Public nursery usual childcare.
     */
    PUBLIC_NURSERY("models.children.enumeration.usualChildcare.publicNursery", true),

    /**
     * Maternal assistance usual childcare.
     */
    MATERNAL_ASSISTANCE("models.children.enumeration.usualChildcare.maternalAssistance", false),

    /**
     * Private nursery usual childcare.
     */
    PRIVATE_NURSERY("models.children.enumeration.usualChildcare.privateNursery", false),

    /**
     * Other usual childcare.
     */
    OTHER("models.children.enumeration.usualChildcare.other", true);

    /**
     * The Translation key.
     */
    private final String translationKey;

    /**
     * The Allow grant.
     */
    private final boolean allowGrant;

    /**
     * Instantiates a new Usual childcare.
     *
     * @param translationKey the translation key
     * @param allowGrant     the allow grant
     */
    UsualChildcare(final String translationKey,
                   final boolean allowGrant) {
        this.translationKey = translationKey;
        this.allowGrant = allowGrant;
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

    /**
     * Gets allow grant.
     *
     * @return the allow grant
     */
    public boolean getAllowGrant() {
        return this.allowGrant;
    }

    /**
     * Select data select data.
     *
     * @return the select data
     */
    public static SelectData selectData() {
        return SelectableEnum.selectData(UsualChildcare.values(), UsualChildcare::getTranslationKey);
    }
}
