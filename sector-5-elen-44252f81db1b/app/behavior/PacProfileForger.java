package behavior;

import controllers.routes;
import io.ebean.QueryIterator;
import models.account.AccountModel;
import models.account.RoleModel;
import org.pac4j.core.profile.BasicUserProfile;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.core.profile.definition.CommonProfileDefinition;
import org.pac4j.play.PlayWebContext;
import play.mvc.Http;
import play.mvc.Result;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * PacProfileForger.
 *
 * @author Pierre Adam
 * @since 20.06.22
 */
public interface PacProfileForger {

    /**
     * Forge a common profile from an account model.
     *
     * @param account the account
     * @return the common profile
     */
    default CommonProfile forgeProfileFromAccount(final AccountModel account) {
        final CommonProfile profile = new CommonProfile();

        this.updateProfile(profile, account);

        return profile;
    }


    /**
     * Update profile according to the account model.
     *
     * @param <T>     the type parameter
     * @param profile the profile
     * @param account the account
     */
    default <T extends BasicUserProfile> void updateProfile(final T profile,
                                                            final AccountModel account) {
        // Updating the profile of the user to use the roles stored in database.
        final QueryIterator<RoleModel> roleIterator = RoleModel.find.query().where().eq("account", account).findIterate();

        while (roleIterator.hasNext()) {
            final RoleModel role = roleIterator.next();
            profile.addRole(role.getRole().name());
        }

        // Set the account UID as linked ID. This is used to resolve the account model from the profile.
        profile.setLinkedId(account.getUid().toString());

        // Remember who is logged.
        profile.setRemembered(true);

        // Set the common attributes of the profile according to the account model.
        profile.addAttribute(CommonProfileDefinition.EMAIL, account.getEmail());
        profile.addAttribute(CommonProfileDefinition.FIRST_NAME, account.getFirstName());
        profile.addAttribute(CommonProfileDefinition.FAMILY_NAME, account.getLastName());
    }

    /**
     * Update profile result.
     *
     * @param <T>            the type parameter
     * @param request        the request
     * @param context        the context
     * @param profileManager the profile manager
     * @param profile        the profile
     * @param account        the account
     * @param resultSupplier the result supplier
     * @return the result
     */
    default <T extends BasicUserProfile> Result updateProfileAndSession(final Http.Request request,
                                                                        final PlayWebContext context,
                                                                        final ProfileManager<T> profileManager,
                                                                        final T profile,
                                                                        final AccountModel account,
                                                                        final Function<AccountModel, Result> resultSupplier) {
        return this.updateProfileAndSession(request, context, profileManager, profile, account, () -> resultSupplier.apply(account));
    }

    /**
     * Update profile result.
     *
     * @param <T>            the type parameter
     * @param request        the request
     * @param context        the context
     * @param profileManager the profile manager
     * @param profile        the profile
     * @param account        the account
     * @param resultSupplier the result supplier
     * @return the result
     */
    default <T extends BasicUserProfile> Result updateProfileAndSession(final Http.Request request,
                                                                        final PlayWebContext context,
                                                                        final ProfileManager<T> profileManager,
                                                                        final T profile,
                                                                        final AccountModel account,
                                                                        final Supplier<Result> resultSupplier) {
        final Map<String, String> sessionContent = this.forgeSessionArgs(account);

        // Update the profile information.
        this.updateProfile(profile, account);

        // Save the profile into the profile manager.
        profileManager.save(true, profile, false);

        // Use the context to supplement the response by altering the session of the user.
        return context.supplementResponse(resultSupplier.get())
            .addingToSession(request, sessionContent);
    }

    /**
     * Forge session args map.
     *
     * @param account the account
     * @return the map
     */
    default Map<String, String> forgeSessionArgs(final AccountModel account) {
        final Map<String, String> sessionContent = new HashMap<>();
        sessionContent.put("u", account.getUid().toString());

        if (account.getProfilePicture() != null && !account.getProfilePicture().isEmpty()) {
            sessionContent.put("p", account.getProfilePicture());
        } else {
            final MessageDigest md5;
            try {
                md5 = MessageDigest.getInstance("md5");
                final String emailMd5 = DatatypeConverter.printHexBinary(md5.digest(account.getEmail().getBytes())).toLowerCase(Locale.ENGLISH);
                sessionContent.put("p", "https://www.gravatar.com/avatar/" + emailMd5 + "?d=mp&f=y&r=g");
            } catch (final NoSuchAlgorithmException ignore) {
                sessionContent.put("p", routes.Assets.at("assets/img/patients/patient1.jpg").url());
            }
        }

        return sessionContent;
    }
}
