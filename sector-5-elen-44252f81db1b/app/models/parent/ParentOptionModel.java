package models.parent;

import io.ebean.ExpressionList;
import io.ebean.Finder;
import models.options.GenericOptionModel;
import models.parent.enumeration.ParentIncomeGroup;
import models.parent.enumeration.ParentOptionKey;

import javax.persistence.*;

/**
 * ParentOptionModel.
 *
 * @author Lucas Stadelmann
 * @since 20.06.08
 */
@Entity
@Table(name = "parent_option", uniqueConstraints = @UniqueConstraint(columnNames = {"parent_id", "opt_key"}))
public class ParentOptionModel extends GenericOptionModel<ParentOptionKey, ParentOptionModel> {

    /**
     * Helpers to request model.
     */
    public static final Finder<Long, ParentOptionModel> find = new Finder<>(ParentOptionModel.class);

    /**
     * The organization owned option.
     */
    @ManyToOne(targetEntity = ParentModel.class)
    @JoinColumn(name = "parent_id", referencedColumnName = "id", nullable = false, unique = false)
    private ParentModel parent;

    /**
     * Instantiates a new Parent option model.
     */
    ParentOptionModel() {
        super(ParentOptionKey.class, ParentOptionModel.class, null);
    }

    /**
     * Instantiates a new Parent option model.
     *
     * @param key    the key
     * @param parent the parent
     */
    public ParentOptionModel(final ParentOptionKey key,
                             final ParentModel parent) {
        super(ParentOptionKey.class, ParentOptionModel.class, key);
        this.parent = parent;
    }

    /**
     * Apply a where close on the key.
     *
     * @param parent the parent
     * @param key    the key
     * @return The query with the where close condition
     */
    public static ExpressionList<ParentOptionModel> whereKey(final ParentModel parent,
                                                             final ParentOptionKey key) {
        final ExpressionList<ParentOptionModel> query = ParentOptionModel.find.query()
            .where().eq("parent", parent);
        return whereKey(query, key);
    }

    /**
     * Apply a where close on the key.
     *
     * @param query the query
     * @param key   the key
     * @return The query with the where close condition
     */
    public static ExpressionList<ParentOptionModel> whereKey(final ExpressionList<ParentOptionModel> query,
                                                             final ParentOptionKey key) {
        return whereKey(ParentOptionKey.class, query, key);
    }

    /**
     * Gets option.
     *
     * @param parent the parent
     * @param key    the key
     * @return the option
     */
    public static ParentOptionModel getOption(final ParentModel parent,
                                              final ParentOptionKey key) {
        final ParentOptionModel option = ParentOptionModel.whereKey(parent, key)
            .setMaxRows(1).findOne();
        if (option != null) {
            return option;
        } else {
            return new ParentOptionModel(key, parent);
        }
    }

    /**
     * Gets parent.
     *
     * @return the parent
     */
    public ParentModel getParent() {
        return this.parent;
    }

    /**
     * Sets parent.
     *
     * @param parent the parent
     */
    public void setParent(final ParentModel parent) {
        this.parent = parent;
    }

    /**
     * Get the value as ParentIncomeGroup
     *
     * @return the value as income group
     */
    public ParentIncomeGroup getValueAsIncomeGroup() {
        return this.getValueAsEnum(ParentIncomeGroup.class);
    }

    /**
     * Set the value as ParentIncomeGroup
     *
     * @param incomeGroup the income group
     */
    public void setValueAsIncomeGroup(final ParentIncomeGroup incomeGroup) {
        this.setValueAsEnum(ParentIncomeGroup.class, incomeGroup);
    }
}
