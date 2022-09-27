package controllers.admin;

import actions.SessionRequired;
import controllers.AController;
import entities.dto.admin.AdminDashboardDTO;
import org.pac4j.play.store.PlaySessionStore;
import play.data.FormFactory;
import play.i18n.MessagesApi;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import security.Role;
import views.html.back.admin.DashboardView;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * AdminController.
 *
 * @author Lucas Stadelmann
 * @since 20.06.14
 */
public class AdminController extends AController {

    @Inject
    public AdminController(final MessagesApi messagesApi,
                           final FormFactory formFactory,
                           final PlaySessionStore playSessionStore) {
        super(messagesApi, formFactory, playSessionStore);
    }

    @SessionRequired(Role.ROLE_ADMIN)
    public CompletionStage<Result> GET_AdminDashboard(final Http.Request request) {
        return this.withMessages(request, messages ->
            CompletableFuture
                .supplyAsync(AdminDashboardDTO::new)
                .thenApply(dashboardDTO -> Results.ok(DashboardView.render(dashboardDTO, request, messages)))
        );
    }
}
