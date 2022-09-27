package models.care;

import io.ebean.Finder;
import models.BaseModel;
import models.care.enumeration.EffectiveCareStatus;
import models.unavailability.EffectiveUnavailabilityModel;
import org.joda.time.DateTime;
import org.joda.time.Duration;

import javax.persistence.*;

/**
 * EffectiveCareModel.
 *
 * @author Lucas Stadelmann
 * @since 20.06.05
 */
@Entity
@Table(name = "effective_care")
public class EffectiveCareModel extends BaseModel<EffectiveCareModel> {

    /**
     * Helpers to request model.
     */
    public static final Finder<Long, EffectiveCareModel> find = new Finder<>(EffectiveCareModel.class);

    /**
     * The Status.
     */
    @Column(name = "status", nullable = false, unique = false)
    private EffectiveCareStatus status;

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
     * The Booked care.
     */
    @ManyToOne(targetEntity = BookedCareModel.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "booked_care_id", nullable = false, unique = false)
    private BookedCareModel bookedCare;

    /**
     * The Effective unavailability.
     */
    @OneToOne(targetEntity = EffectiveUnavailabilityModel.class, fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "effective_unavailability_id", nullable = true, unique = true)
    private EffectiveUnavailabilityModel effectiveUnavailability;

    /**
     * Instantiates a new Effective care model.
     */
    EffectiveCareModel() {
        super(EffectiveCareModel.class);
    }

    /**
     * Instantiates a new Effective care model.
     *
     * @param startDate  the start date
     * @param endDate    the end date
     * @param bookedCare the booked care
     */
    public EffectiveCareModel(final DateTime startDate,
                              final DateTime endDate,
                              final BookedCareModel bookedCare) {
        super(EffectiveCareModel.class);
        this.status = EffectiveCareStatus.PLANNED;
        this.startDate = startDate;
        this.endDate = endDate;
        this.bookedCare = bookedCare;
    }

    /**
     * Gets status.
     *
     * @return the status
     */
    public EffectiveCareStatus getStatus() {
        return this.status;
    }

    /**
     * Sets status.
     *
     * @param status the status
     */
    public void setStatus(final EffectiveCareStatus status) {
        this.status = status;
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
        return new Duration(this.endDate.getMillis() - this.startDate.getMillis());
    }

    /**
     * Gets booked care.
     *
     * @return the booked care
     */
    public BookedCareModel getBookedCare() {
        return this.bookedCare;
    }

    /**
     * Sets booked care.
     *
     * @param bookedCare the booked care
     */
    public void setBookedCare(final BookedCareModel bookedCare) {
        this.bookedCare = bookedCare;
    }

    /**
     * Gets effective unavailability.
     *
     * @return the effective unavailability
     */
    public EffectiveUnavailabilityModel getEffectiveUnavailability() {
        return this.effectiveUnavailability;
    }

    /**
     * Sets effective unavailability.
     *
     * @param effectiveUnavailability the effective unavailability
     */
    public void setEffectiveUnavailability(final EffectiveUnavailabilityModel effectiveUnavailability) {
        this.effectiveUnavailability = effectiveUnavailability;
    }
}
