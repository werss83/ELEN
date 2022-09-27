package models.unavailability;

import io.ebean.Finder;
import models.BaseModel;
import models.sitter.SitterModel;
import models.unavailability.enumeration.PlannedUnavailabilityType;
import org.joda.time.DateTime;
import org.joda.time.Duration;

import javax.persistence.*;
import java.util.List;

/**
 * PlannedUnavailabilityModel.
 *
 * @author Lucas Stadelmann
 * @since 20.06.05
 */
@Entity
@Table(name = "planned_unavailability")
public class PlannedUnavailabilityModel extends BaseModel<PlannedUnavailabilityModel> {

    /**
     * Helpers to request model.
     */
    public static final Finder<Long, PlannedUnavailabilityModel> find = new Finder<>(PlannedUnavailabilityModel.class);

    /**
     * The Type.
     */
    @Column(name = "sitter_unavailability_type", nullable = false, unique = false)
    private PlannedUnavailabilityType type;

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
     * The Duration.
     */
    @Column(name = "duration", nullable = false, unique = false)
    private long duration;

    /**
     * The Parent.
     */
    @ManyToOne(targetEntity = SitterModel.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "sitter_id", nullable = false, unique = false)
    private SitterModel sitter;

    /**
     * The Effective unavailabilities.
     */
    @OneToMany(targetEntity = EffectiveUnavailabilityModel.class, mappedBy = "plannedUnavailability", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private List<EffectiveUnavailabilityModel> effectiveUnavailabilities;

    /**
     * Instantiates a new Planned unavailability model.
     */
    PlannedUnavailabilityModel() {
        super(PlannedUnavailabilityModel.class);
    }

    /**
     * Instantiates a new Sitter unavailability model.
     *
     * @param type      the type
     * @param startDate the start date
     * @param duration  the duration
     * @param sitter    the sitter
     */
    public PlannedUnavailabilityModel(final PlannedUnavailabilityType type,
                                      final DateTime startDate,
                                      final Duration duration,
                                      final SitterModel sitter) {
        super(PlannedUnavailabilityModel.class);
        this.type = type;
        this.startDate = startDate;
        this.duration = duration.getMillis();
        this.sitter = sitter;
    }

    /**
     * Gets type.
     *
     * @return the type
     */
    public PlannedUnavailabilityType getType() {
        return this.type;
    }

    /**
     * Sets type.
     *
     * @param type the type
     */
    public void setType(final PlannedUnavailabilityType type) {
        this.type = type;
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
     * Gets duration.
     *
     * @return the duration
     */
    public Duration getDuration() {
        return new Duration(this.duration);
    }

    /**
     * Sets duration.
     *
     * @param duration the duration
     */
    public void setDuration(final Duration duration) {
        this.duration = duration.getMillis();
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

    /**
     * Gets effective unavailabilities.
     *
     * @return the effective unavailabilities
     */
    public List<EffectiveUnavailabilityModel> getEffectiveUnavailabilities() {
        return this.effectiveUnavailabilities;
    }

    /**
     * Sets effective unavailabilities.
     *
     * @param effectiveUnavailabilities the effective unavailabilities
     */
    public void setEffectiveUnavailabilities(final List<EffectiveUnavailabilityModel> effectiveUnavailabilities) {
        this.effectiveUnavailabilities = effectiveUnavailabilities;
    }

    /**
     * Add effective unavailability boolean.
     *
     * @param effectiveUnavailability the effective unavailability
     * @return the boolean
     */
    public boolean addEffectiveUnavailability(final EffectiveUnavailabilityModel effectiveUnavailability) {
        return this.effectiveUnavailabilities.add(effectiveUnavailability);
    }

    /**
     * Remove effective unavailability boolean.
     *
     * @param effectiveUnavailability the effective unavailability
     * @return the boolean
     */
    public boolean removeEffectiveUnavailability(final EffectiveUnavailabilityModel effectiveUnavailability) {
        return this.effectiveUnavailabilities.remove(effectiveUnavailability);
    }
}
