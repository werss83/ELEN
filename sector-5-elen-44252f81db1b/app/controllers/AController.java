package controllers;

import behavior.RequestBehavior;
import com.jackson42.play.ebeandatatables.entities.AjaxQueryForm;
import com.jackson42.play.ebeandatatables.interfaces.PlayEbeanDataTables;
import interfaces.DbLogic;
import io.ebean.DB;
import models.account.AccountModel;
import org.pac4j.play.store.PlaySessionStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.data.Form;
import play.data.FormFactory;
import play.i18n.Messages;
import play.i18n.MessagesApi;
import play.libs.typedmap.TypedEntry;
import play.libs.typedmap.TypedMap;
import play.mvc.Controller;
import play.mvc.Http;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * AController.
 *
 * @author Pierre Adam
 * @since 19.02.27
 */
public class AController extends Controller implements RequestBehavior, PlayEbeanDataTables {

    /**
     * The Logger.
     */
    protected final Logger logger;

    /**
     * The Messages api.
     */
    protected final MessagesApi messagesApi;

    /**
     * The Form factory.
     */
    protected final FormFactory formFactory;

    /**
     * The Play session store.
     */
    protected final PlaySessionStore playSessionStore;

    /**
     * Instantiates a new A controller.
     *
     * @param messagesApi      the messages api
     * @param formFactory      the form factory
     * @param playSessionStore the play session store
     */
    protected AController(final MessagesApi messagesApi,
                          final FormFactory formFactory,
                          final PlaySessionStore playSessionStore) {
        this.logger = LoggerFactory.getLogger(this.getClass());
        this.messagesApi = messagesApi;
        this.formFactory = formFactory;
        this.playSessionStore = playSessionStore;
    }

    /**
     * With messages.
     *
     * @param <R>      the type parameter
     * @param request  the request
     * @param callback the callback
     * @return the r
     */
    protected <R> R withMessages(final Http.Request request, final Function<Messages, R> callback) {
        return this.internalWithMessages(this.messagesApi, request, callback);
    }

    /**
     * With account r.
     *
     * @param <R>      the type parameter
     * @param request  the request
     * @param callback the callback
     * @return the r
     */
    protected <R> R withAccount(final Http.Request request, final Function<AccountModel, R> callback) {
        try {
            return this.internalWithAccount(request, callback);
        } catch (final RuntimeException e) {
            if ("401".equals(e.getMessage())) {
                this.logger.error("AController.withAccount was called without any account available.");
            }
            throw e;
        }
    }

    /**
     * With optional account r.
     *
     * @param <R>      the type parameter
     * @param request  the request
     * @param callback the callback
     * @return the r
     */
    protected <R> R withOptionalAccount(final Http.Request request, final Function<Optional<AccountModel>, R> callback) {
        return this.internalWithOptionalAccount(request, callback);
    }

    /**
     * Request add attrs r.
     *
     * @param <R>      the type parameter
     * @param request  the request
     * @param attr     the attr
     * @param callback the callback
     * @return the r
     */
    protected <R> R requestAddAttrs(final Http.Request request, final TypedEntry<?> attr, final Function<Http.Request, R> callback) {
        final TypedMap typedMap = request.attrs().putAll(attr);
        return callback.apply(request.withAttrs(typedMap));
    }

    /**
     * Request add attrs r.
     *
     * @param <R>      the type parameter
     * @param request  the request
     * @param attrs    the attrs
     * @param callback the callback
     * @return the r
     */
    protected <R> R requestAddAttrs(final Http.Request request, final List<TypedEntry<?>> attrs, final Function<Http.Request, R> callback) {
        TypedMap typedMap = request.attrs();
        for (final TypedEntry<?> attr : attrs) {
            typedMap = typedMap.putAll(attr);
        }
        return callback.apply(request.withAttrs(typedMap));
    }

    /**
     * Data tables ajax request.
     *
     * @param <R>             the type parameter
     * @param request         the request
     * @param errorCallback   the error callback
     * @param successCallback the success callback
     * @return the r
     */
    protected <R> R dataTablesAjaxRequest(final Http.Request request,
                                          final Function<Form<AjaxQueryForm>, R> errorCallback,
                                          final Function<AjaxQueryForm, R> successCallback) {
        return this.dataTablesAjaxRequest(request, this.formFactory, errorCallback, successCallback);
    }

    /**
     * With dd transaction.
     *
     * @param logic the logic
     */
    protected void safeDB(final DbLogic logic) {
        try {
            DB.beginTransaction();
            logic.process();
            DB.commitTransaction();
        } finally {
            DB.endTransaction();
        }
    }

    /**
     * With dd transaction.
     *
     * @param logic the logic
     */
    protected <T> T safeDB(final Supplier<T> logic) {
        try {
            DB.beginTransaction();
            final T value = logic.get();
            DB.commitTransaction();
            return value;
        } finally {
            DB.endTransaction();
        }
    }
}
