package models.parent;

import models.BaseCriteriaModel;

import javax.persistence.*;

/**
 * ParentCriteriaModel.
 *
 * @author Lucas Stadelmann
 * @since 20.07.03
 */
@Entity
@Table(name = "parent_criteria")
public class ParentCriteriaModel extends BaseCriteriaModel<ParentCriteriaModel> {

    /**
     * The Parent.
     */
    @OneToOne(targetEntity = ParentModel.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_id", nullable = false, unique = true)
    private ParentModel parent;

    /**
     * Instantiates a new Parent preference model.
     */
    public ParentCriteriaModel() {
        super(ParentCriteriaModel.class);
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
}
