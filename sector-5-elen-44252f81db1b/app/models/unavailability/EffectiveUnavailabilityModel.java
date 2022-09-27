package models.unavailability;

import io.ebean.Finder;
import models.BaseModel;
import models.care.EffectiveCareModel;
import org.joda.time.DateTime;

import javax.persistence.*;

/**
 * EffectiveUnavailabilityModel.
 *
 * @author Lucas Stadelmann
 * @since 20.06.08
 */
@Entity
@Table(name = "effective_unavailability")
public class EffectiveUnavailabilityModel extends BaseModel<EffectiveUnavailabilityModel> {

    /**
     * Helpers to request model.
     */
    public static final Finder<Long, EffectiveCareModel> find = new Finder<>(EffectiveCareModel.class);

    /**
     * The Start date.
     */
    @Column(name = "start_date", nullable = false, unique = false)
    private DateTime startDate;

    /**
     * The End date.
     */
    @Column(name = "end_date", nullable = true, unique = false)
    private DateTime endDate;

    /**
     * The Planned unavailability.
     */
    @ManyToOne(targetEntity = PlannedUnavailabilityModel.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "planned_unavailability_id", nullable = false, unique = false)
    private PlannedUnavailabilityModel plannedUnavailability;

    /**
     * The Effective care.
     */
    @OneToOne(targetEntity = EffectiveCareModel.class, mappedBy = "effectiveUnavailability", fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    private EffectiveCareModel effectiveCare;

    /**
     * Instantiates a new Effective unavailability model.
     */
    EffectiveUnavailabilityModel() {
        super(EffectiveUnavailabilityModel.class);
    }

    /**
     * Instantiates a new Effective unavailability model.
     *
     * @param startDate             the start date
     * @param endDate               the end date
     * @param plannedUnavailability the planned unavailability
     */
    public EffectiveUnavailabilityModel(final DateTime startDate,
                                        final DateTime endDate,
                                        final PlannedUnavailabilityModel plannedUnavailability) {
        super(EffectiveUnavailabilityModel.class);
        this.startDate = startDate;
        this.endDate = endDate;
        this.plannedUnavailability = plannedUnavailability;
    }

    /**
     * Gets start date.
     *
     * @return the start date
     */
    public DateTime getStartDate() {
        return this.startDate;
    }

    /**
     * Sets start date.
     *
     * @param startDate the start date
     */
    public void setStartDate(final DateTime startDate) {
        this.startDate = startDate;
    }

    /**
     * Gets end date.
     *
     * @return the end date
     */
    public DateTime getEndDate() {
        return this.endDate;
    }

    /**
     * Sets end date.
     *
     * @param endDate the end date
     */
    public void setEndDate(final DateTime endDate) {
        this.endDate = endDate;
    }

    /**
     * Gets planned unavailability.
     *
     * @return the planned unavailability
     */
    public PlannedUnavailabilityModel getPlannedUnavailability() {
        return this.plannedUnavailability;
    }

    /**
     * Sets planned unavailability.
     *
     * @param plannedUnavailability the planned unavailability
     */
    public void setPlannedUnavailability(final PlannedUnavailabilityModel plannedUnavailability) {
        this.plannedUnavailability = plannedUnavailability;
    }

    /**
     * Gets effective care.
     *
     * @return the effective care
     */
    public EffectiveCareModel getEffectiveCare() {
        return this.effectiveCare;
    }

    /**
     * Sets effective care.
     *
     * @param effectiveCare the effective care
     */
    public void setEffectiveCare(final EffectiveCareModel effectiveCare) {
        this.effectiveCare = effectiveCare;
    }
}
