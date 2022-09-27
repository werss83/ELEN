package datatable;

import behavior.RequestBehavior;
import entities.dto.admin.OneTimeCareDTO;
import entities.multiselect.SelectData;
import form.admin.cares.EditOneTimeCareForm;
import form.admin.cares.EditOneTimeCareStatusForm;
import models.care.EffectiveCareModel;
import models.care.enumeration.EffectiveCareStatus;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import play.data.Form;
import play.data.FormFactory;
import play.i18n.Messages;
import play.i18n.MessagesApi;
import play.mvc.Http;
import views.html.back.admin.cares.common.ParentDisplay;
import views.html.back.admin.cares.common.SitterDisplay;
import views.html.back.admin.cares.common.StatusDisplay;
import views.html.back.admin.cares.onetime.ActionsDisplay;

import java.util.function.Function;

/**
 * OneTimeCareDatatable.
 *
 * @author Pierre Adam
 * @since 20.08.17
 */
public class EffectiveCareDatatable extends ADatatable<EffectiveCareModel> implements RequestBehavior {

    /**
     * The Messages api.
     */
    protected final play.i18n.MessagesApi messagesApi;

    /**
     * The Form factory.
     */
    protected final FormFactory formFactory;

    public EffectiveCareDatatable(final MessagesApi messagesApi, final FormFactory formFactory) {
        super(EffectiveCareModel.find, EffectiveCareModel.class);
        this.messagesApi = messagesApi;
        this.formFactory = formFactory;

        final PeriodFormatter periodFormatter = new PeriodFormatterBuilder()
            .printZeroAlways()
            .minimumPrintedDigits(2)
            .appendHours()
            .appendSeparator(":")
            .appendMinutes()
            .toFormatter();

        this.setFieldDisplayHtmlSupplier("status", (ec, req) ->
            this.withMessages(req, msg -> StatusDisplay.render(ec, req, msg)));
        this.setFieldDisplaySupplier("startDate", (ec, req) -> this.readableDateFormatter(ec.getStartDate(), messagesApi, req));
        this.setFieldDisplaySupplier("endDate", (ec, req) -> this.readableDateFormatter(ec.getEndDate(), messagesApi, req));
        this.setFieldDisplaySupplier("duration", ec ->
            new Period(ec.getEndDate().getMillis() - ec.getStartDate().getMillis()).toString(periodFormatter));
        this.setFieldDisplayHtmlSupplier("parent", (ec, req) ->
            this.withMessages(req, msg -> ParentDisplay.render(ec.getBookedCare(), req, msg)));
        this.setFieldDisplaySupplier("city", (ec, req) -> ec.getBookedCare().getParent().getAccount().getCity());
        this.setFieldDisplayHtmlSupplier("sitter", (ec, req) ->
            this.withMessages(req, msg -> SitterDisplay.render(ec.getEffectiveUnavailability(), req, msg)));
        this.setFieldDisplayHtmlSupplier("actions", (ec, req) ->
            this.withMessages(req, msg -> {
                final Form<EditOneTimeCareForm> boundEditForm = this.formFactory
                    .form(EditOneTimeCareForm.class, EditOneTimeCareForm.Groups.smartGroup(ec))
                    .fill(new EditOneTimeCareForm(ec));
                final Form<EditOneTimeCareStatusForm> boundUpgradeForm = this.formFactory
                    .form(EditOneTimeCareStatusForm.class)
                    .fill(new EditOneTimeCareStatusForm(ec));
                final SelectData sitterData = new OneTimeCareDTO().getSitterData();
                return ActionsDisplay.render(boundEditForm, sitterData, boundUpgradeForm, ec, req, msg);
            })
        );

        this.setSearchHandler("status", (query, searchTerm) -> {
            if (!searchTerm.isEmpty()) {
                query.eq("status", EffectiveCareStatus.valueOf(searchTerm));
            }
        });
        this.setSearchHandler("startDate", (query, searchTerm) -> {
            try {
                final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");
                // Exceptionnaly use a datetime with a UTC Zone. This will effectively remove the timezone from the
                // DateTime allowing a partial use of the DateTime in postgres.
                final DateTime date = dateTimeFormatter.withZoneUTC().parseDateTime(searchTerm).withMillisOfDay(0);
                query.eq("DATE_TRUNC('day', startDate)", date);
            } catch (final Exception ignore) {
            }
        });
        this.setSearchHandler("parent",
            (query, searchTerm) -> query.ilike("unaccent(concat(bookedCare.parent.account.firstName, " +
                    "' ', bookedCare.parent.account.lastName, ' ', bookedCare.parent.account.email))",
                String.format("%%%s%%", StringUtils.stripAccents(searchTerm.trim()))));
        this.setSearchHandler("sitter",
            (query, searchTerm) -> query.ilike("unaccent(concat(" +
                    "effectiveUnavailability.plannedUnavailability.sitter.account.firstName, " +
                    "' ', effectiveUnavailability.plannedUnavailability.sitter.account.lastName, " +
                    "' ', effectiveUnavailability.plannedUnavailability.sitter.account.email))",
                String.format("%%%s%%", StringUtils.stripAccents(searchTerm.trim()))));
    }

    protected <R> R withMessages(final Http.Request request, final Function<Messages, R> callback) {
        return this.internalWithMessages(this.messagesApi, request, callback);
    }
}
