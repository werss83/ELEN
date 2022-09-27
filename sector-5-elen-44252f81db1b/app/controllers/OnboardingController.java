package controllers;

import actions.SessionOptional;
import actions.SessionRequired;
import actors.cares.payloads.Check;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import entities.caf.IncomeLimitsAmount;
import entities.caf.IncomeType;
import entities.dto.quotation.QuotationDTO;
import exceptions.CookieUnavailableException;
import form.accountservice.RegistrationForm;
import form.parent.onboarding.*;
import models.account.AccountModel;
import models.account.RoleModel;
import models.care.BookedCareModel;
import models.care.CareCriteriaModel;
import models.care.enumeration.BookedCareType;
import models.children.ChildrenModel;
import models.children.enumeration.ChildCategory;
import models.children.enumeration.UsualChildcare;
import models.parent.ParentCriteriaModel;
import models.parent.ParentModel;
import models.parent.ParentOptionModel;
import models.parent.enumeration.ParentIncomeGroup;
import models.parent.enumeration.ParentOptionKey;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.pac4j.play.java.Secure;
import org.pac4j.play.store.PlaySessionStore;
import play.data.Form;
import play.data.FormFactory;
import play.i18n.MessagesApi;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import security.Role;
import services.AccountService;
import services.CafService;
import services.CareEmailService;
import services.PricingService;
import views.html.front.parent.ParentOnboardingRegistrationView;
import views.html.front.parent.ParentOnboardingView;
import views.html.front.parent.ParentQuotationView;
import views.html.front.parent.ParentWelcomeView;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * OnboardingController.
 *
 * @author Lucas Stadelmann
 * @since 20.09.03
 */
public class OnboardingController extends AController {

    /**
     * The Account service.
     */
    private final AccountService accountService;

    /**
     * The Pricing service.
     */
    private final PricingService pricingService;

    /**
     * The Caf service.
     */
    private final CafService cafService;

    /**
     * The Care email service.
     */
    private final CareEmailService careEmailService;

    /**
     * The Actor system.
     */
    private final ActorSystem actorSystem;

    /**
     * The care creator actor.
     */
    private final ActorRef careCreatorActor;

    /**
     * Instantiates a new Onboarding controller.
     *
     * @param messagesApi      the messages api
     * @param formFactory      the form factory
     * @param playSessionStore the play session store
     * @param accountService   the account service
     * @param pricingService   the pricing service
     * @param cafService       the caf service
     */
    @Inject
    public OnboardingController(final MessagesApi messagesApi,
                                final FormFactory formFactory,
                                final PlaySessionStore playSessionStore,
                                final AccountService accountService,
                                final PricingService pricingService,
                                final CafService cafService,
                                final CareEmailService careEmailService,
                                final ActorSystem actorSystem,
                                @Named("CareCreatorActor") final ActorRef careCreatorActor) {
        super(messagesApi, formFactory, playSessionStore);
        this.accountService = accountService;
        this.pricingService = pricingService;
        this.cafService = cafService;
        this.careEmailService = careEmailService;
        this.actorSystem = actorSystem;
        this.careCreatorActor = careCreatorActor;
    }

    /**
     * Get parent onboarding result.
     *
     * @param request the request
     * @return the result
     */
    @SessionOptional
    public Result GET_ParentOnboarding(final Http.Request request) {
        return this.withOptionalAccount(request, optionalAccount -> {
            if (optionalAccount.isPresent()) {
                return Results.redirect(controllers.parent.routes.CareController.GET_ScheduleNewCare());
            }
            return this.withMessages(request, messages -> {
                final Form<OnboardingForm> onboardingForm = this.formFactory.form(OnboardingForm.class);
                final Form<RegistrationForm> registrationForm = this.formFactory.form(RegistrationForm.class);
                return Results.ok(ParentOnboardingView.render(onboardingForm, registrationForm, request, messages));
            });
        });
    }

    /**
     * Post parent standard onboarding completion stage.
     *
     * @param request the request
     * @return the completion stage
     */
    public CompletionStage<Result> POST_ParentStandardOnboarding(final Http.Request request) {
        return this.withMessages(request, messages -> {
            final Form<OnboardingForm> onboardingBoundForm = this.formFactory.form(OnboardingForm.class).bindFromRequest(request);
            final Form<RegistrationForm> registrationBoundForm = this.formFactory.form(RegistrationForm.class).bindFromRequest(request);
            if (onboardingBoundForm.hasErrors()) {
                return CompletableFuture.completedFuture(Results.ok(ParentOnboardingView.render(onboardingBoundForm, registrationBoundForm, request, messages)));
            }
            final OnboardingForm onboardingForm = onboardingBoundForm.get();
            final List<Http.Cookie> cookies = this.getRegistrationCookies(onboardingForm);

            final double cost;
            if (onboardingForm.getCareType().equals(BookedCareType.ONE_TIME)) {
                cost = this.pricingService.getOneTimeCareCost(onboardingForm.getOneTimeCare());
            } else {
                final List<CareForm> monthCare = new ArrayList<>();
                onboardingForm.getRecurrentCare().forEach(care -> {
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
            final Long subsidizeAmount = this.cafService.calculateMonthlySupport(
                onboardingForm.getParentWorking(),
                onboardingForm.getChildren(),
                onboardingForm.getChildrenCategories(),
                onboardingForm.getIncome(),
                onboardingForm.getSingleParent(),
                false,
                onboardingForm.getDisabledParent(),
                onboardingForm.getDisabledChildBenefit()
            );

            return this.accountService.PostRegistrationForm(
                request,
                (messagesBis, form) -> Results.ok(ParentOnboardingView.render(onboardingBoundForm, registrationBoundForm, request, messagesBis)),
                account -> this.createNewParentWithChildren(account, onboardingForm),
                account -> Results.ok(ParentQuotationView.render(new QuotationDTO(onboardingForm, account, cost, subsidizeAmount, this.cafService, messages), request, messages)).withCookies(cookies.toArray(new Http.Cookie[]{}))
            );
        });
    }

    /**
     * Post parent google onboarding result.
     *
     * @param request the request
     * @return the result
     */
    public Result POST_ParentGoogleOnboarding(final Http.Request request) {
        return this.withMessages(request, messages -> {
            final Form<OnboardingForm> onboardingBoundForm = this.formFactory.form(OnboardingForm.class).bindFromRequest(request);
            if (onboardingBoundForm.hasErrors()) {
                return Results.ok(ParentOnboardingView.render(onboardingBoundForm, this.formFactory.form(RegistrationForm.class), request, messages));
            }
            final OnboardingForm onboardingForm = onboardingBoundForm.get();
            final List<Http.Cookie> cookies = this.getRegistrationCookies(onboardingForm);

            return Results.redirect(routes.OnboardingController.GET_ParentOnboardingGoogleRegistration()).withCookies(cookies.toArray(new Http.Cookie[]{}));
        });
    }

    /**
     * Get parent onboarding google registration result.
     *
     * @param request the request
     * @return the result
     */
    @Secure(clients = "GoogleClient")
    public Result GET_ParentOnboardingGoogleRegistration(final Http.Request request) {
        final Optional<RegistrationForm> optionalRegistrationForm;
        try {
            optionalRegistrationForm = Optional.of(this.getRegistrationFormFromCookies(request.cookies()));
        } catch (final CookieUnavailableException e) {
            this.logger.debug("Cookies error during social registration", e);
            return Results.redirect(routes.OnboardingController.GET_ParentOnboarding());
        }

        return this.accountService.GetGoogleAuth(
            request,
            optionalRegistrationForm.orElseGet(RegistrationForm::new),
            (messages, form) -> Results.ok(ParentOnboardingRegistrationView.render(form, routes.OnboardingController.POST_ParentOnboardingGoogleRegistration().url(), request, messages)),
            null,
            account -> Results.redirect(routes.OnboardingController.GET_ParentOnboardingGoogleRegistration())
        );
    }

    /**
     * Post parent onboarding google registration completion stage.
     *
     * @param request the request
     * @return the completion stage
     */
    public CompletionStage<Result> POST_ParentOnboardingGoogleRegistration(final Http.Request request) {
        return this.withMessages(request, messages -> {
            final Optional<OnboardingForm> optionalOnboardingForm;
            try {
                optionalOnboardingForm = Optional.of(this.getOnboardingFormFromCookies(request.cookies()));
            } catch (final CookieUnavailableException e) {
                this.logger.debug("Cookies error during social registration", e);
                return CompletableFuture.completedFuture(Results.redirect(routes.OnboardingController.GET_ParentOnboarding()));
            }
            final Form<OnboardingForm> boundForm = this.formFactory.form(OnboardingForm.class)
                .fill(optionalOnboardingForm.orElseGet(OnboardingForm::new));

            final OnboardingForm onboardingForm = boundForm.get();

            final double cost;
            if (onboardingForm.getCareType().equals(BookedCareType.ONE_TIME)) {
                cost = this.pricingService.getOneTimeCareCost(onboardingForm.getOneTimeCare());
            } else {
                final List<CareForm> monthCare = new ArrayList<>();
                onboardingForm.getRecurrentCare().forEach(care -> {
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
            final Long subsidizeAmount = this.cafService.calculateMonthlySupport(
                onboardingForm.getParentWorking(),
                onboardingForm.getChildren(),
                onboardingForm.getChildrenCategories(),
                onboardingForm.getIncome(),
                onboardingForm.getSingleParent(),
                false,
                onboardingForm.getDisabledParent(),
                onboardingForm.getDisabledChildBenefit()
            );

            return this.accountService.PostGoogleAuth(
                request,
                (messagesBis, form) -> Results.ok(ParentOnboardingRegistrationView.render(form, routes.OnboardingController.POST_ParentOnboardingGoogleRegistration().url(), request, messagesBis)),
                account -> this.createNewParentWithChildren(account, optionalOnboardingForm.orElseGet(OnboardingForm::new)),
                account -> Results.ok(ParentQuotationView.render(new QuotationDTO(onboardingForm, account, cost, subsidizeAmount, this.cafService, messages), request, messages))
            );
        });
    }

    /**
     * Post parent facebook onboarding result.
     *
     * @param request the request
     * @return the result
     */
    public Result POST_ParentFacebookOnboarding(final Http.Request request) {
        return this.withMessages(request, messages -> {
            final Form<OnboardingForm> onboardingBoundForm = this.formFactory.form(OnboardingForm.class).bindFromRequest(request);
            if (onboardingBoundForm.hasErrors()) {
                return Results.ok(ParentOnboardingView.render(onboardingBoundForm, this.formFactory.form(RegistrationForm.class), request, messages));
            }
            final OnboardingForm onboardingForm = onboardingBoundForm.get();
            final List<Http.Cookie> cookies = this.getRegistrationCookies(onboardingForm);

            return Results.redirect(routes.OnboardingController.GET_ParentOnboardingFacebookRegistration()).withCookies(cookies.toArray(new Http.Cookie[]{}));
        });
    }

    /**
     * Get parent onboarding facebook registration result.
     *
     * @param request the request
     * @return the result
     */
    @Secure(clients = "FacebookClient")
    public Result GET_ParentOnboardingFacebookRegistration(final Http.Request request) {
        final Optional<RegistrationForm> optionalRegistrationForm;
        try {
            optionalRegistrationForm = Optional.of(this.getRegistrationFormFromCookies(request.cookies()));
        } catch (final CookieUnavailableException e) {
            this.logger.debug("Cookies error during social registration", e);
            return Results.redirect(routes.OnboardingController.GET_ParentOnboarding());
        }

        return this.accountService.GetFacebookAuth(
            request,
            optionalRegistrationForm.orElseGet(RegistrationForm::new),
            (messages, form) -> Results.ok(ParentOnboardingRegistrationView.render(form, routes.OnboardingController.POST_ParentOnboardingFacebookRegistration().url(), request, messages)),
            null,
            account -> Results.redirect(routes.OnboardingController.GET_ParentOnboardingFacebookRegistration())
        );
    }

    /**
     * Post parent onboarding facebook registration completion stage.
     *
     * @param request the request
     * @return the completion stage
     */
    public CompletionStage<Result> POST_ParentOnboardingFacebookRegistration(final Http.Request request) {
        return this.withMessages(request, messages -> {
            final Optional<OnboardingForm> optionalOnboardingForm;
            try {
                optionalOnboardingForm = Optional.of(this.getOnboardingFormFromCookies(request.cookies()));
            } catch (final CookieUnavailableException e) {
                this.logger.debug("Cookies error during social registration", e);
                return CompletableFuture.completedFuture(Results.redirect(routes.OnboardingController.GET_ParentOnboarding()));
            }
            final Form<OnboardingForm> boundForm = this.formFactory.form(OnboardingForm.class)
                .fill(optionalOnboardingForm.orElseGet(OnboardingForm::new));

            final OnboardingForm onboardingForm = boundForm.get();

            final double cost;
            if (onboardingForm.getCareType().equals(BookedCareType.ONE_TIME)) {
                cost = this.pricingService.getOneTimeCareCost(onboardingForm.getOneTimeCare());
            } else {
                final List<CareForm> monthCare = new ArrayList<>();
                onboardingForm.getRecurrentCare().forEach(care -> {
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
            final Long subsidizeAmount = this.cafService.calculateMonthlySupport(
                onboardingForm.getParentWorking(),
                onboardingForm.getChildren(),
                onboardingForm.getChildrenCategories(),
                onboardingForm.getIncome(),
                onboardingForm.getSingleParent(),
                false,
                onboardingForm.getDisabledParent(),
                onboardingForm.getDisabledChildBenefit()
            );

            return this.accountService.PostFacebookAuth(
                request,
                (messagesBis, form) -> Results.ok(ParentOnboardingRegistrationView.render(form, routes.OnboardingController.POST_ParentOnboardingFacebookRegistration().url(), request, messagesBis)),
                account -> this.createNewParentWithChildren(account, optionalOnboardingForm.orElseGet(OnboardingForm::new)),
                account -> Results.ok(ParentQuotationView.render(new QuotationDTO(onboardingForm, account, cost, subsidizeAmount, this.cafService, messages), request, messages))
            );
        });
    }

    /**
     * Post parent onboarding quotation result.
     *
     * @param request the request
     * @return the result
     */
    @SessionRequired
    public Result POST_ParentOnboardingQuotation(final Http.Request request) {
        return this.withAccount(request, account ->
            this.withMessages(request, messages -> {
                final Optional<OnboardingForm> optionalOnboardingForm;
                try {
                    optionalOnboardingForm = Optional.of(this.getOnboardingFormFromCookies(request.cookies()));
                } catch (final CookieUnavailableException e) {
                    this.logger.debug("Cookies error during social registration", e);
                    return Results.redirect(routes.OnboardingController.GET_ParentOnboarding());
                }

                this.safeDB(() -> {
                    final ParentModel parent = account.getParent();

                    this.createCares(parent, optionalOnboardingForm.orElseGet(OnboardingForm::new), request);
                    parent.save();
                });

                return Results.ok(ParentWelcomeView.render(request, messages));
            })
        );
    }

    /**
     * Post get income limit ajax result.
     *
     * @param request the request
     * @return the result
     */
    public Result POST_GetIncomeLimitAJAX(final Http.Request request) {
        final Form<IncomeLimitForm> boundForm = this.formFactory.form(IncomeLimitForm.class).bindFromRequest(request);
        if (boundForm.hasErrors()) {
            return Results.badRequest();
        }

        final IncomeLimitForm form = boundForm.get();
        final IncomeLimitsAmount incomeLimitsAmount = this.cafService.getIncomeLimitsAmount(form.getSingleParent(), form.getChildrenNumber());

        return Results.ok(Json.toJson(incomeLimitsAmount));
    }

    /**
     * Post get email available ajax result.
     *
     * @param request the request
     * @return the result
     */
    public Result POST_GetEmailAvailableAJAX(final Http.Request request) {
        return this.withMessages(request, messages -> {
            final Form<EmailAvailableForm> boundForm = this.formFactory.form(EmailAvailableForm.class).bindFromRequest(request);

            final Map<String, Object> result = new HashMap<>();

            if (boundForm.hasErrors()) {
                result.put("available", false);
                result.put("error", messages.apply(boundForm.errors().get(0).message()));
            } else {
                result.put("available", true);
            }

            return Results.ok(Json.toJson(result));
        });
    }

    /**
     * Post get subsidize amount ajax result.
     *
     * @param request the request
     * @return the result
     */
    public Result POST_GetSubsidizeAmountAJAX(final Http.Request request) {
        final Form<MonthlySupportForm> boundForm = this.formFactory.form(MonthlySupportForm.class).bindFromRequest(request);

        final Map<String, Long> result = new HashMap<>();

        if (boundForm.hasErrors()) {
            result.put("subsidizeAmount", 0L);
        } else {
            final MonthlySupportForm form = boundForm.get();
            final Long subsidizeAmount = this.cafService.calculateMonthlySupport(
                form.getParentWorking(),
                form.getChildren(),
                form.getChildCategories(),
                form.getIncome(),
                form.getSingleParent(),
                form.getSharedCare(),
                form.getDisabledParent(),
                form.getDisabledChildBenefit()
            );
            result.put("subsidizeAmount", subsidizeAmount);
        }

        return Results.ok(Json.toJson(result));
    }

    /**
     * Create new parent with children.
     *
     * @param account        the account
     * @param onboardingForm the onboarding form
     */
    private void createNewParentWithChildren(final AccountModel account,
                                             final OnboardingForm onboardingForm) {
        final RoleModel role = new RoleModel(account, Role.ROLE_PARENT);
        role.save();

        final ParentModel parent = new ParentModel(account, new ParentCriteriaModel());
        parent.save();

        final List<ChildrenModel> children = new ArrayList<>();
        onboardingForm.getChildren().forEach(childrenForm -> children.add(new ChildrenModel(childrenForm.getCategory(), childrenForm.getChildcare(), parent)));
        parent.setChildren(children);
        parent.save();

        final ParentOptionModel singleParent = new ParentOptionModel(ParentOptionKey.SINGLE_PARENT, parent);
        singleParent.setValueAsBoolean(onboardingForm.getSingleParent());
        singleParent.save();

        final ParentOptionModel parentWorking = new ParentOptionModel(ParentOptionKey.PARENT_WORKING, parent);
        parentWorking.setValueAsBoolean(onboardingForm.getParentWorking());
        parentWorking.save();

        final ParentOptionModel disabledParent = new ParentOptionModel(ParentOptionKey.DISABLED_PARENT, parent);
        disabledParent.setValueAsBoolean(onboardingForm.getDisabledParent());
        disabledParent.save();

        final ParentOptionModel disabledChildBenefit = new ParentOptionModel(ParentOptionKey.DISABLED_CHILD_BENEFIT, parent);
        disabledChildBenefit.setValueAsBoolean(onboardingForm.getDisabledChildBenefit());
        disabledChildBenefit.save();

        final ParentOptionModel incomeGroup = new ParentOptionModel(ParentOptionKey.INCOME_GROUP, parent);
        incomeGroup.setValueAsIncomeGroup(ParentIncomeGroup.getAppropriateIncomeGroup(onboardingForm.getIncome()));
        incomeGroup.save();

        final ParentOptionModel cafNumber = new ParentOptionModel(ParentOptionKey.CAF_NUMBER, parent);
        cafNumber.setValueAsString(onboardingForm.getCafNumber());
        cafNumber.save();
    }

    /**
     * Create cares.
     *
     * @param parent         the parent
     * @param onboardingForm the onboarding form
     */
    private void createCares(final ParentModel parent,
                             final OnboardingForm onboardingForm,
                             final Http.Request request) {
        if (BookedCareType.ONE_TIME.equals(onboardingForm.getCareType())) {
            final Duration duration = new Duration(onboardingForm.getOneTimeCare().getDuration());
            final BookedCareModel bookedCare = new BookedCareModel(
                BookedCareType.ONE_TIME,
                onboardingForm.getOneTimeCare().getStartDate(),
                duration,
                parent,
                new CareCriteriaModel()
            );
            bookedCare.setEndDate(onboardingForm.getOneTimeCare().getStartDate().plus(duration));
            bookedCare.save();
            this.actorSystem.scheduler().scheduleOnce(
                scala.concurrent.duration.Duration.create(1, TimeUnit.SECONDS),
                this.careCreatorActor,
                new Check(bookedCare),
                this.actorSystem.dispatcher(),
                ActorRef.noSender()
            );
            this.careEmailService.newCareCreated(bookedCare, request);
        } else if (BookedCareType.RECURRENT.equals(onboardingForm.getCareType())) {
            onboardingForm.getRecurrentCare().forEach(recurrentCare -> {
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
    }

    /**
     * Gets registration cookies.
     *
     * @param onboardingForm the onboarding form
     * @return the registration cookies
     */
    private List<Http.Cookie> getRegistrationCookies(final OnboardingForm onboardingForm) {
        final List<Http.CookieBuilder> cookieBuilders = new ArrayList<>();

        final String encoding = "UTF-8";

        try {
            cookieBuilders.add(Http.Cookie.builder("address", URLEncoder.encode(onboardingForm.getAddress(), encoding)));
            cookieBuilders.add(Http.Cookie.builder("city", URLEncoder.encode(onboardingForm.getCity(), encoding)));
            cookieBuilders.add(Http.Cookie.builder("zipCode", URLEncoder.encode(onboardingForm.getZipCode(), encoding)));
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

            int childrenIndex = 0;
            final String childrenCategoryName = "children.index.category";
            final String childrenChildcareName = "children.index.childcare";
            for (final ChildrenForm childrenForm : onboardingForm.getChildren()) {
                cookieBuilders.add(Http.Cookie.builder(
                    URLEncoder.encode(childrenCategoryName.replace("index", Integer.toString(childrenIndex)), encoding),
                    URLEncoder.encode(childrenForm.getCategory().name(), encoding)
                ));
                cookieBuilders.add(Http.Cookie.builder(
                    URLEncoder.encode(childrenChildcareName.replace("index", Integer.toString(childrenIndex)), encoding),
                    URLEncoder.encode(childrenForm.getChildcare().name(), encoding)
                ));
                childrenIndex++;
            }
            cookieBuilders.add(Http.Cookie.builder("children.size", Integer.toString(childrenIndex)));

            cookieBuilders.add(Http.Cookie.builder("singleParent", URLEncoder.encode(onboardingForm.getSingleParent().toString(), encoding)));
            cookieBuilders.add(Http.Cookie.builder("parentWorking", URLEncoder.encode(onboardingForm.getParentWorking().toString(), encoding)));
            cookieBuilders.add(Http.Cookie.builder("disabledParent", URLEncoder.encode(onboardingForm.getDisabledParent().toString(), encoding)));
            cookieBuilders.add(Http.Cookie.builder("disabledChildBenefit", URLEncoder.encode(onboardingForm.getDisabledChildBenefit().toString(), encoding)));
            cookieBuilders.add(Http.Cookie.builder("income", URLEncoder.encode(onboardingForm.getIncome().name(), encoding)));
            cookieBuilders.add(Http.Cookie.builder("cafNumber", URLEncoder.encode(onboardingForm.getCafNumber(), encoding)));
        } catch (final UnsupportedEncodingException e) {
            throw new RuntimeException("500");
        }

        cookieBuilders.forEach(cookieBuilder -> cookieBuilder.withMaxAge(java.time.Duration.ofMinutes(60)));

        return cookieBuilders.stream().map(Http.CookieBuilder::build).collect(Collectors.toList());
    }

    /**
     * Gets registration form from cookies.
     *
     * @param cookies the cookies
     * @return the registration form from cookies
     * @throws CookieUnavailableException the cookie unavailable exception
     */
    private RegistrationForm getRegistrationFormFromCookies(final Http.Cookies cookies) throws CookieUnavailableException {
        final RegistrationForm form = new RegistrationForm();
        final String encoding = "UTF-8";
        try {
            form.setAddress(URLDecoder.decode(cookies.get("address").orElseThrow(CookieUnavailableException::new).value(), encoding));
            form.setZipCode(URLDecoder.decode(cookies.get("zipCode").orElseThrow(CookieUnavailableException::new).value(), encoding));
            form.setCity(URLDecoder.decode(cookies.get("city").orElseThrow(CookieUnavailableException::new).value(), encoding));
        } catch (final UnsupportedEncodingException e) {
            throw new RuntimeException("500");
        }
        return form;
    }

    /**
     * Gets onboarding form from cookies.
     *
     * @param cookies the cookies
     * @return the onboarding form from cookies
     * @throws CookieUnavailableException the cookie unavailable exception
     */
    private OnboardingForm getOnboardingFormFromCookies(final Http.Cookies cookies) throws CookieUnavailableException {
        final OnboardingForm form = new OnboardingForm();
        final String encoding = "UTF-8";
        try {
            form.setAddress(URLDecoder.decode(cookies.get("address").orElseThrow(CookieUnavailableException::new).value(), encoding));
            form.setZipCode(URLDecoder.decode(cookies.get("zipCode").orElseThrow(CookieUnavailableException::new).value(), encoding));
            form.setCity(URLDecoder.decode(cookies.get("city").orElseThrow(CookieUnavailableException::new).value(), encoding));
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

            final int childrenIndex = Integer.parseInt(cookies.get("children.size").orElseThrow(CookieUnavailableException::new).value());
            final String childrenCategoryName = "children.index.category";
            final String childrenChildcareName = "children.index.childcare";

            final List<ChildrenForm> children = new ArrayList<>();
            for (int i = 0; i < childrenIndex; i++) {
                final ChildrenForm child = new ChildrenForm();
                child.setCategory(ChildCategory.valueOf(URLDecoder.decode(cookies.get(childrenCategoryName.replace("index", Integer.toString(i))).orElseThrow(CookieUnavailableException::new).value(), encoding)));
                child.setUsualChildcare(UsualChildcare.valueOf(URLDecoder.decode(cookies.get(childrenChildcareName.replace("index", Integer.toString(i))).orElseThrow(CookieUnavailableException::new).value(), encoding)));
                children.add(child);
            }
            form.setChildren(children);

            form.setSingleParent(Boolean.valueOf(URLDecoder.decode(cookies.get("singleParent").orElseThrow(CookieUnavailableException::new).value(), encoding)));
            form.setParentWorking(Boolean.valueOf(URLDecoder.decode(cookies.get("parentWorking").orElseThrow(CookieUnavailableException::new).value(), encoding)));
            form.setDisabledParent(Boolean.valueOf(URLDecoder.decode(cookies.get("disabledParent").orElseThrow(CookieUnavailableException::new).value(), encoding)));
            form.setDisabledChildBenefit(Boolean.valueOf(URLDecoder.decode(cookies.get("disabledChildBenefit").orElseThrow(CookieUnavailableException::new).value(), encoding)));
            form.setIncome(IncomeType.valueOf(URLDecoder.decode(cookies.get("income").orElseThrow(CookieUnavailableException::new).value(), encoding)));
            form.setCafNumber(URLDecoder.decode(cookies.get("cafNumber").orElseThrow(CookieUnavailableException::new).value(), encoding));
        } catch (final UnsupportedEncodingException e) {
            throw new RuntimeException("500");
        }
        return form;
    }
}
