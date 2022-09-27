package services;

import com.fasterxml.jackson.databind.JsonNode;
import com.typesafe.config.Config;
import form.accountservice.RegistrationForm;
import form.profile.ProfileForm;
import models.account.AccountModel;
import org.postgis.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * GoogleService.
 *
 * @author Pierre Adam
 * @since 20.07.04
 */
@Singleton
public class GoogleService {

    /**
     * The constant apiKey.
     */
    public static String apiKey;

    /**
     * The constant apiKeyServerSide.
     */
    public static String apiKeyServerSide;

    /**
     * The Logger.
     */
    protected final Logger logger;

    /**
     * The Ws client.
     */
    private final WSClient wsClient;

    /**
     * Instantiates a new Google service.
     *
     * @param config   the config
     * @param wsClient the ws client
     */
    @Inject
    public GoogleService(final Config config, final WSClient wsClient) {
        GoogleService.apiKey = config.getString("google.apiKey");
        GoogleService.apiKeyServerSide = config.getString("google.apiKeyServerSide");
        this.logger = LoggerFactory.getLogger(GoogleService.class);
        this.wsClient = wsClient;
    }

    /**
     * Address to point completion stage.
     *
     * @param form the profile form
     * @return the completion stage
     */
    public CompletionStage<Point> addressToCoordinate(final RegistrationForm form) {
        return this.addressToCoordinate(null, form.getAddress(), form.getZipCode(), form.getCity());
    }

    /**
     * Address to point completion stage.
     *
     * @param account the account
     * @param form    the profile form
     * @return the completion stage
     */
    public CompletionStage<Point> addressToCoordinate(final AccountModel account,
                                                      final ProfileForm form) {
        return this.addressToCoordinate(account, form.getAddress(), form.getPostalCode(), form.getCity());
    }

    /**
     * Address to coordinate completion stage.
     *
     * @param account the account
     * @param address the address
     * @param zipCode the zip code
     * @param city    the city
     * @return the completion stage
     */
    public CompletionStage<Point> addressToCoordinate(final AccountModel account,
                                                      final String address,
                                                      final String zipCode,
                                                      final String city) {
        // Avoid request if the account already has the correct address.
        if (account != null
            && address.equals(account.getAddress())
            && zipCode.equals(account.getZipCode())
            && city.equals(account.getCity())) {
            return CompletableFuture.completedFuture(account.getCoordinate());
        }

        final String fullAddress = String.format("%s %s %s", address, zipCode, city);

        final WSRequest wsRequest = this.wsClient.url("https://maps.googleapis.com/maps/api/geocode/json")
            .addQueryParameter("key", GoogleService.apiKeyServerSide)
            .addQueryParameter("address", fullAddress);

        return wsRequest.get()
            .thenApply(wsResponse -> {
                final JsonNode response = wsResponse.asJson();
                if (response.has("status") && "OK".equals(response.get("status").asText())
                    && response.has("results") && response.get("results").has(0)) {
                    final JsonNode result = response.get("results").get(0).get("geometry").get("location");
                    final double lat = result.get("lat").asDouble();
                    final double lng = result.get("lng").asDouble();
                    final Point coordinate = new Point(lng, lat);
                    coordinate.srid = 4326;
                    return coordinate;
                }
                throw new NoResultException(String.format("No address were found for the address \"%s\"", address));
            });
    }

    /**
     * The type No result exception.
     */
    public static class NoResultException extends RuntimeException {
        /**
         * Instantiates a new No result exception.
         *
         * @param message the message
         */
        public NoResultException(final String message) {
            super(message);
        }
    }
}
