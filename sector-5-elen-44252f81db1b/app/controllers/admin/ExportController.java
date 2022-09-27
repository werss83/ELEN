package controllers.admin;

import actions.SessionRequired;
import akka.stream.javadsl.Source;
import akka.util.ByteString;
import controllers.AController;
import form.admin.ExportForm;
import org.joda.time.DateTime;
import org.pac4j.play.store.PlaySessionStore;
import play.data.Form;
import play.data.FormFactory;
import play.i18n.MessagesApi;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import security.Role;
import utils.iterator.CloseableIteratorSource;
import utils.iterator.csv.BillingIterator;
import utils.iterator.csv.ParentHoursIterator;
import utils.iterator.csv.SitterHoursIterator;
import views.html.back.admin.exports.ExportView;

import javax.inject.Inject;

/**
 * ExportController.
 *
 * @author Pierre Adam
 * @since 20.08.24
 */
public class ExportController extends AController {

    /**
     * Instantiates a new A controller.
     *
     * @param messagesApi      the messages api
     * @param formFactory      the form factory
     * @param playSessionStore the play session store
     */
    @Inject
    public ExportController(final MessagesApi messagesApi, final FormFactory formFactory, final PlaySessionStore playSessionStore) {
        super(messagesApi, formFactory, playSessionStore);
    }

    /**
     * Get sitter export result.
     *
     * @param request the request
     * @return the result
     */
    @SessionRequired(Role.ROLE_ADMIN)
    public Result GET_Export(final Http.Request request) {
        return this.withMessages(request, messages -> {
            final Form<ExportForm> boundForm = this.formFactory.form(ExportForm.class);

            return Results.ok(ExportView.render(boundForm, request, messages));
        });
    }

    @SessionRequired(Role.ROLE_ADMIN)
    public Result POST_Export(final Http.Request request) {
        return this.withMessages(request, messages -> {
            final Source<ByteString, ?> source;

            final Form<ExportForm> boundForm = this.formFactory.form(ExportForm.class).bindFromRequest(request);

            if (boundForm.hasErrors()) {
                return Results.ok(ExportView.render(boundForm, request, messages));
            }

            final ExportForm form = boundForm.get();
            final String filename;

            switch (form.getFlavor()) {
                case "sitter":
                    source = Source.fromGraph(new CloseableIteratorSource<>(
                        new SitterHoursIterator(form.getStartDate(), form.getEndDate())
                    ));
                    filename = String.format("Nounou_%s.csv", this.dateToStr(form.getStartDate(), form.getEndDate()));
                    break;
                case "parent":
                    source = Source.fromGraph(new CloseableIteratorSource<>(
                        new ParentHoursIterator(form.getStartDate(), form.getEndDate())
                    ));
                    filename = String.format("Parent_%s.csv", this.dateToStr(form.getStartDate(), form.getEndDate()));
                    break;
                case "billing":
                    source = Source.fromGraph(new CloseableIteratorSource<>(
                        new BillingIterator(form.getStartDate(), form.getEndDate())
                    ));
                    filename = String.format("Facturation_%s.csv", this.dateToStr(form.getStartDate(), form.getEndDate()));
                    break;
                default:
                    return Results.redirect(routes.ExportController.GET_Export())
                        .flashing("danger", "error.invalid");
            }

            return Results.ok()
                .chunked(source)
                .withHeader("Content-Disposition", String.format("attachment; filename=%s", filename))
                .as("text/csv");
        });
    }

    private String dateToStr(final DateTime start, final DateTime end) {
        final String s = start == null ? "-" : start.toString("dd-MM-YYYY");
        final String e = (end == null ? DateTime.now() : end).toString("dd-MM-YYYY");
        return String.format("%s_%s", s, e);
    }
}
