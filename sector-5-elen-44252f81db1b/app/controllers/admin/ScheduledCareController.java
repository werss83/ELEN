package controllers.admin;

import actions.SessionRequired;
import actors.cares.payloads.Check;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import datatable.BookedCareDatatable;
import datatable.EffectiveCareDatatable;
import entities.dto.admin.OneTimeCareDTO;
import entities.multiselect.SelectData;
import form.admin.cares.EditFutureSitterForm;
import form.admin.cares.EditOneTimeCareForm;
import form.admin.cares.EditOneTimeCareStatusForm;
import form.admin.cares.EditScheduledCareForm;
import models.care.BookedCareModel;
import models.care.EffectiveCareModel;
import models.care.enumeration.BookedCareType;
import models.care.enumeration.EffectiveCareStatus;
import models.sitter.SitterModel;
import models.unavailability.EffectiveUnavailabilityModel;
import models.unavailability.PlannedUnavailabilityModel;
import models.unavailability.enumeration.PlannedUnavailabilityType;
import org.joda.time.DateTime;
import org.pac4j.play.store.PlaySessionStore;
import play.data.Form;
import play.data.FormFactory;
import play.i18n.MessagesApi;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import scala.concurrent.duration.Duration;
import security.Role;
import views.html.back.admin.cares.scheduled.CareDetailsActionsDisplay;
import views.html.back.admin.cares.scheduled.CareDetailsListView;
import views.html.back.admin.cares.scheduled.CaresListView;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * CareController.
 *
 * @author Pierre Adam
 * @since 20.07.24
 */
public class ScheduledCareController extends ACareController {

    /**
     * The Account data tables.
     */
    private final BookedCareDatatable caresDataTables;

    /**
     * The Actor system.
     */
    private final ActorSystem actorSystem;

    /**
     * The care creator actor.
     */
    private final ActorRef careCreatorActor;

    /**
     * Instantiates a new A controller.
     *
     * @param messagesApi      the messages api
     * @param formFactory      the form factory
     * @param playSessionStore the play session store
     * @param actorSystem      the actor system
     * @param careCreatorActor the care creator actor
     */
    @Inject
    public ScheduledCareController(final MessagesApi messagesApi,
                                   final FormFactory formFactory,
                                   final PlaySessionStore playSessionStore,
                                   final ActorSystem actorSystem,
                                   @Named("CareCreatorActor") final ActorRef careCreatorActor) {
        super(messagesApi, formFactory, playSessionStore);

        this.actorSystem = actorSystem;
        this.careCreatorActor = careCreatorActor;

        this.caresDataTables = new BookedCareDatatable(messagesApi, formFactory);
        this.caresDataTables.setWhere(query -> query.eq("type", BookedCareType.RECURRENT));
    }

    /**
     * Get effective cares result.
     *
     * @param request the request
     * @return the result
     */
    @SessionRequired(Role.ROLE_ADMIN)
    public Result GET_Cares(final Http.Request request) {
        return this.withMessages(request, messages -> Results.ok(CaresListView.render(request, messages)));
    }

    /**
     * Post cares ajax list result.
     *
     * @param request the request
     * @return the result
     */
    @SessionRequired(Role.ROLE_ADMIN)
    public Result POST_CaresAjaxList(final Http.Request request) {
        return this.dataTablesAjaxRequest(request,
            boundForm -> Results.badRequest(),
            form -> Results.ok(this.caresDataTables.getAjaxResult(request, form.getParameters()))
        );
    }

    /**
     * Post update booked care result.
     *
     * @param request    the request
     * @param bookedCare the booked care
     * @return the result
     */
    public Result POST_UpdateBookedCare(final Http.Request request, final BookedCareModel bookedCare) {
        return this.withMessages(request, messages -> {
            final Form<EditScheduledCareForm> boundForm = this.formFactory
                .form(EditScheduledCareForm.class)
                .bindFromRequest(request);

            if (boundForm.hasErrors()) {
                return Results.redirect(routes.ScheduledCareController.GET_Cares());
            }

            final EditScheduledCareForm form = boundForm.get();

            this.safeDB(() -> {
                form.updateBookedCareModel(bookedCare);
                bookedCare.save();
            });

            this.actorSystem.scheduler().scheduleOnce(
                Duration.create(1, TimeUnit.SECONDS),
                this.careCreatorActor,
                new Check(bookedCare),
                this.actorSystem.dispatcher(),
                ActorRef.noSender()
            );

            return Results.redirect(routes.ScheduledCareController.GET_Cares());
        });
    }

    /**
     * Get care detail result.
     *
     * @param request    the request
     * @param bookedCare the booked care
     * @return the result
     */
    @SessionRequired(Role.ROLE_ADMIN)
    public Result GET_CareDetail(final Http.Request request, final BookedCareModel bookedCare) {
        return this.withMessages(request, messages -> {
            final Form<EditFutureSitterForm> boundForm = this.formFactory.form(EditFutureSitterForm.class);
            return Results.ok(CareDetailsListView.render(bookedCare, boundForm, new OneTimeCareDTO(), request, messages));
        });
    }

    /**
     * Post care detail ajax list result.
     *
     * @param request    the request
     * @param bookedCare the booked care
     * @return the result
     */
    @SessionRequired(Role.ROLE_ADMIN)
    public Result POST_CareDetailAjaxList(final Http.Request request, final BookedCareModel bookedCare) {
        final EffectiveCareDatatable datatable = new EffectiveCareDatatable(this.messagesApi, this.formFactory);
        datatable.setWhere(query -> query.eq("bookedCare", bookedCare));

        datatable.setFieldDisplayHtmlSupplier("actions", (ec, req) ->
            this.withMessages(req, msg -> {
                final Form<EditOneTimeCareForm> boundEditForm = this.formFactory
                    .form(EditOneTimeCareForm.class, EditOneTimeCareForm.Groups.smartGroup(ec))
                    .fill(new EditOneTimeCareForm(ec));
                final Form<EditOneTimeCareStatusForm> boundUpgradeForm = this.formFactory
                    .form(EditOneTimeCareStatusForm.class)
                    .fill(new EditOneTimeCareStatusForm(ec));
                final SelectData sitterData = new OneTimeCareDTO().getSitterData();
                return CareDetailsActionsDisplay.render(boundEditForm, sitterData, boundUpgradeForm, ec, req, msg);
            })
        );

        return this.dataTablesAjaxRequest(request,
            boundForm -> Results.badRequest(),
            form -> Results.ok(datatable.getAjaxResult(request, form.getParameters()))
        );
    }

    /**
     * Post update care result.
     *
     * @param request       the request
     * @param bookedCare    the booked care
     * @param effectiveCare the effective care
     * @return the result
     */
    @SessionRequired(Role.ROLE_ADMIN)
    public Result POST_UpdateCare(final Http.Request request, final BookedCareModel bookedCare, final EffectiveCareModel effectiveCare) {
        return this.updateCare(request, effectiveCare,
            (oneTimeCareDTO, messages) -> {
                final Form<EditFutureSitterForm> boundForm = this.formFactory.form(EditFutureSitterForm.class);
                return CareDetailsListView.render(bookedCare, boundForm, oneTimeCareDTO, request, messages);
            },
            routes.ScheduledCareController.GET_CareDetail(bookedCare));
    }

    /**
     * Post update care result.
     *
     * @param request    the request
     * @param bookedCare the booked care
     * @return the result
     */
    @SessionRequired(Role.ROLE_ADMIN)
    public Result POST_UpdateFutureCare(final Http.Request request, final BookedCareModel bookedCare) {
        return this.withMessages(request, messages -> {
            final Form<EditFutureSitterForm> boundForm = this.formFactory.form(EditFutureSitterForm.class).bindFromRequest(request);

            if (boundForm.hasErrors()) {
                return Results.badRequest(CareDetailsListView.render(bookedCare, boundForm, new OneTimeCareDTO(), request, messages));
            }

            final EditFutureSitterForm form = boundForm.get();

            final List<EffectiveCareModel> effectiveCareModels = EffectiveCareModel.find.query().where()
                .eq("bookedCare", bookedCare)
                .gt("startDate", DateTime.now())
                .order("startDate")
                .findList();

            if (effectiveCareModels.size() < 1) {
                // ABORT
                return Results.redirect(routes.ScheduledCareController.GET_CareDetail(bookedCare));
            }

            final SitterModel resolvedSitter = form.getResolvedSitter();

            effectiveCareModels.forEach(ec -> {
                final EffectiveUnavailabilityModel currentEU = ec.getEffectiveUnavailability();

                if (currentEU == null || currentEU.getPlannedUnavailability().getType() == PlannedUnavailabilityType.RECURRENT) {
                    final PlannedUnavailabilityModel newPU = new PlannedUnavailabilityModel(PlannedUnavailabilityType.ONE_TIME, ec.getStartDate(), ec.getDuration(), resolvedSitter);
                    newPU.setEndDate(ec.getEndDate());
                    newPU.save();

                    final EffectiveUnavailabilityModel newEU = new EffectiveUnavailabilityModel(ec.getStartDate(), ec.getEndDate(), newPU);
                    newEU.save();

                    ec.setEffectiveUnavailability(newEU);
                    if (ec.getStatus() == EffectiveCareStatus.PLANNED) {
                        ec.setStatus(EffectiveCareStatus.ASSIGNED);
                    }
                    ec.save();

                    if (currentEU != null) {
                        final PlannedUnavailabilityModel currentPU = currentEU.getPlannedUnavailability();
                        if (currentPU.getType() == PlannedUnavailabilityType.ONE_TIME) {
                            currentPU.delete();
                            currentEU.delete();
                        } else {
                            currentEU.delete();
                            if (EffectiveUnavailabilityModel.find.query().where().eq("plannedUnavailability", currentPU).setMaxRows(1).findCount() > 0) {
                                currentPU.setEndDate(DateTime.now());
                                currentPU.save();
                            } else {
                                currentPU.delete();
                            }
                        }
                    }
                } else {
                    final PlannedUnavailabilityModel currentPU = currentEU.getPlannedUnavailability();
                    currentPU.setSitter(resolvedSitter);
                    currentPU.save();
                }
            });

            return Results.redirect(routes.ScheduledCareController.GET_CareDetail(bookedCare));
        });
    }

    /**
     * Post update care status result.
     *
     * @param request       the request
     * @param bookedCare    the booked care
     * @param effectiveCare the effective care
     * @return the result
     */
    @SessionRequired(Role.ROLE_ADMIN)
    public Result POST_UpdateCareStatus(final Http.Request request, final BookedCareModel bookedCare, final EffectiveCareModel effectiveCare) {
        return this.updateCareStatus(request, effectiveCare, routes.ScheduledCareController.GET_CareDetail(bookedCare));
    }
}
