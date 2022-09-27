package controllers.sitter;

import actions.SessionOptional;
import controllers.AController;
import models.sitter.SitterModel;
import org.pac4j.play.store.PlaySessionStore;
import play.data.FormFactory;
import play.i18n.MessagesApi;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;

import javax.inject.Inject;
import java.util.Optional;

/**
 * PictureController.
 *
 * @author Pierre Adam
 * @since 20.07.19
 */
public class PictureController extends AController {

    /**
     * Instantiates a new A controller.
     *
     * @param messagesApi      the messages api
     * @param formFactory      the form factory
     * @param playSessionStore the play session store
     */
    @Inject
    public PictureController(final MessagesApi messagesApi, final FormFactory formFactory, final PlaySessionStore playSessionStore) {
        super(messagesApi, formFactory, playSessionStore);
    }

    /**
     * Get serve picture result.
     *
     * @param request the request
     * @param sitter  the sitter
     * @return the result
     */
    @SessionOptional
    public Result GET_ServePicture(final Http.Request request, final SitterModel sitter) {
        if (sitter.getPictureEtag() == null) {
            return Results.notFound();
        }

        final String eTag = sitter.getPictureEtag();

        final Optional<String> ifNoneMatch = request.getHeaders().get("If-None-Match");
        if (ifNoneMatch.isPresent() && ifNoneMatch.get().equals(eTag)) {
            return Results.status(Http.Status.NOT_MODIFIED).as(sitter.getPictureMimeType());
        }

        return Results.ok(sitter.getPicture()).withHeader(Http.HeaderNames.ETAG, eTag).as(sitter.getPictureMimeType());
    }
}
