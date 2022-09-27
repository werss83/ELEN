package controllers.admin;

import controllers.AController;
import entities.dto.admin.OneTimeCareDTO;
import form.admin.cares.EditOneTimeCareForm;
import form.admin.cares.EditOneTimeCareStatusForm;
import models.care.BookedCareModel;
import models.care.EffectiveCareModel;
import models.care.enumeration.BookedCareType;
import models.care.enumeration.EffectiveCareStatus;
import models.unavailability.EffectiveUnavailabilityModel;
import models.unavailability.PlannedUnavailabilityModel;
import models.unavailability.enumeration.PlannedUnavailabilityType;
import org.joda.time.Duration;
import org.pac4j.play.store.PlaySessionStore;
import play.data.Form;
import play.data.FormFactory;
import play.i18n.Messages;
import play.i18n.MessagesApi;
import play.mvc.Call;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import play.twirl.api.Html;

import java.util.function.BiFunction;

/**
 * ACareController.
 *
 * @author Pierre Adam
 * @since 20.08.17
 */
public class ACareController extends AController {

    /**
     * Instantiates a new A controller.
     *
     * @param messagesApi      the messages api
     * @param formFactory      the form factory
     * @param playSessionStore the play session store
     */
    protected ACareController(final MessagesApi messagesApi, final FormFactory formFactory, final PlaySessionStore playSessionStore) {
        super(messagesApi, formFactory, playSessionStore);
    }

    /**
     * Update care result.
     *
     * @param request       the request
     * @param effectiveCare the effective care
     * @param successCall   the success call
     * @return the result
     */
    protected Result updateCare(final Http.Request request, final EffectiveCareModel effectiveCare, final BiFunction<OneTimeCareDTO, Messages, Html> onErrorRender, final Call successCall) {
        return this.withMessages(request, messages -> {
            final Form<EditOneTimeCareForm> boundForm = this.formFactory
                .form(EditOneTimeCareForm.class, EditOneTimeCareForm.Groups.smartGroup(effectiveCare))
                .bindFromRequest(request);

            if (boundForm.hasErrors()) {
                return Results.badRequest(onErrorRender.apply(new OneTimeCareDTO(boundForm, effectiveCare), messages));
            }

            final EditOneTimeCareForm form = boundForm.get();

            this.safeDB(() -> {
                final BookedCareModel bookedCare = effectiveCare.getBookedCare();
                final Duration duration = Duration.millis(form.getEndDate().getMillis() - form.getStartDate().getMillis());

                effectiveCare.setStartDate(form.getStartDate());
                effectiveCare.setEndDate(form.getEndDate());
                effectiveCare.save();

                if (bookedCare.getType() == BookedCareType.ONE_TIME) {
                    bookedCare.setStartDate(form.getStartDate());
                    bookedCare.setEndDate(form.getEndDate());
                    bookedCare.setDuration(duration);
                    bookedCare.save();
                }

                if (effectiveCare.getStatus() == EffectiveCareStatus.ASSIGNED || effectiveCare.getStatus() == EffectiveCareStatus.PLANNED) {
                    if (form.getResolvedSitter() == null) {
                        final EffectiveUnavailabilityModel effectiveUnavailability = effectiveCare.getEffectiveUnavailability();
                        if (effectiveUnavailability != null) {
                            effectiveUnavailability.getPlannedUnavailability().delete();
                            effectiveUnavailability.delete();

                            effectiveCare.setEffectiveUnavailability(null);
                            effectiveCare.setStatus(EffectiveCareStatus.PLANNED);
                            effectiveCare.save();
                        }
                    } else {
                        EffectiveUnavailabilityModel effectiveUnavailability = effectiveCare.getEffectiveUnavailability();
                        if (effectiveUnavailability == null) {
                            final PlannedUnavailabilityModel plannedUnavailability =
                                new PlannedUnavailabilityModel(PlannedUnavailabilityType.ONE_TIME, form.getStartDate(), duration, form.getResolvedSitter());
                            effectiveUnavailability = new EffectiveUnavailabilityModel(form.getStartDate(), form.getEndDate(), plannedUnavailability);
                            effectiveUnavailability.setEffectiveCare(effectiveCare);
                            effectiveCare.setEffectiveUnavailability(effectiveUnavailability);
                        }
                        final PlannedUnavailabilityModel plannedUnavailability = effectiveUnavailability.getPlannedUnavailability();

                        effectiveUnavailability.setStartDate(form.getStartDate());
                        effectiveUnavailability.setStartDate(form.getEndDate());

                        plannedUnavailability.setStartDate(form.getStartDate());
                        plannedUnavailability.setEndDate(form.getEndDate());
                        plannedUnavailability.setDuration(duration);
                        plannedUnavailability.setSitter(form.getResolvedSitter());

                        effectiveCare.setStatus(EffectiveCareStatus.ASSIGNED);

                        plannedUnavailability.save();
                        effectiveUnavailability.save();
                        effectiveCare.save();
                    }
                }
            });

            return Results.redirect(successCall);
        });
    }

    /**
     * Update care status result.
     *
     * @param request       the request
     * @param effectiveCare the effective care
     * @param redirectTo    the redirect to
     * @return the result
     */
    protected Result updateCareStatus(final Http.Request request, final EffectiveCareModel effectiveCare, final Call redirectTo) {
        return this.withMessages(request, messages -> {
            final Form<EditOneTimeCareStatusForm> boundForm = this.formFactory
                .form(EditOneTimeCareStatusForm.class)
                .bindFromRequest(request);

            if (boundForm.hasErrors()) {
                return Results.redirect(redirectTo);
            }

            final EditOneTimeCareStatusForm form = boundForm.get();

            this.safeDB(() -> {
                effectiveCare.setStatus(form.getStatus());
                effectiveCare.save();
            });

            return Results.redirect(redirectTo);
        });
    }
}
