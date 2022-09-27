package entities.dto.admin;

import behavior.UUIDUtil;
import entities.multiselect.SelectData;
import entities.multiselect.SelectOption;
import form.admin.cares.EditOneTimeCareForm;
import models.account.AccountModel;
import models.care.EffectiveCareModel;
import models.sitter.SitterModel;
import play.data.Form;

/**
 * OneTimeCareDTO.
 *
 * @author Pierre Adam
 * @since 20.08.04
 */
public class OneTimeCareDTO {

    /**
     * The Bound edit form.
     */
    private final Form<EditOneTimeCareForm> boundEditForm;

    /**
     * The Effective care.
     */
    private final EffectiveCareModel effectiveCare;

    /**
     * The Sitter data.
     */
    private final SelectData sitterData;

    /**
     * Instantiates a new One time care dto.
     */
    public OneTimeCareDTO() {
        this.boundEditForm = null;
        this.effectiveCare = null;
        this.sitterData = this.forgeSitterData();
    }

    /**
     * Instantiates a new One time care dto.
     *
     * @param boundEditForm the bound edit form
     * @param effectiveCare the effective care
     */
    public OneTimeCareDTO(final Form<EditOneTimeCareForm> boundEditForm, final EffectiveCareModel effectiveCare) {
        this.boundEditForm = boundEditForm;
        this.effectiveCare = effectiveCare;
        this.sitterData = this.forgeSitterData();
    }

    /**
     * Forget sitter data select data.
     *
     * @return the select data
     */
    private SelectData forgeSitterData() {
        final SelectData sitterData = new SelectData();
        sitterData.options.add(new SelectOption(UUIDUtil.ZERO_UUID.toString(), "entities.dto.admin.oneTimeCardDTO.none"));
        SitterModel.find.all().forEach(sitter -> {
            final AccountModel account = sitter.getAccount();
            sitterData.options.add(new SelectOption(sitter.getUid().toString(),
                String.format("%s - %s", account.getCity(), account.getFullName())));
        });
        return sitterData;
    }

    /**
     * Gets bound edit form.
     *
     * @return the bound edit form
     */
    public Form<EditOneTimeCareForm> getBoundEditForm() {
        return this.boundEditForm;
    }

    /**
     * Has bound edit form boolean.
     *
     * @return the boolean
     */
    public boolean hasBoundEditForm() {
        return this.boundEditForm != null;
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
     * Gets sitter data.
     *
     * @return the sitter data
     */
    public SelectData getSitterData() {
        return this.sitterData;
    }
}
