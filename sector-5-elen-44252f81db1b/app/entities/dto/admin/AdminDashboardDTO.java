package entities.dto.admin;

import models.account.RoleModel;
import security.Role;

/**
 * AdminDashboardDTO.
 *
 * @author Pierre Adam
 * @since 20.06.28
 */
public class AdminDashboardDTO {

    /**
     * The Number of parent.
     */
    private final int numberOfParent;

    /**
     * The Number of sitter.
     */
    private final int numberOfSitter;

    /**
     * Instantiates a new Admin dashboard dto.
     */
    public AdminDashboardDTO() {
        this.numberOfParent = RoleModel.find
            .query()
            .where()
            .eq("role", Role.ROLE_PARENT)
            .findCount();

        this.numberOfSitter = RoleModel.find
            .query()
            .where()
            .eq("role", Role.ROLE_SITTER)
            .findCount();
    }

    /**
     * Gets number of parent.
     *
     * @return the number of parent
     */
    public int getNumberOfParent() {
        return this.numberOfParent;
    }

    /**
     * Gets number of sitter.
     *
     * @return the number of sitter
     */
    public int getNumberOfSitter() {
        return this.numberOfSitter;
    }
}
