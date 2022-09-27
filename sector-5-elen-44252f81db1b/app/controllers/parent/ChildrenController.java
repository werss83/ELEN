package controllers.parent;

import actions.SessionRequired;
import controllers.AController;
import entities.dto.parent.ChildrenDTO;
import form.parent.ChildrenForm;
import models.children.ChildrenModel;
import models.parent.ParentModel;
import org.pac4j.play.store.PlaySessionStore;
import play.data.Form;
import play.data.FormFactory;
import play.i18n.MessagesApi;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import security.Role;
import views.html.back.parent.children.MyChildrenView;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * ChildrenController.
 *
 * @author Pierre Adam
 * @since 20.07.18
 */
public class ChildrenController extends AController {

    /**
     * Instantiates a new A controller.
     *
     * @param messagesApi      the messages api
     * @param formFactory      the form factory
     * @param playSessionStore the play session store
     */
    @Inject
    public ChildrenController(final MessagesApi messagesApi, final FormFactory formFactory, final PlaySessionStore playSessionStore) {
        super(messagesApi, formFactory, playSessionStore);
    }

    /**
     * Get my child result.
     *
     * @param request the request
     * @return the result
     */
    @SessionRequired(Role.ROLE_PARENT)
    public CompletionStage<Result> GET_MyChildren(final Http.Request request) {
        return this.withMessages(request, messages ->
            this.withAccount(request, account ->
                CompletableFuture.supplyAsync(() -> new ChildrenDTO(this.formFactory, account.getParent()))
                    .thenApply(childrenDTO -> Results.ok(MyChildrenView.render(childrenDTO, request, messages))
                    )
            )
        );
    }

    /**
     * Post create or update child completion stage.
     *
     * @param request the request
     * @return the completion stage
     */
    @SessionRequired(Role.ROLE_PARENT)
    public CompletionStage<Result> POST_CreateOrUpdateChild(final Http.Request request) {
        return this.withMessages(request, messages ->
            this.withAccount(request, account -> {
                final Form<ChildrenForm> boundForm = this.formFactory.form(ChildrenForm.class).bindFromRequest(request);
                final ParentModel parent = account.getParent();

                if (boundForm.hasErrors()) {
                    return CompletableFuture
                        .supplyAsync(() -> new ChildrenDTO(this.formFactory, parent, boundForm))
                        .thenApply(childrenDTO -> Results.ok(MyChildrenView.render(childrenDTO, request, messages)));
                }

                return CompletableFuture
                    .supplyAsync(boundForm::get)
                    .thenApply(form -> {
                        final ChildrenModel child = form.updateOrCreate(parent);
                        child.save();
                        return Results.redirect(routes.ChildrenController.GET_MyChildren());
                    });
            })
        );
    }

    /**
     * Post create or update child completion stage.
     *
     * @param request the request
     * @return the completion stage
     */
    @SessionRequired(Role.ROLE_PARENT)
    public Result POST_DeleteChild(final Http.Request request, final ChildrenModel child) {
        return this.withAccount(request, account -> {
            if (child.getParent().getId().equals(account.getParent().getId())) {
                child.delete();
            }

            return Results.redirect(routes.ChildrenController.GET_MyChildren());
        });
    }
}
