import akka.http.scaladsl.model.EntityStreamException;
import akka.stream.impl.io.ByteStringParser;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.typesafe.config.Config;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.Environment;
import play.api.OptionalSourceMapper;
import play.api.http.HttpErrorHandlerExceptions;
import play.api.routing.Router;
import play.http.HttpErrorHandler;
import play.i18n.Messages;
import play.i18n.MessagesApi;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import play.twirl.api.Html;
import scala.Option;
import scala.Some;
import views.html.systemviews.Error400View;
import views.html.systemviews.Error403View;
import views.html.systemviews.Error404View;
import views.html.systemviews.Error500View;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

/**
 * ErrorHandler.
 *
 * @author Pierre Adam
 * @since 20.06.04
 */
public class ErrorHandler implements HttpErrorHandler {

    /**
     * The Logger.
     */
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Possible current play editor link.
     */
    private final Option<String> playEditor;

    /**
     * Current running environment.
     */
    private final Environment environment;

    /**
     * Source mapper instance.
     */
    private final OptionalSourceMapper sourceMapper;

    /**
     * The Messages api.
     */
    private final MessagesApi messagesApi;

    /**
     * The Status as text.
     */
    private final Map<Integer, String> statusAsText;

    /**
     * The Status description.
     */
    private final Map<Integer, String> statusDescription;

    /**
     * The Status as web page description in production.
     */
    private final Map<Integer, Provider<Html>> statusAsPageProd;

    /**
     * The Status as web page description in development.
     */
    private final Map<Integer, Function<Http.RequestHeader, Html>> statusAsPageDev;

    /**
     * Build instance.
     *
     * @param configuration The current configuration
     * @param environment   The current environment
     * @param sourceMapper  The current source mapper
     * @param routes        The know routes
     * @param messagesApi   the messages api
     */
    @Inject
    public ErrorHandler(final Config configuration, final Environment environment, final OptionalSourceMapper sourceMapper,
                        final Provider<Router> routes, final MessagesApi messagesApi) {
        this.environment = environment;
        this.sourceMapper = sourceMapper;
        this.messagesApi = messagesApi;
        this.statusAsText = new HashMap<Integer, String>() {{
            this.put(Http.Status.BAD_REQUEST, "Bad Request");
            this.put(Http.Status.UNAUTHORIZED, "Unauthorized");
            this.put(Http.Status.FORBIDDEN, "Forbidden");
            this.put(Http.Status.NOT_FOUND, "Not Found");
        }};
        this.statusDescription = new HashMap<Integer, String>() {{
            this.put(Http.Status.BAD_REQUEST, "api.error.badRequest");
            this.put(Http.Status.UNAUTHORIZED, "api.error.unauthorized");
            this.put(Http.Status.FORBIDDEN, "api.error.forbidden");
            this.put(Http.Status.NOT_FOUND, "api.error.notFound");
        }};
        this.statusAsPageProd = new HashMap<Integer, Provider<Html>>() {{
            this.put(Http.Status.BAD_REQUEST, Error400View::render);
            this.put(Http.Status.UNAUTHORIZED, Error403View::render);
            this.put(Http.Status.FORBIDDEN, Error403View::render);
            this.put(Http.Status.NOT_FOUND, Error404View::render);
        }};
        this.statusAsPageDev = new HashMap<Integer, Function<Http.RequestHeader, Html>>() {{
            this.put(Http.Status.NOT_FOUND, (request) -> views.html.defaultpages.devNotFound.render(request.method(), request.uri(), Some.apply(routes.get()), request.asScala()));
        }};
        if (configuration.hasPath("play.editor")) {
            this.playEditor = Option.apply(configuration.getString("play.editor"));
        } else {
            this.playEditor = Option.empty();
        }
    }

    @Override
    public CompletionStage<Result> onClientError(final Http.RequestHeader request, final int statusCode, final String message) {
        int resolvedStatusCode = statusCode;

        if (NumberUtils.isDigits(message)) {
            resolvedStatusCode = 404;
        }

        final Messages preferredMessage = this.messagesApi.preferred(request);

        if (request.path().startsWith("/api/")) {
            if (this.statusAsText.containsKey(resolvedStatusCode)) {
                final ObjectNode result = Json.newObject();
                result.put("type", this.statusAsText.get(resolvedStatusCode));
                result.put("key", this.statusDescription.get(resolvedStatusCode));
                result.put("message", preferredMessage.at(this.statusDescription.get(resolvedStatusCode)));
                if (this.environment.isDev()) {
                    result.set("extra", Json.toJson(Collections.singletonMap("exception", Collections.singletonList(message))));
                } else {
                    result.putNull("extra");
                }
            } else {
                throw new IllegalArgumentException(
                    "onClientError invoked with non client error status code " + statusCode + ": " + message
                );
            }
        }
        if (this.environment.isDev()) {
            // In development mode.
            if (this.statusAsPageDev.containsKey(resolvedStatusCode)) {
                return CompletableFuture.completedFuture(Results.status(resolvedStatusCode, this.statusAsPageDev.get(resolvedStatusCode).apply(request)));
            } else if (this.statusAsPageProd.containsKey(resolvedStatusCode)) {
                return CompletableFuture.completedFuture(Results.status(resolvedStatusCode, this.statusAsPageProd.get(resolvedStatusCode).get()));
            }
        } else {
            // In production mode.
            if (this.statusAsPageProd.containsKey(resolvedStatusCode)) {
                return CompletableFuture.completedFuture(Results.status(resolvedStatusCode, this.statusAsPageProd.get(resolvedStatusCode).get()));
            }
        }

        return CompletableFuture.completedFuture(
            Results.status(statusCode, views.html.defaultpages.badRequest.render(request.method(), request.uri(), message, request.asScala())));
    }

    /**
     * Is due to abnormal request boolean.
     *
     * @param t the throwable
     * @return the boolean
     */
    private boolean isDueToAbnormalRequest(final Throwable t) {
        // Client disconnected before all data have been sent
        return (t instanceof EntityStreamException && t.getMessage().contains("stream truncation"))
            // Can't parse request headers
            || (t instanceof ByteStringParser.ParsingException && t.getMessage().contains("failed in step ReadHeaders"));
    }

    @Override
    public CompletionStage<Result> onServerError(final Http.RequestHeader request, final Throwable throwable) {
        Throwable t = throwable;
        while (t != null) {

            // Test if exception is a digit exception (ex: 404)
            if (NumberUtils.isDigits(t.getMessage())) {
                try {
                    return this.onClientError(request, Integer.valueOf(t.getMessage()), "");
                } catch (final NumberFormatException | NullPointerException ignore) {
                }
            }

            // Special case: Check if exception is thrown due to abnormal request (ie: a client
            // close the connection before all data has been sent by the server)
            if (this.isDueToAbnormalRequest(t)) {
                return CompletableFuture.completedFuture(Results.noContent());
            }

            // Next throwable
            t = t.getCause();
        }

        this.logger.error(
            String.format(
                "Fatal error on %s %s called by %s",
                request.method(),
                request.uri(),
                request.header("X-Forwarded-For").orElse(request.remoteAddress())
            ),
            throwable
        );

        final Messages preferredMessage = this.messagesApi.preferred(request);

        if (request.path().startsWith("/api/")) {
            final ObjectNode result = Json.newObject();
            result.put("type", "InternalServerError");
            result.put("message", preferredMessage.at("api.error.internalError"));
            if (throwable == null || this.environment.isProd()) {
                result.putNull("extra");
            } else {
                result.set("extra", Json.toJson(Collections.singletonMap("exception", Collections.singletonList(throwable))));
            }
            return CompletableFuture.completedFuture(Results.internalServerError(result));
        }

        if (this.environment.isDev()) {
            return CompletableFuture.completedFuture(
                Results.internalServerError(
                    views.html.defaultpages.devError.render(
                        this.playEditor,
                        HttpErrorHandlerExceptions.throwableToUsefulException(
                            this.sourceMapper.sourceMapper(),
                            this.environment.isProd(),
                            throwable
                        ),
                        request.asScala()
                    )
                )
            );
        }
        return CompletableFuture.completedFuture(Results.internalServerError(Error500View.render()));
    }
}
