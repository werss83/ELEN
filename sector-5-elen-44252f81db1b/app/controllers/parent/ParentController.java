package controllers.parent;

import actions.SessionRequired;
import controllers.AController;
import io.ebean.QueryIterator;
import models.care.EffectiveCareModel;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.pac4j.play.store.PlaySessionStore;
import play.data.FormFactory;
import play.i18n.MessagesApi;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import security.Role;
import views.html.back.parent.DashboardView;

import javax.inject.Inject;

/**
 * ParentController.
 *
 * @author Lucas Stadelmann
 * @since 20.06.29
 */
public class ParentController extends AController {

    /**
     * Instantiates a new Parent controller.
     *
     * @param messagesApi      the messages api
     * @param formFactory      the form factory
     * @param playSessionStore the play session store
     */
    @Inject
    public ParentController(final MessagesApi messagesApi,
                            final FormFactory formFactory,
                            final PlaySessionStore playSessionStore) {
        super(messagesApi, formFactory, playSessionStore);
    }

    /**
     * Get parent dashboard result.
     *
     * @param request the request
     * @return the result
     */
    @SessionRequired(Role.ROLE_PARENT)
    public Result GET_ParentDashboard(final Http.Request request) {
        return this.withAccount(request, account ->
            this.withMessages(request, messages -> {
                final DateTime startOfThisMonth = DateTime.now().withDayOfMonth(1).withMillisOfDay(0);
                final QueryIterator<EffectiveCareModel> iterator = EffectiveCareModel.find.query()
                    .where()
                    .eq("bookedCare.parent", account.getParent())
                    .ge("startDate", startOfThisMonth)
                    .lt("startDate", startOfThisMonth.plusMonths(1))
                    .findIterate();

                Duration scheduled = new Duration(0);
                while (iterator.hasNext()) {
                    final EffectiveCareModel care = iterator.next();
                    scheduled = scheduled.plus(care.getDuration());
                }

                final double scheduledHours = (double) scheduled.getMillis() / (1000 * 3600);
                final double remainingHours = 16.0 - scheduledHours;

                return Results.ok(DashboardView.render(scheduledHours, remainingHours, request, messages));
            })
        );
    }
}
