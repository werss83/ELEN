package modules;

import be.objectify.deadbolt.java.cache.HandlerCache;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import org.pac4j.core.authorization.authorizer.RequireAnyRoleAuthorizer;
import org.pac4j.core.client.Clients;
import org.pac4j.core.config.Config;
import org.pac4j.oauth.client.FacebookClient;
import org.pac4j.oauth.profile.facebook.FacebookConfiguration;
import org.pac4j.oidc.client.OidcClient;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.play.CallbackController;
import org.pac4j.play.LogoutController;
import org.pac4j.play.deadbolt2.Pac4jHandlerCache;
import org.pac4j.play.deadbolt2.Pac4jRoleHandler;
import org.pac4j.play.http.PlayHttpActionAdapter;
import org.pac4j.play.store.PlayCacheSessionStore;
import org.pac4j.play.store.PlaySessionStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.Environment;

import javax.inject.Inject;

/**
 * SecurityModule.
 *
 * @author Pierre Adam
 * @since 20.05.28
 */
public class SecurityModule extends AbstractModule {

    /**
     * The constant LOGGER.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityModule.class);

    /**
     * The Configuration.
     */
    private final com.typesafe.config.Config configuration;
    /**
     * The Base url.
     */
    private final String baseUrl;

    /**
     * Instantiates a new Security module.
     *
     * @param environment   the environment
     * @param configuration the configuration
     */
    @Inject
    public SecurityModule(final Environment environment, final com.typesafe.config.Config configuration) {
        this.configuration = configuration;
        this.baseUrl = configuration.getString("baseUrl");
    }

    @Override
    protected void configure() {
        this.bind(HandlerCache.class).to(Pac4jHandlerCache.class);
        this.bind(Pac4jRoleHandler.class).to(MyPac4jRoleHandler.class);

        // Bind the PlaySessionStore to PlayCacheSessionStore which is implemented by the RedisCache
        this.bind(PlaySessionStore.class).to(PlayCacheSessionStore.class);

        // callback
        final CallbackController callbackController = new CallbackController();
        callbackController.setDefaultUrl("/");
        callbackController.setMultiProfile(true);
        callbackController.setRenewSession(true);
        this.bind(CallbackController.class).toInstance(callbackController);

        // logout
        final LogoutController logoutController = new LogoutController();
        logoutController.setDefaultUrl("/");
        logoutController.setDestroySession(true);
        logoutController.setCentralLogout(true);
        this.bind(LogoutController.class).toInstance(logoutController);
    }

    /**
     * Provide facebook client facebook client.
     *
     * @return the facebook client
     */
    @Provides
    protected FacebookClient provideFacebookClient() {
        final String fbId = this.configuration.getString("pac4j.facebook.clientId");
        final String fbSecret = this.configuration.getString("pac4j.facebook.secret");
        final FacebookClient facebookClient = new FacebookClient(fbId, fbSecret);
        final FacebookConfiguration facebookConfiguration = facebookClient.getConfiguration();
        facebookConfiguration.setFields("id,name,first_name,middle_name,last_name,picture,email");
        facebookConfiguration.setScope("email");
//        final String baseUrl = this.configuration.getString("baseUrl");
//        if (baseUrl.contains(":9000")) {
//            facebookClient.setCallbackUrl("https://localhost/callback");
//        }

//        facebookClient.addAuthorizationGenerator(
//            new GenericAuthorizationGenerator<FacebookProfile>("FacebookClient", (accountModel, facebookProfile) -> {
//                accountModel.setFacebookId(facebookProfile.getId());
//            })
//        );
        return facebookClient;
    }

    /**
     * Provide google client oidc client.
     *
     * @return the oidc client
     */
    @Provides
    protected OidcClient<OidcConfiguration> provideGoogleClient() {
        final OidcConfiguration oidcConfiguration = new OidcConfiguration();
        oidcConfiguration.setClientId(this.configuration.getString("pac4j.google.clientId"));
        oidcConfiguration.setSecret(this.configuration.getString("pac4j.google.secret"));
        oidcConfiguration.setDiscoveryURI("https://accounts.google.com/.well-known/openid-configuration");
        oidcConfiguration.addCustomParam("prompt", "consent");
        final OidcClient<OidcConfiguration> oidcClient = new OidcClient<>(oidcConfiguration);
        oidcClient.setName("GoogleClient");
        return oidcClient;
    }

    /**
     * Provide config config.
     *
     * @param facebookClient the facebook client
     * @param googleClient   the google client
     * @return the config
     */
    @Provides
    protected Config provideConfig(final FacebookClient facebookClient,
                                   final OidcClient<OidcConfiguration> googleClient) {
        final Clients clients = new Clients(this.baseUrl + "/callback", facebookClient, googleClient);
        final Config config = new Config(clients);
        config.addAuthorizer("authenticated", new RequireAnyRoleAuthorizer<>("ROLE_PARENT", "ROLE_SITTER", "ROLE_ADMIN"));
        config.addAuthorizer("parent", new RequireAnyRoleAuthorizer<>("ROLE_PARENT", "ROLE_ADMIN"));
        config.addAuthorizer("sitter", new RequireAnyRoleAuthorizer<>("ROLE_SITTER", "ROLE_ADMIN"));
        config.addAuthorizer("admin", new RequireAnyRoleAuthorizer<>("ROLE_ADMIN"));
        //config.addAuthorizer("custom", new CustomAuthorizer());
        //config.addMatcher("excludedPath", new PathMatcher().excludeRegex("^/facebook/notprotected\\.html$"));

        // for deadbolt:
        config.setHttpActionAdapter(PlayHttpActionAdapter.INSTANCE);
        return config;
    }

    /**
     * The type My pac 4 j role handler.
     */
    private static class MyPac4jRoleHandler implements Pac4jRoleHandler {
    }
}
