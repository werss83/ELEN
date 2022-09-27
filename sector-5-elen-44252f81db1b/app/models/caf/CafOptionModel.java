package models.caf;

import io.ebean.Finder;
import models.caf.entities.IncomeLimits;
import models.caf.entities.MonthlySupport;
import models.caf.enumetation.CafOptionKey;
import models.options.GenericOptionModel;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * CafOption.
 *
 * @author Pierre Adam
 * @since 20.07.23
 */
@Entity
@Table(name = "caf_option", uniqueConstraints = @UniqueConstraint(columnNames = {"opt_key"}))
public class CafOptionModel extends GenericOptionModel<CafOptionKey, CafOptionModel> {

    /**
     * Helpers to request model.
     */
    public static final Finder<Long, CafOptionModel> find = new Finder<>(CafOptionModel.class);

    /**
     * Constructor.
     */
    CafOptionModel() {
        super(CafOptionKey.class, CafOptionModel.class, null);
    }

    /**
     * Constructor.
     *
     * @param key the key
     */
    public CafOptionModel(final CafOptionKey key) {
        super(CafOptionKey.class, CafOptionModel.class, key);
    }

    /**
     * Gets option.
     *
     * @param key the key
     * @return the option
     */
    public static CafOptionModel getOption(final CafOptionKey key) {
        final CafOptionModel option = CafOptionModel.whereKey(CafOptionKey.class, CafOptionModel.find, key)
            .setMaxRows(1).findOne();
        if (option != null) {
            return option;
        } else {
            return new CafOptionModel(key);
        }
    }

    /**
     * Gets as income limits.
     *
     * @return the as income limits
     */
    public IncomeLimits getAsIncomeLimits() {
        return this.getValueAsObject(IncomeLimits.class);
    }

    /**
     * Sets as income limits.
     *
     * @param incomeLimits the income limits
     */
    public void setAsIncomeLimits(final IncomeLimits incomeLimits) {
        this.setValueAsObject(IncomeLimits.class, incomeLimits);
    }

    /**
     * Gets as monthly support.
     *
     * @return the as monthly support
     */
    public MonthlySupport getAsMonthlySupport() {
        return this.getValueAsObject(MonthlySupport.class);
    }

    /**
     * Sets as monthly support.
     *
     * @param monthlySupport the monthly support
     */
    public void setAsMonthlySupport(final MonthlySupport monthlySupport) {
        this.setValueAsObject(MonthlySupport.class, monthlySupport);
    }
}
