package utils.iterator.csv;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.csv.CsvGenerator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import entities.export.SitterHoursEntity;
import models.sitter.SitterModel;
import org.joda.time.DateTime;

/**
 * SitterHoursIterator.
 *
 * @author Pierre Adam
 * @since 20.08.24
 */
public class SitterHoursIterator extends CsvIterator<SitterModel, SitterHoursEntity> {

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
    public SitterHoursIterator(final DateTime start, final DateTime end) {
        super(true);
        this.start = start;
        this.end = end;

        final CsvMapper mapper = new CsvMapper();
        mapper.configure(CsvGenerator.Feature.ALWAYS_QUOTE_STRINGS, true);
        final CsvSchema schema = mapper
            .schemaFor(SitterHoursEntity.class)
            .withoutHeader()
            .withColumnSeparator(',');
        this.objectWriter = mapper
            .registerModule(
                new JodaModule()
            )
            .disable(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS)
            .writer(schema);

        this.setIterator(SitterModel.find.query().where().findIterate());
    }

    @Override
    protected boolean accept(final SitterModel sitter) {
        final SitterHoursEntity entity = new SitterHoursEntity(
            sitter,
            this.start,
            this.end
        );

        this.setCache(entity);

        return entity.hasHours();
    }

    @Override
    protected String renderHeader() {
        return "Id,Nom,Prénom,Email,Phone,Address," +
            "Total Heures,Heures Standard,Heures Nuit, " +
            "Heures WE+JF,Heures Nuit WE+JF\n";
    }

    @Override
    protected String toCsvLine(final SitterModel sitter) {
        try {
            return this.objectWriter.writeValueAsString(this.getCache());
        } catch (final JsonProcessingException e) {
            throw new RuntimeException("Unable to convert the ExportCardEntity.", e);
        }
    }
}
