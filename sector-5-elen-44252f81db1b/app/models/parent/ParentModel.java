package models.parent;

import io.ebean.Finder;
import models.BaseModel;
import models.account.AccountModel;
import models.care.BookedCareModel;
import models.children.ChildrenModel;
import models.children.enumeration.ChildCategory;
import models.parent.enumeration.ParentOptionKey;
import org.apache.commons.lang3.RandomStringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

/**
 * ParentModel.
 *
 * @author Lucas Stadelmann
 * @since 20.06.05
 */
@Entity
@Table(name = "parent")
public class ParentModel extends BaseModel<ParentModel> {

    /**
     * Helpers to request model.
     */
    public static final Finder<Long, ParentModel> find = new Finder<>(ParentModel.class);

    /**
     * The Account.
     */
    @OneToOne(targetEntity = AccountModel.class, fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "account_id", nullable = false, unique = true)
    private AccountModel account;

    /**
     * The Children.
     */
    @OneToMany(targetEntity = ChildrenModel.class, mappedBy = "parent", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<ChildrenModel> children;

    /**
     * The Sponsor code.
     */
    @Size(max = 16)
    @Column(name = "sponsor_code", nullable = false, unique = true)
    private String sponsorCode;

    /**
     * The Sponsor.
     */
    @ManyToOne(targetEntity = ParentModel.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "sponsor_id", nullable = true, unique = false)
    private ParentModel sponsor;

    /**
     * The Sponsored parents.
     */
    @OneToMany(targetEntity = ParentModel.class, mappedBy = "sponsor", fetch = FetchType.LAZY)
    private List<ParentModel> sponsoredParents;

    /**
     * The Booked cares.
     */
    @OneToMany(targetEntity = BookedCareModel.class, mappedBy = "parent", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private List<BookedCareModel> bookedCares;

    /**
     * The Parent preference.
     */
    @OneToOne(targetEntity = ParentCriteriaModel.class, mappedBy = "parent", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private ParentCriteriaModel parentCriteria;

    /**
     * The Parent options.
     */
    @OneToMany(targetEntity = ParentOptionModel.class, mappedBy = "parent", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<ParentOptionModel> options;

    /**
     * Instantiates a new Parent model.
     */
    ParentModel() {
        super(ParentModel.class);
    }

    /**
     * Instantiates a new Parent model.
     *
     * @param account        the account
     */
    public ParentModel(final AccountModel account) {
        super(ParentModel.class);
        this.sponsorCode = RandomStringUtils.randomAlphanumeric(10);
        this.account = account;
}

    /**
     * Instantiates a new Parent model.
     *
     * @param account        the account
     * @param parentCriteria the parent criteria
     */
    public ParentModel(final AccountModel account,
                       final ParentCriteriaModel parentCriteria) {
        this(account);
        this.parentCriteria = parentCriteria;
        this.parentCriteria.setParent(this);
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
     * Gets sponsor code.
     *
     * @return the sponsor code
     */
    public String getSponsorCode() {
        return this.sponsorCode;
    }

    /**
     * Sets sponsor code.
     *
     * @param sponsorCode the sponsor code
     */
    public void setSponsorCode(final String sponsorCode) {
        this.sponsorCode = sponsorCode;
    }

    /**
     * Generate new sponsor code.
     */
    public void generateNewSponsorCode() {
        this.sponsorCode = RandomStringUtils.randomAlphanumeric(10);
    }

    /**
     * Gets sponsor.
     *
     * @return the sponsor
     */
    public ParentModel getSponsor() {
        return this.sponsor;
    }

    /**
     * Sets sponsor.
     *
     * @param sponsor the sponsor
     */
    public void setSponsor(final ParentModel sponsor) {
        this.sponsor = sponsor;
    }

    /**
     * Gets sponsored parents.
     *
     * @return the sponsored parents
     */
    public List<ParentModel> getSponsoredParents() {
        return this.sponsoredParents;
    }

    /**
     * Sets sponsored parents.
     *
     * @param sponsoredParents the sponsored parents
     */
    public void setSponsoredParents(final List<ParentModel> sponsoredParents) {
        this.sponsoredParents = sponsoredParents;
    }

    /**
     * Add sponsored parent.
     *
     * @param parent the parent
     * @return the boolean
     */
    public boolean addSponsoredParent(final ParentModel parent) {
        return this.sponsoredParents.add(parent);
    }

    /**
     * Remove sponsored parent boolean.
     *
     * @param parent the parent
     * @return the boolean
     */
    public boolean removeSponsoredParent(final ParentModel parent) {
        return this.sponsoredParents.remove(parent);
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
     * @return the boolean
     */
    public boolean addBookedCare(final BookedCareModel bookedCare) {
        return this.bookedCares.add(bookedCare);
    }

    /**
     * Gets parent criteria.
     *
     * @return the parent criteria
     */
    public ParentCriteriaModel getParentCriteria() {
        return this.parentCriteria;
    }

    /**
     * Sets parent criteria.
     *
     * @param parentCriteria the parent criteria
     */
    public void setParentCriteria(final ParentCriteriaModel parentCriteria) {
        this.parentCriteria = parentCriteria;
    }

    /**
     * Gets options.
     *
     * @return the options
     */
    public List<ParentOptionModel> getOptions() {
        return this.options;
    }

    /**
     * Gets option.
     *
     * @param key the key
     * @return the option
     */
    public ParentOptionModel getOption(final ParentOptionKey key) {
        return ParentOptionModel.whereKey(
            ParentOptionModel.find.query()
                .where()
                .eq("parent", this),
            key
        ).findOneOrEmpty().orElse(new ParentOptionModel(key, this));
    }

    /**
     * Gets children of category.
     *
     * @param childCategory the child category
     * @return the children of category
     */
    public List<ChildrenModel> getChildrenOfCategory(final ChildCategory childCategory) {
        final List<ChildrenModel> childrenList = new ArrayList<>();

        if (childCategory.equals(ChildCategory.BIRTHDAY)) {
            return childrenList;
        }

        for (final ChildrenModel child : this.children) {
            if (child.getCategory().equals(childCategory)) {
                childrenList.add(child);
            } else if (child.getBirthDate() != null) {
                final LocalDate birthDate = child.getBirthDate();

                if (birthDate.isAfter(DateTime.now().minusYears(3).toLocalDate()) && childCategory.equals(ChildCategory.UNDER_3_YEARS)) {
                    childrenList.add(child);
                } else if (birthDate.isAfter(DateTime.now().minusYears(6).toLocalDate())
                    && birthDate.isBefore(DateTime.now().minusYears(3).toLocalDate())
                    && childCategory.equals(ChildCategory.UNDER_6_YEARS)) {
                    childrenList.add(child);
                } else if (birthDate.isBefore(DateTime.now().minusYears(6).toLocalDate())
                    && childCategory.equals(ChildCategory.OVER_6_YEARS)) {
                    childrenList.add(child);
                }
            }
        }

        return childrenList;
    }
}
