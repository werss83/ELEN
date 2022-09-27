package models.children;

import io.ebean.Finder;
import models.BaseModel;
import models.care.BookedCareModel;
import models.children.enumeration.ChildCategory;
import models.children.enumeration.UsualChildcare;
import models.parent.ParentModel;
import org.joda.time.LocalDate;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * ChildrenModel.
 *
 * @author Lucas Stadelmann
 * @since 20.06.05
 */
@Entity
@Table(name = "children")
public class ChildrenModel extends BaseModel<ChildrenModel> {

    /**
     * Helpers to request model.
     */
    public static final Finder<Long, ChildrenModel> find = new Finder<>(ChildrenModel.class);

    /**
     * The First name.
     */
    @Size(max = 64)
    @Column(name = "first_name", nullable = true, unique = false)
    private String firstName;

    /**
     * The Category.
     */
    @Column(name = "category", nullable = false, unique = false)
    private ChildCategory category;

    /**
     * The Birth date.
     */
    @Column(name = "birth_date", nullable = true, unique = false)
    private LocalDate birthDate;

    /**
     * The Parent.
     */
    @ManyToOne(targetEntity = ParentModel.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_id", nullable = false, unique = false)
    private ParentModel parent;

    /**
     * The Booked cares.
     */
    @ManyToMany(targetEntity = BookedCareModel.class, fetch = FetchType.LAZY)
    @JoinTable(name = "children_has_booked_care")
    private List<BookedCareModel> bookedCares;

    /**
     * The Usual childcare.
     */
    @Column(name = "usual_childcare", nullable = true, unique = false)
    private UsualChildcare usualChildcare;

    /**
     * Instantiates a new Children model.
     */
    ChildrenModel() {
        super(ChildrenModel.class);
    }

    /**
     * Instantiates a new Children model.
     *
     * @param firstName the first name
     * @param birthDate the birth date
     * @param parent    the parent
     */
    public ChildrenModel(final String firstName,
                         final LocalDate birthDate,
                         final ParentModel parent) {
        super(ChildrenModel.class);
        this.firstName = firstName;
        this.category = ChildCategory.BIRTHDAY;
        this.birthDate = birthDate;
        this.parent = parent;
    }

    public ChildrenModel(final ChildCategory category,
                         final UsualChildcare usualChildcare,
                         final ParentModel parent) {
        super(ChildrenModel.class);
        this.category = category;
        this.usualChildcare = usualChildcare;
        this.parent = parent;
    }

    /**
     * Gets first name.
     *
     * @return the first name
     */
    public String getFirstName() {
        return this.firstName;
    }

    /**
     * Sets first name.
     *
     * @param firstName the first name
     */
    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets category.
     *
     * @return the category
     */
    public ChildCategory getCategory() {
        return this.category;
    }

    /**
     * Sets category.
     *
     * @param category the category
     */
    public void setCategory(final ChildCategory category) {
        this.category = category;
    }

    /**
     * Gets birth date.
     *
     * @return the birth date
     */
    public LocalDate getBirthDate() {
        return this.birthDate;
    }

    /**
     * Sets birth date.
     *
     * @param birthDate the birth date
     */
    public void setBirthDate(final LocalDate birthDate) {
        this.birthDate = birthDate;
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
     * Gets booked cares.
     *
     * @return the booked cares
     */
    public List<BookedCareModel> getBookedCares() {
        return this.bookedCares;
    }

    /**
     * Sets booked cares.
     *
     * @param bookedCares the booked cares
     */
    public void setBookedCares(final List<BookedCareModel> bookedCares) {
        this.bookedCares = bookedCares;
    }

    /**
     * Add booked care.
     *
     * @param bookedCare the booked care
     */
    public void addBookedCare(final BookedCareModel bookedCare) {
        this.bookedCares.add(bookedCare);
    }

    /**
     * Remove booked care boolean.
     *
     * @param bookedCare the booked care
     * @return the boolean
     */
    public boolean removeBookedCare(final BookedCareModel bookedCare) {
        return this.bookedCares.remove(bookedCare);
    }

    /**
     * Gets usual childcare.
     *
     * @return the usual childcare
     */
    public UsualChildcare getUsualChildcare() {
        return this.usualChildcare;
    }

    /**
     * Sets usual childcare.
     *
     * @param usualChildcare the usual childcare
     */
    public void setUsualChildcare(final UsualChildcare usualChildcare) {
        this.usualChildcare = usualChildcare;
    }
}
