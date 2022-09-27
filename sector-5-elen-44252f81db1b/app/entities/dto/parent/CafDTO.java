package entities.dto.parent;

import entities.caf.IncomeLimitsAmount;
import entities.multiselect.SelectData;
import models.parent.ParentModel;
import models.parent.enumeration.ParentIncomeGroup;
import play.i18n.Messages;
import services.CafService;

/**
 * CafDTO.
 *
 * @author Pierre Adam
 * @since 20.09.18
 */
public class CafDTO {

    /**
     * The Income group.
     */
    private final SelectData incomeGroup;

    /**
     * Instantiates a new Caf dto.
     *
     * @param cafService the caf service
     * @param parent     the parent
     */
    public CafDTO(final CafService cafService, final Messages messages, final ParentModel parent) {
        final IncomeLimitsAmount incomeLimitsAmount = cafService.getIncomeLimitsAmount(parent);
        this.incomeGroup = SelectData.fromEnum(ParentIncomeGroup.class, (ParentIncomeGroup parentIncomeGroup) -> {
            switch (parentIncomeGroup) {
                case BELOW_MIN:
                    return messages.at(parentIncomeGroup.getTransalationKey(), incomeLimitsAmount.getLowLimit() / 100);
                default:
                    return messages.at(parentIncomeGroup.getTransalationKey(), incomeLimitsAmount.getHighLimit() / 100);
            }
        });
    }

    /**
     * Gets income group.
     *
     * @return the income group
     */
    public SelectData getIncomeGroup() {
        return this.incomeGroup;
    }
}
