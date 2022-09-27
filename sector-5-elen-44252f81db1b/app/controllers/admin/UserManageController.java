package controllers.admin;

import actions.SessionRequired;
import behavior.PacProfileForger;
import com.jackson42.play.ebeandatatables.EbeanDataTablesQuery;
import controllers.AController;
import form.admin.PasswordForm;
import form.admin.RoleForm;
import form.parent.ParentCriteriaForm;
import form.profile.ProfileForm;
import form.sitter.SitterAttributesForm;
import io.ebean.DB;
import io.ebean.Expr;
import io.ebean.QueryIterator;
import models.account.AccountModel;
import models.care.EffectiveCareModel;
import models.children.ChildrenModel;
import models.children.enumeration.ChildCategory;
import models.parent.ParentCriteriaModel;
import models.parent.ParentModel;
import models.parent.ParentOptionModel;
import models.parent.enumeration.ParentOptionKey;
import models.sitter.SitterAttributeModel;
import models.sitter.SitterModel;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.pac4j.play.store.PlaySessionStore;
import play.data.Form;
import play.data.FormFactory;
import play.i18n.MessagesApi;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import play.twirl.api.Html;
import security.Role;
import services.GoogleService;
import views.html.back.admin.users.*;

import javax.inject.Inject;
import javax.validation.groups.Default;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

/**
 * AccountManageController.
 *
 * @author Pierre Adam
 * @since 20.06.28
 */
public class UserManageController extends AController implements PacProfileForger {

    /**
     * The Account data tables.
     */
    private final EbeanDataTablesQuery<AccountModel> accountDataTables;

    /**
     * The Google service.
     */
    private final GoogleService googleService;

    /**
     * Instantiates a new A controller.
     *
     * @param messagesApi      the messages api
     * @param formFactory      the form factory
     * @param playSessionStore the play session store
     * @param googleService    the google service
     */
    @Inject
    public UserManageController(final MessagesApi messagesApi,
                                final FormFactory formFactory,
                                final PlaySessionStore playSessionStore,
                                final GoogleService googleService) {
        super(messagesApi, formFactory, playSessionStore);

        this.googleService = googleService;

        // Build the logic for the AJAX list.
        this.accountDataTables = new EbeanDataTablesQuery<>(AccountModel.find, AccountModel.class);

        // Column name
        this.accountDataTables.setFieldDisplaySupplier("name", AccountModel::getFullName);
        this.accountDataTables.setSearchHandler("name",
            (query, searchTerm) -> query.ilike("unaccent(concat(first_name, ' ', last_name, ' ', email))",
                String.format("%%%s%%", StringUtils.stripAccents(searchTerm))));
        this.accountDataTables.setOrderHandler("name", (query, orderEnum) ->
            query.order(String.format("firstName %s, lastName %s", orderEnum.name(), orderEnum.name())));

        // Column phoneNumber
        this.accountDataTables.setSearchHandler("phoneNumber", (query, searchTerm) -> {
            if (searchTerm.startsWith("0") && searchTerm.length() > 1) {
                query.or(
                    Expr.ilike("phoneNumber", String.format("%%%s%%", searchTerm)),
                    Expr.ilike("phoneNumber", String.format("%%%s%%", searchTerm.substring(1)))
                );
            } else {
                query.ilike("phoneNumber", String.format("%%%s%%", searchTerm));
            }
        });

        // Column address
        this.accountDataTables.setFieldDisplayHtmlSupplier("address",
            (account, request) -> this.withMessages(request, messages -> UserAddressDisplay.render(account, request, messages)));
        this.accountDataTables.setSearchHandler("address",
            (query, searchTerm) -> query.ilike("unaccent(concat(address, ' ', additionalAddress, ' ', zipCode, ' ', city))",
                String.format("%%%s%%", StringUtils.stripAccents(searchTerm))));

        final Function<ParentModel, Boolean> hasChildUnder6yo = (ParentModel parent) -> {
            for (final ChildrenModel child : parent.getChildren()) {
                switch (child.getCategory()) {
                    case UNDER_3_YEARS:
                    case UNDER_6_YEARS:
                        return true;
                    case BIRTHDAY:
                        if (child.getBirthDate().isAfter(DateTime.now().withMillisOfDay(0).minusYears(6).toLocalDate())) {
                            return true;
                        }
                        break;
                    default:
                        break;
                }
            }
            return false;
        };
        final Function<ParentModel, Boolean> hasChildUnder3yo = (ParentModel parent) -> {
            for (final ChildrenModel child : parent.getChildren()) {
                switch (child.getCategory()) {
                    case UNDER_3_YEARS:
                        return true;
                    case BIRTHDAY:
                        if (child.getBirthDate().isAfter(DateTime.now().withMillisOfDay(0).minusYears(6).toLocalDate())) {
                            return true;
                        }
                        break;
                    default:
                        break;
                }
            }
            return false;
        };

        // Hours
        this.accountDataTables.setFieldDisplayHtmlSupplier("hours",
            (account, request) -> this.withMessages(request, messages -> {
                final ParentModel parent = account.getParent();

                if (parent == null) {
                    return new Html("-");
                }

                Duration total = new Duration(0);

                final DateTime start = DateTime.now().withMillisOfDay(0).withDayOfMonth(1);
                final QueryIterator<EffectiveCareModel> iterator = EffectiveCareModel.find
                    .query()
                    .where()
                    .eq("bookedCare.parent", parent)
                    .ge("startDate", start)
                    .lt("startDate", start.plusMonths(1))
                    .findIterate();

                while (iterator.hasNext()) {
                    final EffectiveCareModel ec = iterator.next();
                    total = total.plus(ec.getDuration());
                }
                final Double hours = (double) total.getMillis() / 3600000.0;

                return UserHoursDisplay.render(account, hours, hasChildUnder6yo.apply(parent), request, messages);
            }));

        // Problems
        this.accountDataTables.setFieldDisplayHtmlSupplier("problems",
            (account, request) -> this.withMessages(request, messages -> {
                final ParentModel parent = account.getParent();

                if (parent == null) {
                    return new Html("-");
                }

                final boolean noChildren = parent.getChildren().size() <= 0;

                if (!noChildren && !hasChildUnder6yo.apply(parent)) {
                    return new Html("-");
                }

                final String cafNumber = ParentOptionModel.getOption(parent, ParentOptionKey.CAF_NUMBER).getValueAsString();
                final boolean cafNumberMissing = cafNumber == null || cafNumber.isEmpty();
                final boolean under3yo = hasChildUnder3yo.apply(parent);
                final boolean noBooking = EffectiveCareModel.find.query().where().eq("bookedCare.parent", parent).setMaxRows(1).findCount() == 0;
                boolean incompleteChildrenProfile = false;

                for (final ChildrenModel child : parent.getChildren()) {
                    if (child.getCategory() != ChildCategory.BIRTHDAY) {
                        incompleteChildrenProfile = true;
                        break;
                    }
                }

                return UserProblemsDisplay.render(account, cafNumberMissing, incompleteChildrenProfile, noChildren, under3yo, noBooking, request, messages);
            }));

        // Column roles
        this.accountDataTables.setFieldDisplayHtmlSupplier("roles",
            (account, request) -> this.withMessages(request, messages -> UserRolesDisplay.render(account, request, messages)));
        this.accountDataTables.setSearchHandler("roles",
            (query, searchTerm) -> {
                try {
                    query.eq("roles.role", Role.valueOf(searchTerm));
                } catch (final IllegalArgumentException ignore) {
                }
            });

        // Column actions
        this.accountDataTables.setFieldDisplayHtmlSupplier("actions",
            (account, request) -> this.withMessages(request, messages -> ActionsDisplay.render(account, request, messages)));
    }

    /**
     * Get accounts result.
     *
     * @param request the request
     * @return the result
     */
    @SessionRequired(Role.ROLE_ADMIN)
    public Result GET_Users(final Http.Request request) {
        return this.withMessages(request, messages ->
            Results.ok(UsersListView.render(request, messages))
        );
    }

    /**
     * Post user ajax list result.
     *
     * @param request the request
     * @return the result
     */
    @SessionRequired(Role.ROLE_ADMIN)
    public Result POST_UserAjaxList(final Http.Request request) {
        return this.dataTablesAjaxRequest(request,
            boundForm -> Results.badRequest(),
            form -> Results.ok(this.accountDataTables.getAjaxResult(request, form.getParameters()))
        );
    }

    /**
     * Post user ajax list result.
     *
     * @param request the request
     * @param account the account
     * @return the result
     */
    @SessionRequired(Role.ROLE_ADMIN)
    public Result POST_DeleteUser(final Http.Request request, final AccountModel account) {
        account.delete();
        return Results.redirect(routes.UserManageController.GET_Users())
            .flashing("success", "controllers.admin.user.deleted");
    }

    /**
     * Get new user result.
     *
     * @param request the request
     * @return the result
     */
    @SessionRequired(Role.ROLE_ADMIN)
    public Result GET_NewUser(final Http.Request request) {
        return this.withMessages(request, messages -> {
            final Form<ProfileForm> boundProfileForm = this.formFactory
                .form(ProfileForm.class, Default.class);
            final Form<RoleForm> boundRoleForm = this.formFactory
                .form(RoleForm.class);
            final Form<PasswordForm> boundPasswordForm = this.formFactory
                .form(PasswordForm.class, PasswordForm.Groups.NoConfirmation.class);
            final Form<ParentCriteriaForm> boundParentForm = this.formFactory
                .form(ParentCriteriaForm.class);
            final Form<SitterAttributesForm> boundSitterForm = this.formFactory
                .form(SitterAttributesForm.class);
            return Results.ok(NewUserView.render(boundProfileForm, boundRoleForm, boundPasswordForm,
                boundParentForm, boundSitterForm, request, messages));
        });
    }

    /**
     * Post new user completion stage.
     *
     * @param request the request
     * @return the completion stage
     */
    @SessionRequired(Role.ROLE_ADMIN)
    public CompletionStage<Result> POST_NewUser(final Http.Request request) {
        return this.withMessages(request, messages -> {
            final Form<ProfileForm> boundProfileForm = this.formFactory
                .form(ProfileForm.class, Default.class)
                .bindFromRequest(request);
            final Form<RoleForm> boundRoleForm = this.formFactory
                .form(RoleForm.class)
                .bindFromRequest(request);
            final Form<PasswordForm> boundPasswordForm = this.formFactory
                .form(PasswordForm.class, PasswordForm.Groups.NoConfirmation.class)
                .bindFromRequest(request);
            final Form<ParentCriteriaForm> boundParentForm = this.formFactory
                .form(ParentCriteriaForm.class)
                .bindFromRequest(request);
            final Form<SitterAttributesForm> boundSitterForm = this.formFactory
                .form(SitterAttributesForm.class)
                .bindFromRequest(request);

            if (boundProfileForm.hasErrors() || boundRoleForm.hasErrors() || boundPasswordForm.hasErrors()
                || boundParentForm.hasErrors() || boundSitterForm.hasErrors()) {
                return CompletableFuture.completedFuture(
                    Results.ok(NewUserView.render(boundProfileForm, boundRoleForm, boundPasswordForm,
                        boundParentForm, boundSitterForm, request, messages))
                );
            }

            final ProfileForm profileForm = boundProfileForm.get();
            final RoleForm roleForm = boundRoleForm.get();
            final PasswordForm passwordForm = boundPasswordForm.get();
            final ParentCriteriaForm parentForm = boundParentForm.get();
            final SitterAttributesForm sitterForm = boundSitterForm.get();

            return this.googleService.addressToCoordinate(null, profileForm)
                .handle((coordinate, throwable) -> {
                    if (throwable != null) {
                        if (throwable.getCause() instanceof GoogleService.NoResultException) {
                            final Form<ProfileForm> profileWithError = boundProfileForm.withError("address", "error.invalid");
                            return Results.ok(NewUserView.render(profileWithError, boundRoleForm, boundPasswordForm,
                                boundParentForm, boundSitterForm, request, messages));
                        }
                        throw new RuntimeException(throwable);
                    }

                    //this.safeDB(() -> {
                    final AccountModel account = new AccountModel(profileForm.getFirstName(), profileForm.getLastName(), profileForm.getEmail(),
                        profileForm.getAddress(), profileForm.getCity(), profileForm.getPostalCode(), profileForm.getPhoneNumber());
                    account.setAdditionalAddress(profileForm.getAdditionalAddress());
                    account.setCoordinate(coordinate);
                    passwordForm.updateAccount(account);
                    account.setEmailVerified(true);
                    account.save();

                    roleForm.updateAccountRoles(account);

                    if (account.hasRole(Role.ROLE_PARENT)) {
                        final ParentCriteriaModel parentCriteria = new ParentCriteriaModel();
                        final ParentModel parent = new ParentModel(account);

                        parent.save();
                        parentCriteria.setParent(parent);
                        parentForm.updateCriteria(parentCriteria);
                        parentCriteria.save();
                    }
                    if (account.hasRole(Role.ROLE_SITTER)) {
                        final SitterAttributeModel sitterAttributes = new SitterAttributeModel();
                        final SitterModel sitter = new SitterModel(account);

                        sitter.save();
                        sitterAttributes.setSitter(sitter);
                        sitterForm.updateAttributes(sitter);
                        sitterAttributes.save();
                    }
                    //});

                    return Results.redirect(routes.UserManageController.GET_Users());
                });
        });
    }

    /**
     * Get the user profile.
     *
     * @param request the request
     * @param account the account
     * @return the result
     */
    @SessionRequired(Role.ROLE_ADMIN)
    public Result GET_User(final Http.Request request, final AccountModel account) {
        return this.withMessages(request, messages -> {
                final Form<ProfileForm> boundProfileForm = this.formFactory
                    .form(ProfileForm.class, ProfileForm.Groups.smartGroup(account))
                    .fill(new ProfileForm(account));
                final Form<RoleForm> boundRoleForm = this.formFactory.form(RoleForm.class)
                    .fill(new RoleForm(account));
                final Form<ParentCriteriaForm> boundParentForm = this.formFactory.form(ParentCriteriaForm.class)
                    .fill(new ParentCriteriaForm(account.getParent()));
                final Form<SitterAttributesForm> boundSitterForm = this.formFactory.form(SitterAttributesForm.class)
                    .fill(new SitterAttributesForm(account.getSitter()));
                return Results.ok(EditUserView.render(boundProfileForm, boundRoleForm, boundParentForm,
                    boundSitterForm, account, request, messages));
            }
        );
    }

    /**
     * Get user redirect result.
     *
     * @param request the request
     * @param account the account
     * @return the result
     */
    @SessionRequired(Role.ROLE_ADMIN)
    public Result GET_UserRedirect(final Http.Request request, final AccountModel account) {
        return Results.redirect(routes.UserManageController.GET_User(account));
    }

    /**
     * Post user profile result.
     *
     * @param request the request
     * @param account the account
     * @return the result
     */
    @SessionRequired(Role.ROLE_ADMIN)
    public CompletionStage<Result> POST_UserProfile(final Http.Request request, final AccountModel account) {
        return this.withMessages(request, messages -> {
            final Form<ProfileForm> boundProfileForm = this.requestAddAttrs(request,
                ProfileForm.Attrs.ACCOUNT.bindValue(account), requestWithAttrs ->
                    this.formFactory
                        .form(ProfileForm.class, ProfileForm.Groups.smartGroup(account))
                        .bindFromRequest(requestWithAttrs));

            final Form<RoleForm> boundRoleForm = this.formFactory
                .form(RoleForm.class)
                .fill(new RoleForm(account));
            final Form<ParentCriteriaForm> boundParentForm = this.formFactory.form(ParentCriteriaForm.class)
                .fill(new ParentCriteriaForm(account.getParent()));
            final Form<SitterAttributesForm> boundSitterForm = this.formFactory.form(SitterAttributesForm.class)
                .fill(new SitterAttributesForm(account.getSitter()));

            if (boundProfileForm.hasErrors()) {
                return CompletableFuture.completedFuture(
                    Results.ok(EditUserView.render(boundProfileForm, boundRoleForm, boundParentForm,
                        boundSitterForm, account, request, messages)));
            }

            final ProfileForm profileForm = boundProfileForm.get();

            return this.googleService.addressToCoordinate(account, profileForm)
                .handle((coordinate, throwable) -> {
                    if (throwable != null) {
                        if (throwable.getCause() instanceof GoogleService.NoResultException) {
                            final Form<ProfileForm> formWithError = boundProfileForm.withError("address", "error.invalid");
                            return Results.ok(EditUserView.render(formWithError, boundRoleForm, boundParentForm,
                                boundSitterForm, account, request, messages));
                        }
                        throw new RuntimeException(throwable);
                    }

                    this.safeDB(() -> {
                        profileForm.updateAccount(account);
                        account.save();
                    });

                    return redirect(routes.UserManageController.GET_User(account))
                        .flashing("success", "controllers.admin.user.updatedProfile");
                });
        });
    }

    /**
     * Post user role result.
     *
     * @param request the request
     * @param account the account
     * @return the result
     */
    @SessionRequired(Role.ROLE_ADMIN)
    public Result POST_UserRoles(final Http.Request request, final AccountModel account) {
        return this.withMessages(request, messages -> {
            final Form<RoleForm> boundRoleForm = this.formFactory
                .form(RoleForm.class)
                .bindFromRequest(request);
            final Form<ParentCriteriaForm> boundParentForm = this.formFactory
                .form(ParentCriteriaForm.class)
                .bindFromRequest(request);
            final Form<SitterAttributesForm> boundSitterForm = this.formFactory
                .form(SitterAttributesForm.class)
                .bindFromRequest(request);

            if (boundRoleForm.hasErrors() || boundParentForm.hasErrors() || boundSitterForm.hasErrors()) {
                final Form<ProfileForm> boundProfileForm = this.formFactory
                    .form(ProfileForm.class, ProfileForm.Groups.smartGroup(account))
                    .fill(new ProfileForm(account));
                return Results.ok(EditUserView.render(boundProfileForm, boundRoleForm, boundParentForm,
                    boundSitterForm, account, request, messages));
            }

            final RoleForm roleForm = boundRoleForm.get();
            roleForm.updateAccountRoles(account);

            this.safeDB(() -> {
                if (account.hasRole(Role.ROLE_PARENT)) {
                    final ParentCriteriaForm parentForm = boundParentForm.get();
                    ParentModel parent = account.getParent();
                    if (parent == null) {
                        parent = new ParentModel(account, new ParentCriteriaModel());
                        parent.save();
                    }
                    parentForm.updateCriteria(parent.getParentCriteria());
                }

                if (account.hasRole(Role.ROLE_SITTER)) {
                    final SitterAttributesForm sitterForm = boundSitterForm.get();
                    SitterModel sitter = account.getSitter();
                    if (sitter == null) {
                        sitter = new SitterModel(account, new SitterAttributeModel());
                        sitter.save();
                    }
                    sitterForm.updateAttributes(sitter);
                    sitter.save();
                }
            });

            return redirect(routes.UserManageController.GET_User(account))
                .flashing("success", "controllers.admin.user.updatedRoles");
        });
    }

    /**
     * Get impersonate result.
     *
     * @param request the request
     * @param account the account
     * @return the result
     */
    @SessionRequired(Role.ROLE_ADMIN)
    public Result GET_Impersonate(final Http.Request request, final AccountModel account) {
        final Http.Session session = request.session();

        final Optional<String> userUid = session.get("u");
        final Optional<String> userPic = session.get("p");
        final Optional<String> realUserUid = session.get("ru");
        final Optional<String> realUserPic = session.get("rp");

        if (!userUid.isPresent() || !userPic.isPresent() || realUserUid.isPresent() || realUserPic.isPresent()
            || userUid.get().equals(account.getUid().toString())) {
            return Results.redirect(routes.UserManageController.GET_Users())
                .flashing("danger", "controllers.admin.user.errorImpersonate");
        }

        final Map<String, String> values = this.forgeSessionArgs(account);
        values.put("ru", userUid.get());
        values.put("rp", userPic.get());

        return Results.redirect(controllers.routes.HomeController.GET_Home())
            .addingToSession(request, values);
    }

    /**
     * Get exit impersonate result.
     *
     * @param request the request
     * @return the result
     */
    @SessionRequired
    public Result GET_ExitImpersonate(final Http.Request request) {
        final Http.Session session = request.session();

        final Optional<String> realUserUid = session.get("ru");
        final Optional<String> realUserPic = session.get("rp");

        if (!realUserUid.isPresent() || !realUserPic.isPresent()) {
            throw new RuntimeException("404");
        }

        return Results.redirect(routes.UserManageController.GET_Users())
            .removingFromSession(request, "ru", "rp")
            .addingToSession(request, "u", realUserUid.get())
            .addingToSession(request, "p", realUserPic.get());
    }
}
