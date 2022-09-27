package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.pac4j.play.store.PlaySessionStore;
import play.data.FormFactory;
import play.i18n.MessagesApi;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import system.BuildInfo;

import javax.inject.Inject;

/**
 * HealthcheckController.
 *
 * @author Pierre Adam
 * @since 20.06.01
 */
public class HealthcheckController extends AController {

    /**
     * Instantiates a new A controller.
     *
     * @param messagesApi the messages api
     * @param formFactory the form factory
     */
    @Inject
    public HealthcheckController(final MessagesApi messagesApi,
                                 final FormFactory formFactory,
                                 final PlaySessionStore playSessionStore) {
        super(messagesApi, formFactory, playSessionStore);
    }

    /**
     * Get health check result.
     *
     * @param request the request
     * @return the result
     */
    public Result GET_HealthCheck(final Http.Request request) throws JsonProcessingException {
        final ObjectNode json = Json.newObject();

        json.put("projectName", BuildInfo.getProjectName());
        json.put("projectVersion", BuildInfo.getProjectVersion());
        json.put("buildDate", BuildInfo.getBuildDate());
        json.put("javaVersion", BuildInfo.getJavaVersion());
        json.put("scalaVersion", BuildInfo.getScalaVersion());
        json.put("sbtVersion", BuildInfo.getSbtVersion());

        return ok().sendJson(json);
    }
}
