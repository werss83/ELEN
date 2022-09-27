package models.account;

import io.ebean.Finder;
import models.BaseModel;
import security.Role;

import javax.persistence.*;
import javax.validation.constraints.Size;

/**
 * RoleModel.
 *
 * @author Pierre Adam
 * @since 20.06.01
 */
@Entity
@Table(name = "role")
public class RoleModel extends BaseModel<RoleModel> {

    /**
     * Helpers to request model.
     */
    public static final Finder<Long, RoleModel> find = new Finder<>(RoleModel.class);

    /**
     * The role name.
     */
    @Size(max = 32)
    @Column(name = "role", nullable = false, unique = false)
    private Role role;

    /**
     * The Account.
     */
    @ManyToOne(targetEntity = AccountModel.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id", nullable = false, unique = false)
    private AccountModel account;

    /**
     * Instantiates a new Role model.
     */
    RoleModel() {
        super(RoleModel.class);
    }

    /**
     * Default constructor.
     *
     * @param account the account
     * @param role    the role
     */
    public RoleModel(final AccountModel account, final Role role) {
        super(RoleModel.class);
        this.account = account;
        this.role = role;
    }

    /**
     * Gets role.
     *
     * @return the role
     */
    public Role getRole() {
        return this.role;
    }

    /**
     * Sets role.
     *
     * @param role the role
     */
    public void setRole(final Role role) {
        this.role = role;
    }

    /**
     * Gets account.
     *
     * @return the account
     */
    public AccountModel getAccount() {
        return this.account;
    }

    /**
     * Sets account.
     *
     * @param account the account
     */
    public void setAccount(final AccountModel account) {
        this.account = account;
    }
}
