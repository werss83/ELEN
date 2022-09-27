package controllers.sitter;

import actions.SessionRequired;
import controllers.AController;
import org.pac4j.play.store.PlaySessionStore;
import play.data.FormFactory;
import play.i18n.MessagesApi;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import security.Role;
import views.html.back.sitter.DashboardView;

import javax.inject.Inject;

/**
 * AdminController.
 *
 * @author Lucas Stadelmann
 * @since 20.06.14
 */
public class SitterController extends AController {

    @Inject
    public SitterController(final MessagesApi messagesApi,
                            final FormFactory formFactory,
                            final PlaySessionStore playSessionStore) {
        super(messagesApi, formFactory, playSessionStore);
    }

    @SessionRequired(Role.ROLE_PARENT)
    public Result GET_SitterDashboard(final Http.Request request) {
        return this.withMessages(request, messages ->
            Results.ok(DashboardView.render(request, messages))
        );
    }
}
