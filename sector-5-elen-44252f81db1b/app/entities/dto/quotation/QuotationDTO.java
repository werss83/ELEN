package entities.dto.quotation;

import entities.caf.IncomeLimitsAmount;
import entities.caf.IncomeType;
import form.parent.BookedCareForm;
import form.parent.onboarding.CareForm;
import form.parent.onboarding.OnboardingForm;
import models.account.AccountModel;
import models.care.enumeration.BookedCareType;
import models.children.enumeration.ChildCategory;
import models.parent.ParentModel;
import models.parent.enumeration.ParentOptionKey;
import org.apache.commons.lang3.RandomStringUtils;
import org.joda.time.DateTime;
import play.i18n.Messages;
import services.CafService;
import services.PricingService;
import utils.capital.Capital;

import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.List;

/**
 * QuotationDTO.
 *
 * @author Lucas Stadelmann
 * @since 20.09.04
 */
public class QuotationDTO {

    /**
     * The Base duty rate.
     */
    static final long BASE_DUTY_RATE = 10;

    /**
     * The Minimum hour.
     */
    static final long MINIMUM_HOUR = 16;

    /**
     * The Minimum customer share rate.
     */
    static final long MINIMUM_CUSTOMER_SHARE_RATE = 15;

    /**
     * The Decimal format.
     */
    static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,##0.00");

    /**
     * The Messages.
     */
    private final Messages messages;

    /**
     * The Account.
     */
    private final AccountModel account;

    /**
     * The Capital name.
     */
    private final String capitalName;

    /**
     * The Total hour.
     */
    private final double totalHour;

    /**
     * The Less than minimum hour.
     */
    private final boolean lessThanMinimumHour;

    /**
     * The Children less than three.
     */
    private final int childrenLessThanThree;

    /**
     * The Children less than six.
     */
    private final int childrenLessThanSix;

    /**
     * The Children more than six.
     */
    private final int childrenMoreThanSix;

    /**
     * The Income type.
     */
    private final IncomeType incomeType;

    /**
     * The Care type.
     */
    private final BookedCareType careType;

    /**
     * The One time care.
     */
    private final CareForm oneTimeCare;

    /**
     * The Recurrent care.
     */
    private final List<CareForm> recurrentCare;

    /**
     * The Base cost duty free.
     */
    private final double costDutyFree;

    /**
     * The Base duty.
     */
    private final double dutyCost;

    /**
     * The Base cost including taxes.
     */
    private final double costIncludingTaxes;

    /**
     * The Cost including taxes after credit discount.
     */
    private final double costIncludingTaxesAfterCreditDiscount;

    /**
     * The Cost including taxes after cmg discount.
     */
    private final double costIncludingTaxesAfterCmgDiscount;

    /**
     * The Cost including taxes after cmg and credit discount.
     */
    private final double costIncludingTaxesAfterCmgAndCreditDiscount;

    /**
     * The Minimum hour cost duty free.
     */
    private final double minimumHourCostDutyFree;

    /**
     * The Minimum hour duty cost.
     */
    private final double minimumHourDutyCost;

    /**
     * The Minimum hour cost including taxes before cmg and credit discount.
     */
    private final double minimumHourCostIncludingTaxesBeforeCmgAndCreditDiscount;

    /**
     * The Minimum hour cost including taxes after cmg discount.
     */
    private final double minimumHourCostIncludingTaxesAfterCmgDiscount;

    /**
     * The Minimum hour cost including taxes after cmg and credit discount.
     */
    private final double minimumHourCostIncludingTaxesAfterCmgAndCreditDiscount;

    /**
     * The Customer share.
     */
    private final double customerShare;

    /**
     * The Tax share.
     */
    private final double taxShare;

    /**
     * The Customer share per hour.
     */
    private final double customerSharePerHour;

    /**
     * The Is subsidize.
     */
    private final boolean isSubsidize;

    /**
     * The Subsidize amount.
     */
    private final long subsidizeAmount;

    /**
     * The Income limits amount.
     */
    private final IncomeLimitsAmount incomeLimitsAmount;

    /**
     * The Caf share.
     */
    private final double cafShare;

    /**
     * The Single parent.
     */
    private final boolean singleParent;

    /**
     * The Handicap benefit.
     */
    private final boolean handicapBenefit;

    /**
     * The Parent working.
     */
    private final boolean parentWorking;

    /**
     * Instantiates a new Quotation dto.
     *
     * @param singleParent          the single parent
     * @param handicapBenefit       the handicap benefit
     * @param parentWorking         the parent working
     * @param childrenLessThanThree the children less than three
     * @param childrenLessThanSix   the children less than six
     * @param childrenMoreThanSix   the children more than six
     * @param incomeType            the income type
     * @param careType              the care type
     * @param oneTimeCare           the one time care
     * @param recurrentCare         the recurrent care
     * @param account               the account
     * @param cost                  the cost
     * @param subsidizeAmount       the subsidize amount
     * @param cafService            the caf service
     * @param messages              the messages
     */
    private QuotationDTO(final boolean singleParent,
                         final boolean handicapBenefit,
                         final boolean parentWorking,
                         final int childrenLessThanThree,
                         final int childrenLessThanSix,
                         final int childrenMoreThanSix,
                         final IncomeType incomeType,
                         final BookedCareType careType,
                         final CareForm oneTimeCare,
                         final List<CareForm> recurrentCare,
                         final AccountModel account,
                         final double cost,
                         final long subsidizeAmount,
                         final CafService cafService,
                         final Messages messages) {
        this.singleParent = singleParent;
        this.handicapBenefit = handicapBenefit;
        this.parentWorking = parentWorking;

        this.childrenLessThanThree = childrenLessThanThree;
        this.childrenLessThanSix = childrenLessThanSix;
        this.childrenMoreThanSix = childrenMoreThanSix;
        this.incomeType = incomeType;
        this.careType = careType;
        this.oneTimeCare = oneTimeCare;
        if (recurrentCare != null) {
            recurrentCare.sort(Comparator.comparing(c -> c.getDate().getMillis()));
        }
        this.recurrentCare = recurrentCare;

        this.account = account;
        this.capitalName = Capital.resolveCapital(account.getShortZipCode()).getCapitalName();

        this.costIncludingTaxes = cost;
        this.costIncludingTaxesAfterCreditDiscount = this.costIncludingTaxes / 2;
        this.dutyCost = this.costIncludingTaxes - this.costIncludingTaxes * 100 / (100 + QuotationDTO.BASE_DUTY_RATE);
        this.costDutyFree = this.costIncludingTaxes - this.dutyCost;

        this.minimumHourCostIncludingTaxesBeforeCmgAndCreditDiscount = QuotationDTO.MINIMUM_HOUR * PricingService.FULL_HOUR_COST;
        this.minimumHourDutyCost = this.minimumHourCostIncludingTaxesBeforeCmgAndCreditDiscount
            - this.minimumHourCostIncludingTaxesBeforeCmgAndCreditDiscount * 100 / (100 + QuotationDTO.BASE_DUTY_RATE);
        this.minimumHourCostDutyFree = this.minimumHourCostIncludingTaxesBeforeCmgAndCreditDiscount - this.minimumHourDutyCost;

        if (this.isOneTimeCare()) {
            this.totalHour = this.getOneTimeCare().getDurationInHours();
        } else {
            this.totalHour = this.getRecurrentCare().stream().mapToDouble(CareForm::getDurationInHours).sum() * 4;
        }

        if ((this.isOneTimeCare() && this.getOneTimeCare().getDurationInHours() < QuotationDTO.MINIMUM_HOUR)
            || this.isRecurrentCare() && this.getRecurrentCare().stream().mapToDouble(care -> care.getDurationInHours() * 4).sum() < QuotationDTO.MINIMUM_HOUR) {
            this.lessThanMinimumHour = true;
        } else {
            this.lessThanMinimumHour = false;
        }

        this.isSubsidize = subsidizeAmount > 0L;
        this.subsidizeAmount = subsidizeAmount;

        if (this.minimumHourCostIncludingTaxesBeforeCmgAndCreditDiscount * (100 - QuotationDTO.MINIMUM_CUSTOMER_SHARE_RATE) / 100 < this.subsidizeAmount) {
            this.minimumHourCostIncludingTaxesAfterCmgDiscount = this.minimumHourCostIncludingTaxesBeforeCmgAndCreditDiscount
                - this.minimumHourCostIncludingTaxesBeforeCmgAndCreditDiscount * (100 - QuotationDTO.MINIMUM_CUSTOMER_SHARE_RATE) / 100;
        } else {
            this.minimumHourCostIncludingTaxesAfterCmgDiscount = this.minimumHourCostIncludingTaxesBeforeCmgAndCreditDiscount - this.subsidizeAmount;
        }
        this.minimumHourCostIncludingTaxesAfterCmgAndCreditDiscount = this.minimumHourCostIncludingTaxesAfterCmgDiscount / 2;

        if (this.costIncludingTaxes * (100 - QuotationDTO.MINIMUM_CUSTOMER_SHARE_RATE) / 100 < this.subsidizeAmount) {
            this.costIncludingTaxesAfterCmgDiscount = this.costIncludingTaxes
                - this.costIncludingTaxes * (100 - QuotationDTO.MINIMUM_CUSTOMER_SHARE_RATE) / 100;
        } else {
            this.costIncludingTaxesAfterCmgDiscount = this.costIncludingTaxes - this.subsidizeAmount;
        }
        this.costIncludingTaxesAfterCmgAndCreditDiscount = this.costIncludingTaxesAfterCmgDiscount / 2;

        if (this.isSubsidize()) {
            if (this.isLessThanMinimumHour()) {
                this.customerShare = this.minimumHourCostIncludingTaxesAfterCmgAndCreditDiscount;
                this.taxShare = this.customerShare;
                this.cafShare = this.minimumHourCostIncludingTaxesBeforeCmgAndCreditDiscount - this.customerShare - this.taxShare;
            } else {
                this.customerShare = this.costIncludingTaxesAfterCmgAndCreditDiscount;
                this.taxShare = this.customerShare;
                this.cafShare = this.costIncludingTaxes - this.customerShare - this.taxShare;
            }
        } else {
            this.customerShare = 0;
            this.taxShare = 0;
            this.cafShare = 0;
        }

        this.customerSharePerHour = this.customerShare / this.getSubsidizeHours();

        this.incomeLimitsAmount = cafService.getIncomeLimitsAmount(singleParent, (this.childrenLessThanThree + this.childrenMoreThanSix + this.childrenMoreThanSix));
        this.messages = messages;
    }

    /**
     * Instantiates a new Quotation dto.
     *
     * @param parent          the parent
     * @param form            the form
     * @param cost            the cost
     * @param subsidizeAmount the subsidize amount
     * @param cafService      the caf service
     * @param messages        the messages
     */
    public QuotationDTO(final ParentModel parent,
                        final BookedCareForm form,
                        final double cost,
                        final long subsidizeAmount,
                        final CafService cafService,
                        final Messages messages) {
        this(
            parent.getOption(ParentOptionKey.SINGLE_PARENT).getValueAsBoolean(),
            parent.getOption(ParentOptionKey.DISABLED_PARENT).getValueAsBoolean() || parent.getOption(ParentOptionKey.DISABLED_CHILD_BENEFIT).getValueAsBoolean(),
            parent.getOption(ParentOptionKey.PARENT_WORKING).getValueAsBoolean(),
            parent.getChildrenOfCategory(ChildCategory.UNDER_3_YEARS).size(),
            parent.getChildrenOfCategory(ChildCategory.UNDER_6_YEARS).size(),
            parent.getChildrenOfCategory(ChildCategory.OVER_6_YEARS).size(),
            cafService.getIncomeType(parent),
            form.getCareType(),
            form.getOneTimeCare(),
            form.getRecurrentCare(),
            parent.getAccount(),
            cost,
            subsidizeAmount,
            cafService,
            messages
        );
    }

    /**
     * Instantiates a new Quotation dto.
     *
     * @param form            the form
     * @param account         the account
     * @param cost            the cost
     * @param subsidizeAmount the subsidize amount
     * @param cafService      the caf service
     * @param messages        the messages
     */
    public QuotationDTO(final OnboardingForm form,
                        final AccountModel account,
                        final double cost,
                        final long subsidizeAmount,
                        final CafService cafService,
                        final Messages messages) {
        this(
            form.getSingleParent(),
            form.getDisabledParent() || form.getDisabledChildBenefit(),
            form.getParentWorking(),
            form.getChildrenOfCategory(ChildCategory.UNDER_3_YEARS),
            form.getChildrenOfCategory(ChildCategory.UNDER_6_YEARS),
            form.getChildrenOfCategory(ChildCategory.OVER_6_YEARS),
            form.getIncome(),
            form.getCareType(),
            form.getOneTimeCare(),
            form.getRecurrentCare(),
            account,
            cost,
            subsidizeAmount,
            cafService,
            messages
        );
    }

    /**
     * Gets quotation number.
     *
     * @return the quotation number
     */
    public String getQuotationNumber() {
        return DateTime.now().toString("YYYY.MM.dd.").concat(RandomStringUtils.randomNumeric(4));
    }

    /**
     * Gets customer full name.
     *
     * @return the customer full name
     */
    public String getCustomerFullName() {
        return this.account.getFullName();
    }

    /**
     * Gets customer address.
     *
     * @return the customer address
     */
    public String getCustomerAddress() {
        return this.account.getAddress();
    }

    /**
     * Gets customer location.
     *
     * @return the customer location
     */
    public String getCustomerLocation() {
        return String.format("%s %s", this.account.getZipCode(), this.account.getCity());
    }

    /**
     * Gets capital name.
     *
     * @return the capital name
     */
    public String getCapitalName() {
        return this.capitalName;
    }

    /**
     * Gets all dependent children.
     *
     * @return the all dependent children
     */
    public int getAllDependentChildren() {
        return this.childrenLessThanThree + this.childrenLessThanSix + this.childrenMoreThanSix;
    }

    /**
     * Gets children less than three.
     *
     * @return the children less than three
     */
    public int getChildrenLessThanThree() {
        return this.childrenLessThanThree;
    }

    /**
     * Gets children less than six.
     *
     * @return the children less than six
     */
    public int getChildrenLessThanSix() {
        return this.childrenLessThanSix;
    }

    /**
     * Gets children more than six.
     *
     * @return the children more than six
     */
    public int getChildrenMoreThanSix() {
        return this.childrenMoreThanSix;
    }

    /**
     * Has children less than six boolean.
     *
     * @return the boolean
     */
    public boolean hasChildrenLessThanSix() {
        return (this.childrenLessThanThree + this.childrenLessThanSix) > 0;
    }

    /**
     * Gets income statement.
     *
     * @return the income statement
     */
    public String getIncomeStatement() {
        if (this.incomeType.equals(IncomeType.LOW)) {
            return this.messages.at("common.lowerThan.euro", this.getDecimalFormatValue(this.incomeLimitsAmount.getLowLimit()));
        } else if (this.incomeType.equals(IncomeType.MEDIUM)) {
            return this.messages.at("common.lowerThan.euro", this.getDecimalFormatValue(this.incomeLimitsAmount.getHighLimit()));
        } else {
            return this.messages.at("common.greaterThan.euro", this.getDecimalFormatValue(this.incomeLimitsAmount.getHighLimit()));
        }
    }

    /**
     * Is one time care boolean.
     *
     * @return the boolean
     */
    public boolean isOneTimeCare() {
        return this.careType.equals(BookedCareType.ONE_TIME);
    }

    /**
     * Is recurrent care boolean.
     *
     * @return the boolean
     */
    public boolean isRecurrentCare() {
        return this.careType.equals(BookedCareType.RECURRENT);
    }

    /**
     * Gets one time care.
     *
     * @return the one time care
     */
    public CareForm getOneTimeCare() {
        return this.oneTimeCare;
    }

    /**
     * Gets recurrent care.
     *
     * @return the recurrent care
     */
    public List<CareForm> getRecurrentCare() {
        return this.recurrentCare;
    }

    /**
     * Gets recurrent weekly hour.
     *
     * @return the recurrent weekly hour
     */
    public double getRecurrentWeeklyHour() {
        return this.recurrentCare.stream().mapToLong(CareForm::getDuration).sum() / (3600d * 1000d);
    }

    /**
     * Is less than minimum hour boolean.
     *
     * @return the boolean
     */
    public boolean isLessThanMinimumHour() {
        return this.lessThanMinimumHour;
    }

    /**
     * Gets total hour.
     *
     * @return the total hour
     */
    public double getTotalHour() {
        return this.totalHour;
    }

    /**
     * Gets cost duty free.
     *
     * @return the cost duty free
     */
    public String getCostDutyFree() {
        return this.getDecimalFormatValue(this.costDutyFree);
    }

    /**
     * Gets duty cost.
     *
     * @return the duty cost
     */
    public String getDutyCost() {
        return this.getDecimalFormatValue(this.dutyCost);
    }

    /**
     * Gets cost including taxes.
     *
     * @return the cost including taxes
     */
    public String getCostIncludingTaxes() {
        return this.getDecimalFormatValue(this.costIncludingTaxes);
    }

    /**
     * Gets cost including taxes after credit discount.
     *
     * @return the cost including taxes after credit discount
     */
    public String getCostIncludingTaxesAfterCreditDiscount() {
        return this.getDecimalFormatValue(this.costIncludingTaxesAfterCreditDiscount);
    }

    /**
     * Gets cost per hour including taxes after credit discount.
     *
     * @return the cost per hour including taxes after credit discount
     */
    public String getCostPerHourIncludingTaxesAfterCreditDiscount() {
        return this.getDecimalFormatValue(this.costIncludingTaxesAfterCreditDiscount / this.totalHour);
    }

    /**
     * Gets cost including taxes after cmg discount.
     *
     * @return the cost including taxes after cmg discount
     */
    public String getCostIncludingTaxesAfterCmgDiscount() {
        return this.getDecimalFormatValue(this.costIncludingTaxesAfterCmgDiscount);
    }

    /**
     * Gets cost including taxes after cmg and credit discount.
     *
     * @return the cost including taxes after cmg and credit discount
     */
    public String getCostIncludingTaxesAfterCmgAndCreditDiscount() {
        return this.getDecimalFormatValue(this.costIncludingTaxesAfterCmgAndCreditDiscount);
    }

    /**
     * Gets cost per hour including taxes after cmg and credit discount.
     *
     * @return the cost per hour including taxes after cmg and credit discount
     */
    public String getCostPerHourIncludingTaxesAfterCmgAndCreditDiscount() {
        return this.getDecimalFormatValue(this.costIncludingTaxesAfterCmgAndCreditDiscount / this.totalHour);
    }

    /**
     * Gets minimum hour cost duty free.
     *
     * @return the minimum hour cost duty free
     */
    public String getMinimumHourCostDutyFree() {
        return this.getDecimalFormatValue(this.minimumHourCostDutyFree);
    }

    /**
     * Gets minimum hour duty cost.
     *
     * @return the minimum hour duty cost
     */
    public String getMinimumHourDutyCost() {
        return this.getDecimalFormatValue(this.minimumHourDutyCost);
    }

    /**
     * Gets minimum hour cost including taxes before cmg and credit discount.
     *
     * @return the minimum hour cost including taxes before cmg and credit discount
     */
    public String getMinimumHourCostIncludingTaxesBeforeCmgAndCreditDiscount() {
        return this.getDecimalFormatValue(this.minimumHourCostIncludingTaxesBeforeCmgAndCreditDiscount);
    }

    /**
     * Gets minimum hour cost including taxes after cmg discount.
     *
     * @return the minimum hour cost including taxes after cmg discount
     */
    public String getMinimumHourCostIncludingTaxesAfterCmgDiscount() {
        return this.getDecimalFormatValue(this.minimumHourCostIncludingTaxesAfterCmgDiscount);
    }

    /**
     * Gets minimum hour cost including taxes after cmg and credit discount.
     *
     * @return the minimum hour cost including taxes after cmg and credit discount
     */
    public String getMinimumHourCostIncludingTaxesAfterCmgAndCreditDiscount() {
        return this.getDecimalFormatValue(this.minimumHourCostIncludingTaxesAfterCmgAndCreditDiscount);
    }

    /**
     * Gets minimum hour cost per hour including taxes after cmg and credit discount.
     *
     * @return the minimum hour cost per hour including taxes after cmg and credit discount
     */
    public String getMinimumHourCostPerHourIncludingTaxesAfterCmgAndCreditDiscount() {
        return this.getDecimalFormatValue(this.minimumHourCostIncludingTaxesAfterCmgAndCreditDiscount / QuotationDTO.MINIMUM_HOUR);
    }

    /**
     * Is subsidize boolean.
     *
     * @return the boolean
     */
    public boolean isSubsidize() {
        return this.isSubsidize;
    }

    /**
     * Gets subsidize amount.
     *
     * @return the subsidize amount
     */
    public String getSubsidizeAmount() {
        return this.getDecimalFormatValue(this.subsidizeAmount);
    }

    /**
     * Do less than minimum hours boolean.
     *
     * @return the boolean
     */
    public boolean doLessThanMinimumHours() {
        return this.isOneTimeCare() || this.getRecurrentCare().stream().mapToLong(care -> Math.round(care.getDurationInHours() * 4L)).sum() < MINIMUM_HOUR;
    }

    /**
     * Gets minimum hour.
     *
     * @return the minimum hour
     */
    public long getMinimumHour() {
        return QuotationDTO.MINIMUM_HOUR;
    }

    /**
     * Gets subsidize hours.
     *
     * @return the subsidize hours
     */
    public long getSubsidizeHours() {
        if (this.doLessThanMinimumHours()) {
            return MINIMUM_HOUR;
        } else {
            return this.getRecurrentCare().stream().mapToLong(care -> Math.round(care.getDurationInHours() * 4L)).sum();
        }
    }

    /**
     * Gets customer share.
     *
     * @return the customer share
     */
    public String getCustomerShare() {
        return this.getDecimalFormatValue(this.customerShare);
    }

    /**
     * Gets customer share per hour.
     *
     * @return the customer share per hour
     */
    public String getCustomerSharePerHour() {
        return this.getDecimalFormatValue(this.customerSharePerHour);
    }

    /**
     * Gets tax share.
     *
     * @return the tax share
     */
    public String getTaxShare() {
        return this.getDecimalFormatValue(this.taxShare);
    }

    /**
     * Gets caf share.
     *
     * @return the caf share
     */
    public String getCafShare() {
        return this.getDecimalFormatValue(this.cafShare);
    }

    /**
     * Gets full hour cost.
     *
     * @return the full hour cost
     */
    public String getFullHourCost() {
        return this.getDecimalFormatValue(PricingService.FULL_HOUR_COST);
    }

    /**
     * Gets reduce hour cost.
     *
     * @return the reduce hour cost
     */
    public String getReduceHourCost() {
        return this.getDecimalFormatValue(PricingService.REDUCE_HOUR_COST);
    }

    /**
     * Gets elen full hour cost.
     *
     * @return the elen full hour cost
     */
    public String getElenFullHourCost() {
        return this.getDecimalFormatValue(PricingService.ELEN_FULL_HOUR_COST);
    }

    /**
     * Gets elen reduce hour cost.
     *
     * @return the elen reduce hour cost
     */
    public String getElenReduceHourCost() {
        return this.getDecimalFormatValue(PricingService.ELEN_RECUCE_HOUR_COST);
    }

    /**
     * Gets duty hour cost.
     *
     * @return the duty hour cost
     */
    public String getDutyHourCost() {
        return this.getDecimalFormatValue(PricingService.DUTY_HOUR_COST);
    }

    /**
     * Gets payroll hour cost.
     *
     * @return the payroll hour cost
     */
    public String getPayrollHourCost() {
        return this.getDecimalFormatValue(PricingService.PAYROLL_HOUR_COST);
    }

    /**
     * Gets salary hour cost.
     *
     * @return the salary hour cost
     */
    public String getSalaryHourCost() {
        return this.getDecimalFormatValue(PricingService.SALARY_HOUR_COST);
    }

    /**
     * Gets kilometer charge cost.
     *
     * @return the kilometer charge cost
     */
    public String getKilometerChargeCost() {
        return this.getDecimalFormatValue(PricingService.KILOMETER_CHARGE_COST);
    }

    /**
     * Gets full hour limit.
     *
     * @return the full hour limit
     */
    public long getFullHourLimit() {
        return Math.round(PricingService.FULL_HOUR_LIMIT);
    }

    /**
     * Is single parent boolean.
     *
     * @return the boolean
     */
    public boolean isSingleParent() {
        return this.singleParent;
    }

    /**
     * Is handicap benefit boolean.
     *
     * @return the boolean
     */
    public boolean isHandicapBenefit() {
        return this.handicapBenefit;
    }

    /**
     * Is parent working boolean.
     *
     * @return the boolean
     */
    public boolean isParentWorking() {
        return this.parentWorking;
    }

    /**
     * Gets decimal format value.
     *
     * @param value the value
     * @return the decimal format value
     */
    private String getDecimalFormatValue(final double value) {
        return QuotationDTO.DECIMAL_FORMAT.format(value / 100d);
    }
}
