package security;

import entities.multiselect.SelectData;
import interfaces.SelectableEnum;
import io.ebean.annotation.DbEnumValue;

/**
 * Role.
 *
 * @author Pierre Adam
 * @since 20.06.01
 */
public enum Role {

    /**
     * Role parent role.
     */
    NONE("security.role.none"),

    /**
     * Role parent role.
     */
    ROLE_PARENT("security.role.parent"),

    /**
     * Role sitter role.
     */
    ROLE_SITTER("security.role.sitter"),

    /**
     * Role admin role.
     */
    ROLE_ADMIN("security.role.admin");

    /**
     * The Translation key.
     */
    private final String translationKey;

    /**
     * Instantiates a new Role.
     *
     * @param translationKey the translation key
     */
    Role(final String translationKey) {
        this.translationKey = translationKey;
    }

    /**
     * Select data select data.
     *
     * @return the select data
     */
    public static SelectData selectData() {
        return SelectableEnum.selectData(Role.values(), Role::getTranslationKey, Role.NONE);
    }

    /**
     * Gets value.
     *
     * @return the value
     */
    @DbEnumValue(length = 32)
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
