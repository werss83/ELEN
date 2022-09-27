package models.parent.enumeration;

import models.options.Option;
import play.i18n.Messages;

/**
 * ParentOptionKey.
 *
 * @author Lucas Stadelmann
 * @since 20.06.08
 */
public enum ParentOptionKey {

    /**
     * CAF number.
     */
    @Option(defaultValue = "", type = String.class)
    CAF_NUMBER("models.parent.option.cafNumber"),

    /**
     * Single parent.
     */
    @Option(defaultValue = "false", type = Boolean.class)
    SINGLE_PARENT("models.parent.option.singleParent"),

    /**
     * Shared Care.
     */
    @Option(defaultValue = "false", type = Boolean.class)
    SHARED_CARE("models.parent.option.sharedCare"),

    /**
     * Parent working.
     */
    @Option(defaultValue = "false", type = Boolean.class)
    PARENT_WORKING("models.parent.option.singleParent"),

    /**
     * Parent disabled.
     */
    @Option(defaultValue = "false", type = Boolean.class)
    DISABLED_PARENT("models.parent.option.disabledParent"),

    /**
     * Disabled child benefit.
     */
    @Option(defaultValue = "false", type = Boolean.class)
    DISABLED_CHILD_BENEFIT("models.parent.option.disabledChildBenefit"),

    /**
     * Income.
     */
    @Option(defaultValue = "0", type = Long.class)
    INCOME("models.parent.option.income"),

    /**
     * Income group.
     */
    @Option(defaultValue = "ABOVE_MAX", type = ParentIncomeGroup.class)
    INCOME_GROUP("models.parent.option.incomeGroup");

    /**
     * The Translation key.
     */
    private final String translationKey;

    /**
     * Instantiates a new Parent option key.
     *
     * @param translationKey the translation key
     */
    ParentOptionKey(final String translationKey) {
        this.translationKey = translationKey;
    }

    /**
     * Gets translation.
     *
     * @param messages the messages
     * @return the translation
     */
    public String getTranslation(final Messages messages) {
        return messages.apply(this.translationKey);
    }
}
