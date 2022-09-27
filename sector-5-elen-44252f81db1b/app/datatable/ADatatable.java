package datatable;

import behavior.RequestBehavior;
import com.jackson42.play.ebeandatatables.EbeanDataTablesQuery;
import io.ebean.ExpressionList;
import io.ebean.Finder;
import io.ebean.Model;
import org.joda.time.DateTime;
import play.i18n.MessagesApi;
import play.mvc.Http;

import java.util.function.Consumer;

/**
 * ADatatable.
 *
 * @param <T> the type parameter
 * @author Pierre Adam
 * @since 20.10.05
 */
public abstract class ADatatable<T extends Model> extends EbeanDataTablesQuery<T> implements RequestBehavior {

    /**
     * Instantiates a new A datatable.
     *
     * @param finder the finder
     * @param tClass the t class
     * @param where  the where
     */
    public ADatatable(final Finder<?, T> finder, final Class<T> tClass, final Consumer<ExpressionList<T>> where) {
        super(finder, tClass, where);
    }

    /**
     * Instantiates a new A datatable.
     *
     * @param finder the finder
     * @param tClass the t class
     */
    public ADatatable(final Finder<?, T> finder, final Class<T> tClass) {
        super(finder, tClass);
    }

    /**
     * Instantiates a new A datatable.
     *
     * @param tClass the t class
     */
    public ADatatable(final Class<T> tClass) {
        super(tClass);
    }

    /**
     * Readable date formatter string.
     *
     * @param date        the date
     * @param messagesApi the messages api
     * @param request     the request
     * @return the string
     */
    protected String readableDateFormatter(final DateTime date, final MessagesApi messagesApi, final Http.Request request) {
        if (date == null) {
            return "-";
        }
        return this.internalWithMessages(messagesApi, request, messages -> date.toString("EEEEE'<br/>'dd/MM/yyyy HH:mm", messages.lang().toLocale()));
    }
}
