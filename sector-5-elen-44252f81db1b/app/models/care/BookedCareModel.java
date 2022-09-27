package models.care;

import io.ebean.Finder;
import models.BaseModel;
import models.care.enumeration.BookedCareType;
import models.children.ChildrenModel;
import models.parent.ParentModel;
import org.joda.time.DateTime;
import org.joda.time.Duration;

import javax.persistence.*;
import java.util.List;

/**
 * BookedCareModel.
 *
 * @author Lucas Stadelmann
 * @since 20.06.05
 */
@Entity
@Table(name = "booked_care")
public class BookedCareModel extends BaseModel<BookedCareModel> {

    /**
     * Helpers to request model.
     */
    public static final Finder<Long, BookedCareModel> find = new Finder<>(BookedCareModel.class);

    /**
     * The Type.
     */
    @Column(name = "booked_care_type", nullable = false, unique = false)
    private BookedCareType type;

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
    @ManyToOne(targetEntity = ParentModel.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", nullable = false, unique = false)
    private ParentModel parent;

    /**
     * The Children.
     */
    @ManyToMany(targetEntity = ChildrenModel.class, fetch = FetchType.LAZY)
    private List<ChildrenModel> children;

    /**
     * The Care preference.
     */
    @OneToOne(targetEntity = CareCriteriaModel.class, mappedBy = "bookedCare", fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private CareCriteriaModel careCriteria;

    /**
     * The Effective cares.
     */
    @OneToMany(targetEntity = EffectiveCareModel.class, mappedBy = "bookedCare", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private List<EffectiveCareModel> effectiveCares;

    /**
     * Instantiates a new Booked care model.
     */
    BookedCareModel() {
        super(BookedCareModel.class);
    }

    /**
     * Instantiates a new Booked care model.
     *
     * @param type           the type
     * @param startDate      the start date
     * @param parent         the parent
     * @param carePreference the care preference
     */
    public BookedCareModel(final BookedCareType type,
                           final DateTime startDate,
                           final Duration duration,
                           final ParentModel parent,
                           final CareCriteriaModel carePreference) {
        super(BookedCareModel.class);
        this.type = type;
        this.startDate = startDate;
        this.duration = duration.getMillis();
        this.parent = parent;
        this.careCriteria = carePreference;
    }

    /**
     * Gets type.
     *
     * @return the type
     */
    public BookedCareType getType() {
        return this.type;
    }

    /**
     * Sets type.
     *
     * @param type the type
     */
    public void setType(final BookedCareType type) {
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
     * Gets children.
     *
     * @return the children
     */
    public List<ChildrenModel> getChildren() {
        return this.children;
    }

    /**
     * Sets children.
     *
     * @param children the children
     */
    public void setChildren(final List<ChildrenModel> children) {
        this.children = children;
    }

    /**
     * Add child.
     *
     * @param child the child
     * @return the boolean
     */
    public boolean addChild(final ChildrenModel child) {
        return this.children.add(child);
    }

    /**
     * Remove child boolean.
     *
     * @param child the child
     * @return the boolean
     */
    public boolean removeChild(final ChildrenModel child) {
        return this.children.remove(child);
    }

    /**
     * Gets care preference.
     *
     * @return the care preference
     */
    public CareCriteriaModel getCareCriteria() {
        return this.careCriteria;
    }

    /**
     * Sets care preference.
     *
     * @param careCriteria the care preference
     */
    public void setCareCriteria(final CareCriteriaModel careCriteria) {
        this.careCriteria = careCriteria;
    }

    /**
     * Gets effective cares.
     *
     * @return the effective cares
     */
    public List<EffectiveCareModel> getEffectiveCares() {
        return this.effectiveCares;
    }

    /**
     * Sets effective cares.
     *
     * @param effectiveCares the effective cares
     */
    public void setEffectiveCares(final List<EffectiveCareModel> effectiveCares) {
        this.effectiveCares = effectiveCares;
    }

    /**
     * Add effective care boolean.
     *
     * @param effectiveCare the effective care
     * @return the boolean
     */
    public boolean addEffectiveCare(final EffectiveCareModel effectiveCare) {
        return this.effectiveCares.add(effectiveCare);
    }

    /**
     * Remove effective care boolean.
     *
     * @param effectiveCare the effective care
     * @return the boolean
     */
    public boolean removeEffectiveCare(final EffectiveCareModel effectiveCare) {
        return this.effectiveCares.remove(effectiveCare);
    }
}
