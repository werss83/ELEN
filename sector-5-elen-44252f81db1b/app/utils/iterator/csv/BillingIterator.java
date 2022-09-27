package utils.iterator.csv;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.csv.CsvGenerator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import entities.export.BillingEntity;
import entities.export.ParentHoursEntity;
import io.ebean.ExpressionList;
import io.ebean.QueryIterator;
import models.care.EffectiveCareModel;
import models.care.enumeration.EffectiveCareStatus;
import models.parent.ParentModel;
import models.sitter.SitterModel;
import org.joda.time.DateTime;
import utils.date.DateChecker;

/**
 * SitterHoursIterator.
 *
 * @author Pierre Adam
 * @since 20.08.24
 */
public class BillingIterator extends CsvIterator<ParentModel, ParentHoursEntity> {

    /**
     * The object writer for the CSV.
     */
    private final ObjectWriter objectWriter;

    /**
     * The Start.
     */
    private final DateTime start;

    /**
     * The End.
     */
    private final DateTime end;

    /**
     * Instantiates a new Sitter hours iterator.
     *
     * @param start the start
     * @param end   the end
     */
    public BillingIterator(final DateTime start, final DateTime end) {
        super(true);
        this.start = start;
        this.end = end;

        final CsvMapper mapper = new CsvMapper();
        mapper.configure(CsvGenerator.Feature.ALWAYS_QUOTE_STRINGS, true);
        final CsvSchema schema = mapper
            .schemaFor(BillingEntity.class)
            .withoutHeader()
            .withColumnSeparator(',');
        this.objectWriter = mapper
            .registerModule(
                new JodaModule()
            )
            .disable(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS)
            .writer(schema);

        this.setIterator(ParentModel.find.query().where().findIterate());
    }

    @Override
    protected boolean accept(final ParentModel parent) {
        ExpressionList<EffectiveCareModel> query = EffectiveCareModel.find.query().where()
            .eq("bookedCare.parent", parent)
            .isNotNull("endDate")
            .eq("status", EffectiveCareStatus.PERFORMED);

        if (this.start != null) {
            query = query.ge("startDate", this.start.withMillisOfDay(0));
        }
        if (this.end != null) {
            query = query.lt("startDate", this.end.withMillisOfDay(0));
        }

        query.setMaxRows(1);
        return query.findCount() > 0;
    }

    @Override
    protected String renderHeader() {
        return "Nom,Prénom,Address, Phone,Date de début,Date de fin," +
            "Heures Standard,Heures Nuit, " +
            "Heures WE+JF,Nounou, Numéro de sécurité sociale\n";
    }

    @Override
    protected String toCsvLine(final ParentModel parent) {
        try {
            ExpressionList<EffectiveCareModel> query = EffectiveCareModel.find.query()
                .where()
                .eq("bookedCare.parent", parent)
                .isNotNull("endDate")
                .eq("status", EffectiveCareStatus.PERFORMED);

            if (this.start != null) {
                query = query.ge("startDate", this.start.withMillisOfDay(0));
            }
            if (this.end != null) {
                query = query.lt("startDate", this.end.withMillisOfDay(0));
            }

            final StringBuilder buffer = new StringBuilder();
            final QueryIterator<EffectiveCareModel> iterator = query.findIterate();
            while (iterator.hasNext()) {
                final EffectiveCareModel care = iterator.next();
                final SitterModel sitter = care.getEffectiveUnavailability().getPlannedUnavailability().getSitter();

                final BillingEntity billingEntity = new BillingEntity(parent, sitter, care.getStartDate(), care.getEndDate());

                final DateChecker.DateContext context = DateChecker.getContext(care);
                billingEntity.addStandardHours(context.getStandard());
                billingEntity.addNightHours(context.getNight());
                billingEntity.addBankHolidayHours(context.getBankHoliday().plus(context.getNightBankHoliday()));
                buffer.append(this.objectWriter.writeValueAsString(billingEntity));
            }

            return buffer.toString();
        } catch (final JsonProcessingException e) {
            throw new RuntimeException("Unable to convert the ExportCardEntity.", e);
        }
    }
}
