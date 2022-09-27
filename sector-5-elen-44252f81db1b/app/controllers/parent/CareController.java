package controllers.parent;

import actions.SessionRequired;
import actors.cares.payloads.Check;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import controllers.AController;
import entities.dto.quotation.QuotationDTO;
import exceptions.CookieUnavailableException;
import form.parent.BookedCareForm;
import form.parent.onboarding.CareForm;
import models.care.BookedCareModel;
import models.care.CareCriteriaModel;
import models.care.EffectiveCareModel;
import models.care.enumeration.BookedCareType;
import models.care.enumeration.EffectiveCareStatus;
import models.parent.ParentModel;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.pac4j.play.store.PlaySessionStore;
import play.data.Form;
import play.data.FormFactory;
import play.i18n.MessagesApi;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import security.Role;
import services.CafService;
import services.CareEmailService;
import services.PricingService;
import views.html.back.parent.care.CurrentCareView;
import views.html.back.parent.care.ScheduleNewCareQuotationView;
import views.html.back.parent.care.ScheduleNewCareView;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * CareController.
 *
 * @author Pierre Adam
 * @since 20.07.19
 */
public class CareController extends AController {

    /**
     * The Pricing service.
     */
    private final PricingService pricingService;

    /**
     * The Caf service.
     */
    private final CafService cafService;

    /**
     * The Actor system.
     */
    private final ActorSystem actorSystem;

    /**
     * The care creator actor.
     */
    private final ActorRef careCreatorActor;

    /**
     * The Care email service.
     */
    private final CareEmailService careEmailService;

    /**
     * Instantiates a new Care controller.
     *
     * @param messagesApi      the messages api
     * @param formFactory      the form factory
     * @param playSessionStore the play session store
     * @param pricingService   the pricing service
     * @param cafService       the caf service
     */
    @Inject
    public CareController(final MessagesApi messagesApi,
                          final FormFactory formFactory,
                          final PlaySessionStore playSessionStore,
                          final PricingService pricingService,
                          final CafService cafService,
                          final ActorSystem actorSystem,
                          @Named("CareCreatorActor") final ActorRef careCreatorActor,
                          final CareEmailService careEmailService) {
        super(messagesApi, formFactory, playSessionStore);
        this.pricingService = pricingService;
        this.cafService = cafService;
        this.actorSystem = actorSystem;
        this.careCreatorActor = careCreatorActor;
        this.careEmailService = careEmailService;
    }

    /**
     * Get current care result.
     *
     * @param request the request
     * @return the result
     */
    @SessionRequired(Role.ROLE_PARENT)
    public Result GET_CurrentCare(final Http.Request request) {
        return this.withAccount(request, account ->
            this.withMessages(request, messages -> {
                final ParentModel parent = account.getParent();
                final EffectiveCareModel care = EffectiveCareModel.find.query()
                    .where()
                    .eq("bookedCare.parent", parent)
                    .gt("endDate", DateTime.now())
                    .in("status", EffectiveCareStatus.PLANNED, EffectiveCareStatus.ASSIGNED)
                    .order("endDate")
                    .setMaxRows(1)
                    .findOne();

                return Results.ok(CurrentCareView.render(care, request, messages));
            })
        );
    }

    /**
     * Get programmed care result.
     *
     * @param request the request
     * @return the result
     */
    @SessionRequired(Role.ROLE_PARENT)
    public Result GET_ScheduleNewCare(final Http.Request request) {
        return this.withAccount(request, account ->
            this.withMessages(request, messages -> {
                final Form<BookedCareForm> form = this.formFactory.form(BookedCareForm.class);

                return Results.ok(ScheduleNewCareView.render(form, request, messages));
            }));
    }

    /**
     * Post schedule new care result.
     *
     * @param request the request
     * @return the result
     */
    @SessionRequired(Role.ROLE_PARENT)
    public Result POST_ScheduleNewCare(final Http.Request request) {
        return this.withAccount(request, account ->
            this.withMessages(request, messages -> {
                final Form<BookedCareForm> boundForm = this.formFactory.form(BookedCareForm.class).bindFromRequest(request);
                if (boundForm.hasErrors()) {
                    return Results.ok(ScheduleNewCareView.render(boundForm, request, messages))
                        .flashing("danger", "controllers.parent.careController.scheduleNewCare.error");
                }

                final BookedCareForm form = boundForm.get();
                final ParentModel parent = account.getParent();

                final double cost;
                if (form.getCareType().equals(BookedCareType.ONE_TIME)) {
                    cost = this.pricingService.getOneTimeCareCost(form.getOneTimeCare());
                } else {
                    final List<CareForm> monthCare = new ArrayList<>();
                    form.getRecurrentCare().forEach(care -> {
                        monthCare.add(care);
                        for (int i = 1; i <= 3; i++) {
                            final CareForm newCare = new CareForm();
                            newCare.setDate(care.getDate().plusDays(i * 7));
                            newCare.setStartHour(care.getStartHour());
                            newCare.setDuration(care.getDurationInHours());
                            monthCare.add(newCare);
                        }
                    });

                    cost = this.pricingService.getRecurrentCareCost(monthCare);
                }
                final Long subsidizeAmount = this.cafService.calculateMonthlySupport(parent);

                final QuotationDTO quotationDTO = new QuotationDTO(parent, form, cost, subsidizeAmount, this.cafService, messages);

                final List<Http.Cookie> cookies = this.getBookedCareCookies(form);

                return Results.ok(ScheduleNewCareQuotationView.render(quotationDTO, request, messages)).withCookies(cookies.toArray(new Http.Cookie[]{}));
            }));
    }

    /**
     * Post schedule new care quotation result.
     *
     * @param request the request
     * @return the result
     */
    @SessionRequired(Role.ROLE_PARENT)
    public Result POST_ScheduleNewCareQuotation(final Http.Request request) {
        return this.withAccount(request, account ->
            this.withMessages(request, messages -> {
                final Optional<BookedCareForm> optionalBookedCareForm;
                try {
                    optionalBookedCareForm = Optional.of(this.getBookedCareFormFromCookies(request.cookies()));
                } catch (final CookieUnavailableException e) {
                    this.logger.debug("Cookies error during new care schedule", e);
                    return Results.redirect(routes.ParentController.GET_ParentDashboard())
                        .flashing("danger", "controllers.parent.careController.scheduleNewCare.error");
                }

                final BookedCareForm form = optionalBookedCareForm.orElseGet(BookedCareForm::new);
                final ParentModel parent = account.getParent();

                this.safeDB(() -> {
                    if (BookedCareType.ONE_TIME.equals(form.getCareType())) {
                        final Duration duration = new Duration(form.getOneTimeCare().getDuration());
                        final BookedCareModel bookedCare = new BookedCareModel(
                            BookedCareType.ONE_TIME,
                            form.getOneTimeCare().getStartDate(),
                            duration,
                            parent,
                            new CareCriteriaModel()
                        );
                        bookedCare.setEndDate(form.getOneTimeCare().getStartDate().plus(duration));
                        bookedCare.save();
                        this.actorSystem.scheduler().scheduleOnce(
                            scala.concurrent.duration.Duration.create(1, TimeUnit.SECONDS),
                            this.careCreatorActor,
                            new Check(bookedCare),
                            this.actorSystem.dispatcher(),
                            ActorRef.noSender()
                        );
                        this.careEmailService.newCareCreated(bookedCare, request);
                    } else if (BookedCareType.RECURRENT.equals(form.getCareType())) {
                        form.getRecurrentCare().forEach(recurrentCare -> {
                            final BookedCareModel bookedCare = new BookedCareModel(
                                BookedCareType.RECURRENT,
                                recurrentCare.getStartDate().isBefore(DateTime.now()) ? recurrentCare.getStartDate().plusWeeks(1) : recurrentCare.getStartDate(),
                                new Duration(recurrentCare.getDuration()),
                                parent,
                                new CareCriteriaModel()
                            );
                            bookedCare.save();
                            this.actorSystem.scheduler().scheduleOnce(
                                scala.concurrent.duration.Duration.create(1, TimeUnit.SECONDS),
                                this.careCreatorActor,
                                new Check(bookedCare),
                                this.actorSystem.dispatcher(),
                                ActorRef.noSender()
                            );
                            this.careEmailService.newCareCreated(bookedCare, request);
                        });
                    }
                });

                return Results.redirect(routes.ParentController.GET_ParentDashboard())
                    .flashing("success", "controllers.parent.careController.scheduleNewCare.success");
            })
        );
    }

    /**
     * Gets booked care cookies.
     *
     * @param onboardingForm the onboarding form
     * @return the booked care cookies
     */
    private List<Http.Cookie> getBookedCareCookies(final BookedCareForm onboardingForm) {
        final List<Http.CookieBuilder> cookieBuilders = new ArrayList<>();

        final String encoding = "UTF-8";

        try {
            cookieBuilders.add(Http.Cookie.builder("careType", URLEncoder.encode(onboardingForm.getCareType().name(), encoding)));

            if (onboardingForm.getCareType().equals(BookedCareType.ONE_TIME)) {
                cookieBuilders.add(Http.Cookie.builder("oneTimeCare.date", URLEncoder.encode(onboardingForm.getOneTimeCare().getDate().toString("dd/MM/YYYY"), encoding)));
                cookieBuilders.add(Http.Cookie.builder("oneTimeCare.startHour", URLEncoder.encode(onboardingForm.getOneTimeCare().getStartHour().toString("HH:mm"), encoding)));
                cookieBuilders.add(Http.Cookie.builder("oneTimeCare.duration", URLEncoder.encode(String.valueOf(onboardingForm.getOneTimeCare().getDurationInHours()), encoding)));
            } else {
                int recurrentCareIndex = 0;
                final String recurrentCareDateName = "recurrentCare.index.date";
                final String recurrentCareStartHourName = "recurrentCare.index.startHour";
                final String recurrentCareDurationName = "recurrentCare.index.duration";
                for (final CareForm careForm : onboardingForm.getRecurrentCare()) {
                    cookieBuilders.add(Http.Cookie.builder(
                        recurrentCareDateName.replace("index", Integer.toString(recurrentCareIndex)),
                        URLEncoder.encode(careForm.getDate().toString("dd/MM/YYYY"), encoding)
                    ));
                    cookieBuilders.add(Http.Cookie.builder(
                        recurrentCareStartHourName.replace("index", Integer.toString(recurrentCareIndex)),
                        URLEncoder.encode(careForm.getStartHour().toString("HH:mm"), encoding)
                    ));
                    cookieBuilders.add(Http.Cookie.builder(
                        recurrentCareDurationName.replace("index", Integer.toString(recurrentCareIndex)),
                        URLEncoder.encode(String.valueOf(careForm.getDurationInHours()), encoding)
                    ));
                    recurrentCareIndex++;
                }
                cookieBuilders.add(Http.Cookie.builder("recurrentCare.size", Integer.toString(recurrentCareIndex)));
            }
        } catch (final UnsupportedEncodingException e) {
            throw new RuntimeException("500");
        }

        cookieBuilders.forEach(cookieBuilder -> cookieBuilder.withMaxAge(java.time.Duration.ofMinutes(60)));

        return cookieBuilders.stream().map(Http.CookieBuilder::build).collect(Collectors.toList());
    }

    /**
     * Gets booked care form from cookies.
     *
     * @param cookies the cookies
     * @return the booked care form from cookies
     * @throws CookieUnavailableException the cookie unavailable exception
     */
    private BookedCareForm getBookedCareFormFromCookies(final Http.Cookies cookies) throws CookieUnavailableException {
        final BookedCareForm form = new BookedCareForm();
        final String encoding = "UTF-8";
        try {
            form.setCareType(BookedCareType.valueOf(URLDecoder.decode(cookies.get("careType").orElseThrow(CookieUnavailableException::new).value(), encoding)));

            final DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("dd/MM/YYYY");
            final DateTimeFormatter startHourFormatter = DateTimeFormat.forPattern("HH:mm");

            if (form.getCareType().equals(BookedCareType.ONE_TIME)) {
                final CareForm care = new CareForm();
                care.setDate(DateTime.parse(URLDecoder.decode(cookies.get("oneTimeCare.date").orElseThrow(CookieUnavailableException::new).value(), encoding), dateFormatter));
                care.setStartHour(DateTime.parse(URLDecoder.decode(cookies.get("oneTimeCare.startHour").orElseThrow(CookieUnavailableException::new).value(), encoding), startHourFormatter));
                care.setDuration(Double.valueOf(URLDecoder.decode(cookies.get("oneTimeCare.duration").orElseThrow(CookieUnavailableException::new).value(), encoding)));
                form.setOneTimeCare(care);
            } else if (form.getCareType().equals(BookedCareType.RECURRENT)) {
                final int recurrentCareSize = Integer.parseInt(cookies.get("recurrentCare.size").orElseThrow(CookieUnavailableException::new).value());
                final String recurrentCareDateName = "recurrentCare.index.date";
                final String recurrentCareStartHourName = "recurrentCare.index.startHour";
                final String recurrentCareDurationName = "recurrentCare.index.duration";

                final List<CareForm> recurrentCares = new ArrayList<>();
                for (int i = 0; i < recurrentCareSize; i++) {
                    final CareForm care = new CareForm();
                    care.setDate(DateTime.parse(URLDecoder.decode(cookies.get(recurrentCareDateName.replace("index", Integer.toString(i))).orElseThrow(CookieUnavailableException::new).value(), encoding), dateFormatter));
                    care.setStartHour(DateTime.parse(URLDecoder.decode(cookies.get(recurrentCareStartHourName.replace("index", Integer.toString(i))).orElseThrow(CookieUnavailableException::new).value(), encoding), startHourFormatter));
                    care.setDuration(Double.valueOf(URLDecoder.decode(cookies.get(recurrentCareDurationName.replace("index", Integer.toString(i))).orElseThrow(CookieUnavailableException::new).value(), encoding)));
                    recurrentCares.add(care);
                }
                form.setRecurrentCare(recurrentCares);
            }
        } catch (final UnsupportedEncodingException e) {
            throw new RuntimeException("500");
        }
        return form;
    }
}
