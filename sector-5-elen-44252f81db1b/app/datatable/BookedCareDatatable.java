package datatable;

import behavior.RequestBehavior;
import form.admin.cares.EditScheduledCareForm;
import models.care.BookedCareModel;
import models.care.EffectiveCareModel;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import play.data.Form;
import play.data.FormFactory;
import play.i18n.Messages;
import play.i18n.MessagesApi;
import play.mvc.Http;
import play.twirl.api.Html;
import views.html.back.admin.cares.common.ParentDisplay;
import views.html.back.admin.cares.common.StatusDisplay;
import views.html.back.admin.cares.scheduled.ActionsDisplay;

import java.util.function.Function;

/**
 * OneTimeCareDatatable.
 *
 * @author Pierre Adam
 * @since 20.08.17
 */
public class BookedCareDatatable extends ADatatable<BookedCareModel> implements RequestBehavior {

    /**
     * The Messages api.
     */
    protected final MessagesApi messagesApi;

    /**
     * The Form factory.
     */
    protected final FormFactory formFactory;

    public BookedCareDatatable(final MessagesApi messagesApi, final FormFactory formFactory) {
        super(BookedCareModel.find, BookedCareModel.class);
        this.messagesApi = messagesApi;
        this.formFactory = formFactory;


        final PeriodFormatter periodFormatter = new PeriodFormatterBuilder()
            .printZeroAlways()
            .minimumPrintedDigits(2)
            .appendHours()
            .appendSeparator(":")
            .appendMinutes()
            .toFormatter();

        this.setFieldDisplayHtmlSupplier("status", (bc, request) -> {
            final EffectiveCareModel lastEffectiveCare = EffectiveCareModel.find.query()
                .where()
                .eq("bookedCare", bc)
                .order("startDate DESC")
                .setMaxRows(1)
                .findOne();
            if (lastEffectiveCare == null) {
                return new Html("-");
            }
            return this.withMessages(request, messages -> StatusDisplay.render(lastEffectiveCare, request, messages));
        });
        this.setFieldDisplaySupplier("startDate", (ec, req) -> this.readableDateFormatter(ec.getStartDate(), messagesApi, req));
        this.setFieldDisplaySupplier("endDate", (ec, req) -> this.readableDateFormatter(ec.getEndDate(), messagesApi, req));
        this.setFieldDisplaySupplier("duration", bc ->
            bc.getDuration().toPeriod().toString(periodFormatter));
        this.setFieldDisplayHtmlSupplier("parent", (bc, req) ->
            this.withMessages(req, msg -> ParentDisplay.render(bc, req, msg)));
        this.setFieldDisplaySupplier("city", (bc, req) -> bc.getParent().getAccount().getCity());
        this.setFieldDisplayHtmlSupplier("parent", (bc, req) ->
            this.withMessages(req, msg -> ParentDisplay.render(bc, req, msg)));
        this.setFieldDisplayHtmlSupplier("actions", (bc, req) ->
            this.withMessages(req, msg -> {
                final Form<EditScheduledCareForm> boundEditForm = this.formFactory
                    .form(EditScheduledCareForm.class)
                    .fill(new EditScheduledCareForm(bc));
                return ActionsDisplay.render(boundEditForm, bc, req, msg);
            }));
    }

    protected <R> R withMessages(final Http.Request request, final Function<Messages, R> callback) {
        return this.internalWithMessages(this.messagesApi, request, callback);
    }
}
