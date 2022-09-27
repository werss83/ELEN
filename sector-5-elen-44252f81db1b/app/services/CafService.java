package services;

import entities.caf.ChildType;
import entities.caf.IncomeLimitsAmount;
import entities.caf.IncomeType;
import form.parent.onboarding.ChildrenForm;
import io.ebean.QueryIterator;
import models.caf.CafOptionModel;
import models.caf.entities.IncomeLimits;
import models.caf.entities.MonthlySupport;
import models.caf.enumetation.CafOptionKey;
import models.care.EffectiveCareModel;
import models.children.ChildrenModel;
import models.children.enumeration.ChildCategory;
import models.children.enumeration.UsualChildcare;
import models.parent.ParentModel;
import models.parent.ParentOptionModel;
import models.parent.enumeration.ParentOptionKey;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.date.DateChecker;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

/**
 * CafService.
 *
 * @author Pierre Adam
 * @since 20.07.20
 */
@Singleton
public class CafService {

    /**
     * The Logger.
     */
    protected final Logger logger;

    /**
     * The Usual childcares that invalidate monthly support.
     */
    private final List<UsualChildcare> usualChildcares = new ArrayList<UsualChildcare>() {{
        this.add(UsualChildcare.MATERNAL_ASSISTANCE);
        this.add(UsualChildcare.PRIVATE_NURSERY);
    }};

    /**
     * Instantiates a new Caf service.
     */
    @Inject
    public CafService() {
        this.logger = LoggerFactory.getLogger(CafService.class);
    }

    /**
     * Calculate monthly support long.
     *
     * @param parent the parent
     * @return the long
     */
    public Long calculateMonthlySupport(final ParentModel parent) {
        if (!parent.getOption(ParentOptionKey.PARENT_WORKING).getValueAsBoolean()) {
            return 0L;
        }
        if (parent.getChildren().stream().anyMatch(children -> this.usualChildcares.contains(children.getUsualChildcare()))) {
            return 0L;
        }

        final MonthlySupport monthlySupport = CafOptionModel.getOption(CafOptionKey.MONTHLY_SUPPORT).getAsMonthlySupport();
        final IncomeType incomeType = this.getIncomeType(parent);
        final ChildType youngestChild = this.getYoungestChildType(parent);
        final Boolean sharedCare = ParentOptionModel.getOption(parent, ParentOptionKey.SHARED_CARE).getValueAsBoolean();
        final Boolean singleParent = ParentOptionModel.getOption(parent, ParentOptionKey.SINGLE_PARENT).getValueAsBoolean();
        final Boolean disabledParent = ParentOptionModel.getOption(parent, ParentOptionKey.DISABLED_PARENT).getValueAsBoolean();
        final Boolean disabledChildBenefit = ParentOptionModel.getOption(parent, ParentOptionKey.DISABLED_CHILD_BENEFIT).getValueAsBoolean();

        boolean nightOrBankHoliday = false;
        final QueryIterator<EffectiveCareModel> iterator = EffectiveCareModel.find.query().where()
            .eq("bookedCare.parent", parent)
            .ge("startDate", DateTime.now().withDayOfMonth(1).withMillisOfDay(0))
            .lt("startDate", DateTime.now().withDayOfMonth(1).withMillisOfDay(0).plusMonths(1))
            .findIterate();
        while (iterator.hasNext()) {
            final EffectiveCareModel care = iterator.next();
            if (DateChecker.hasNightOrBankHoliday(care)) {
                nightOrBankHoliday = true;
                break;
            }
        }

        return monthlySupport.getSupportAmount(incomeType, youngestChild, sharedCare, singleParent, nightOrBankHoliday, disabledParent, disabledChildBenefit);
    }

    /**
     * Calculate monthly support long.
     *
     * @param parentWorking        the parent working
     * @param childrenForms        the children forms
     * @param childCategories      the child categories
     * @param incomeType           the income type
     * @param singleParent         the single parent
     * @param sharedCare           the shared care
     * @param disabledParent       the disabled parent
     * @param disabledChildBenefit the disabled child benefit
     * @return the long
     */
    public Long calculateMonthlySupport(final boolean parentWorking,
                                        final List<ChildrenForm> childrenForms,
                                        final List<ChildCategory> childCategories,
                                        final IncomeType incomeType,
                                        final Boolean singleParent,
                                        final Boolean sharedCare,
                                        final Boolean disabledParent,
                                        final Boolean disabledChildBenefit) {
        if (!parentWorking) {
            return 0L;
        }
        if (childrenForms.stream().anyMatch(form -> this.usualChildcares.contains(form.getChildcare()))) {
            return 0L;
        }

        final MonthlySupport monthlySupport = CafOptionModel.getOption(CafOptionKey.MONTHLY_SUPPORT).getAsMonthlySupport();
        final ChildType youngestChild = this.getYoungestChildType(childCategories);

        return monthlySupport.getSupportAmount(incomeType, youngestChild, sharedCare, singleParent, false, disabledParent, disabledChildBenefit);
    }

    /**
     * Gets youngest child type.
     *
     * @param parent the parent
     * @return the youngest child type
     */
    public ChildType getYoungestChildType(final ParentModel parent) {
        ChildType youngestChild = ChildType.OVER_6;

        for (final ChildrenModel child : parent.getChildren()) {
            if (child.getCategory() == ChildCategory.UNDER_6_YEARS) {
                youngestChild = ChildType.UNDER_6;
            } else if (child.getCategory() == ChildCategory.UNDER_3_YEARS) {
                return ChildType.UNDER_3;
            } else if (child.getCategory() == ChildCategory.BIRTHDAY) {
                final LocalDate birthDate = child.getBirthDate();

                if (birthDate.isAfter(DateTime.now().minusYears(3).toLocalDate())) {
                    return ChildType.UNDER_3;
                } else if (birthDate.isAfter(DateTime.now().minusYears(6).toLocalDate())) {
                    youngestChild = ChildType.UNDER_6;
                }
            }
        }
        return youngestChild;
    }

    /**
     * Gets youngest child type.
     *
     * @param childCategories the child categories
     * @return the youngest child type
     */
    public ChildType getYoungestChildType(final List<ChildCategory> childCategories) {
        ChildType youngestChild = ChildType.OVER_6;
        for (final ChildCategory childCategory : childCategories) {
            if (childCategory == ChildCategory.UNDER_6_YEARS) {
                youngestChild = ChildType.UNDER_6;
            } else if (childCategory == ChildCategory.UNDER_3_YEARS) {
                return ChildType.UNDER_3;
            }
        }
        return youngestChild;
    }

    /**
     * Gets income type.
     *
     * @param parent       the parent
     * @param annualIncome the annual income
     * @return the income type
     */
    public IncomeType getIncomeType(final ParentModel parent,
                                    final Long annualIncome) {
        final IncomeLimitsAmount incomeLimitsAmount = this.getIncomeLimitsAmount(parent);
        return incomeLimitsAmount.getIncomeType(annualIncome);
    }

    /**
     * Gets income type.
     *
     * @param annualIncome   the annual income
     * @param singleParent   the single parent
     * @param childrenNumber the children number
     * @return the income type
     */
    public IncomeType getIncomeType(final Long annualIncome,
                                    final Boolean singleParent,
                                    final Integer childrenNumber) {
        final IncomeLimitsAmount incomeLimitsAmount = this.getIncomeLimitsAmount(singleParent, childrenNumber);
        return incomeLimitsAmount.getIncomeType(annualIncome);
    }

    /**
     * Gets income type.
     *
     * @param parent the parent
     * @return the income type
     */
    public IncomeType getIncomeType(final ParentModel parent) {
        final ParentOptionModel incomeOption = ParentOptionModel.getOption(parent, ParentOptionKey.INCOME);
        final ParentOptionModel incomeGroupOption = ParentOptionModel.getOption(parent, ParentOptionKey.INCOME_GROUP);

        final Long annualIncome = incomeOption.getValueAsLong();
        if (annualIncome != 0L) {
            return this.getIncomeType(parent, annualIncome);
        } else {
            switch (incomeGroupOption.getValueAsIncomeGroup()) {
                case BELOW_MIN:
                    return IncomeType.LOW;
                case BELOW_MAX:
                    return IncomeType.MEDIUM;
                case ABOVE_MAX:
                    return IncomeType.HIGH;
            }
        }

        this.logger.error("Unable to determine the income type for the parent {}. Defaulting to IncomeType.LOW", parent.getUid());
        return IncomeType.LOW;
    }

    /**
     * Gets income limits amount for the given parent.
     *
     * @param parent the parent
     * @return the income limits for the given parent
     */
    public IncomeLimitsAmount getIncomeLimitsAmount(final ParentModel parent) {
        final Boolean singleParent = ParentOptionModel.getOption(parent, ParentOptionKey.SINGLE_PARENT).getValueAsBoolean();
        final int childrenNumber = ChildrenModel.find.query()
            .where()
            .eq("parent", parent)
            .findCount();
        return this.getIncomeLimitsAmount(singleParent, childrenNumber);
    }

    /**
     * Gets income limits amount.
     *
     * @param singleParent   the single parent
     * @param childrenNumber the children number
     * @return the income limits amount
     */
    public IncomeLimitsAmount getIncomeLimitsAmount(final Boolean singleParent,
                                                    final Integer childrenNumber) {
        final IncomeLimits incomeLimits = CafOptionModel.getOption(CafOptionKey.INCOME_LIMIT).getAsIncomeLimits();
        return incomeLimits.getIncomeLevel(childrenNumber, singleParent);
    }
}
