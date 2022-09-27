package models.sitter;

import io.ebean.ExpressionList;
import io.ebean.Finder;
import models.options.GenericOptionModel;
import models.sitter.enumeration.SitterOptionKey;

import javax.persistence.*;

/**
 * SitterOptionModel.
 *
 * @author Lucas Stadelmann
 * @since 20.06.14
 */
@Entity
@Table(name = "sitter_option", uniqueConstraints = @UniqueConstraint(columnNames = {"sitter_id", "opt_key"}))
public class SitterOptionModel extends GenericOptionModel<SitterOptionKey, SitterOptionModel> {

    /**
     * Helpers to request model.
     */
    public static final Finder<Long, SitterOptionModel> find = new Finder<>(SitterOptionModel.class);

    /**
     * The organization owned option.
     */
    @ManyToOne(targetEntity = SitterModel.class)
    @JoinColumn(name = "sitter_id", referencedColumnName = "id", nullable = false, unique = false)
    private SitterModel sitter;

    /**
     * Instantiates a new Sitter option model.
     */
    public SitterOptionModel() {
        super(SitterOptionKey.class, SitterOptionModel.class, null);
    }

    /**
     * Instantiates a new Sitter option model.
     *
     * @param key    the key
     * @param sitter the sitter
     */
    public SitterOptionModel(final SitterOptionKey key,
                             final SitterModel sitter) {
        super(SitterOptionKey.class, SitterOptionModel.class, key);
        this.sitter = sitter;
    }

    /**
     * Apply a where close on the key.
     *
     * @param query the query
     * @param key   the key
     * @return The query with the where close condition
     */
    public static ExpressionList<SitterOptionModel> whereKey(final ExpressionList<SitterOptionModel> query,
                                                             final SitterOptionKey key) {
        return whereKey(SitterOptionKey.class, query, key);
    }

    /**
     * Gets sitter.
     *
     * @return the sitter
     */
    public SitterModel getSitter() {
        return this.sitter;
    }

    /**
     * Sets sitter.
     *
     * @param sitter the sitter
     */
    public void setSitter(final SitterModel sitter) {
        this.sitter = sitter;
    }
}
