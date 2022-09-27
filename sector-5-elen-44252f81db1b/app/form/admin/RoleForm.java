package form.admin;

import form.AForm;
import io.ebean.Model;
import models.account.AccountModel;
import models.account.RoleModel;
import play.data.validation.Constraints;
import security.Role;

import java.util.List;
import java.util.stream.Collectors;

/**
 * RoleForm.
 *
 * @author Pierre Adam
 * @since 20.07.04
 */
public class RoleForm extends AForm {

    /**
     * The Roles.
     */
    @Constraints.Required
    protected List<Role> roles;

    /**
     * Instantiates a new Role form.
     */
    public RoleForm() {
    }

    /**
     * Instantiates a new Role form.
     *
     * @param account the account
     */
    public RoleForm(final AccountModel account) {
        this.roles = account.getRoles().stream().map(RoleModel::getRole).collect(Collectors.toList());
    }

    /**
     * Gets roles.
     *
     * @return the roles
     */
    public List<Role> getRoles() {
        return this.roles;
    }

    /**
     * Sets roles.
     *
     * @param roles the roles
     */
    public void setRoles(final List<Role> roles) {
        this.roles = roles;
    }

    /**
     * Update account roles.
     *
     * @param account the account
     */
    public void updateAccountRoles(final AccountModel account) {
        final List<RoleModel> toDelete = account.getRoles();
        for (final Role role : this.roles) {
            boolean found = false;
            for (final RoleModel roleModel : toDelete) {
                if (roleModel.getRole().equals(role)) {
                    // The account already has the role.
                    found = true;
                    toDelete.remove(roleModel);
                    break;
                }
            }
            if (!found) {
                final RoleModel newRole = new RoleModel(account, role);
                newRole.save();
            }
        }
        toDelete.forEach(Model::deletePermanent);
    }
}
