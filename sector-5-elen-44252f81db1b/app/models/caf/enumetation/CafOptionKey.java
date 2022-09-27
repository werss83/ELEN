package models.caf.enumetation;

import models.caf.entities.IncomeLimits;
import models.caf.entities.MonthlySupport;
import models.options.Option;

/**
 * CafOptionKey.
 *
 * @author Pierre Adam
 * @since 20.07.23
 */
public enum CafOptionKey {

    @Option(defaultValue = "{\"baseLow\":2108700,\"baseHigh\":4686100,\"stepLow\":299300,\"stepHigh\":665200,\"sppi\":1.40}", type = IncomeLimits.class)
    INCOME_LIMIT,

    @Option(defaultValue = "{\"under3\":{\"low\":85983,\"medium\":74121,\"high\":62262},\"under6\":{\"low\":42992,\"medium\":37061,\"high\":31131},\"sc\":0.5,\"nobhc\":1.1,\"hp\":1.3,\"hc\":1.3,\"sp\":1.3}", type = MonthlySupport.class)
    MONTHLY_SUPPORT
}
