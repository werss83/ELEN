package controllers.admin;

import controllers.AController;
import io.ebean.annotation.Platform;
import io.ebean.dbmigration.DbMigration;
import org.pac4j.play.store.PlaySessionStore;
import play.Environment;
import play.Mode;
import play.data.FormFactory;
import play.i18n.MessagesApi;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;

import javax.inject.Inject;
import java.io.IOException;
import java.util.function.Supplier;

/**
 * DevController.
 *
 * @author Pierre Adam
 * @since 20.05.31
 */
public class DevController extends AController {

    /**
     * The Application.
     */
    public final Environment environment;

    /**
     * Instantiates a new A controller.
     *
     * @param messagesApi      the messages api
     * @param formFactory      the form factory
     * @param playSessionStore the play session store
     * @param environment      the environment
     */
    @Inject
    public DevController(final MessagesApi messagesApi, final FormFactory formFactory, final PlaySessionStore playSessionStore, final Environment environment) {
        super(messagesApi, formFactory, playSessionStore);
        this.environment = environment;
    }

    /**
     * Only dev mode.
     *
     * @param <T>      the type parameter
     * @param behavior the behavior
     * @return the t
     */
    private <T> T onlyDevMode(final Supplier<T> behavior) {
        if (!Mode.DEV.equals(this.environment.mode())) {
            throw new RuntimeException("404");
        }
        return behavior.get();
    }

    /**
     * Get generate migration result.
     *
     * @param request the request
     * @return the result
     */
    public Result GET_GenerateMigration(final Http.Request request) {
        return this.onlyDevMode(() -> {
            final DbMigration dbMigration = DbMigration.create();

            dbMigration.setPlatform(Platform.POSTGRES);
            dbMigration.setPathToResources("conf");
            dbMigration.setMigrationPath("dbmigration/default");
            dbMigration.setIncludeGeneratedFileComment(true);

            try {
                dbMigration.generateMigration();
                for (final String pendingDrop : dbMigration.getPendingDrops()) {
                    dbMigration.setGeneratePendingDrop(pendingDrop);
                    dbMigration.generateMigration();
                }

            } catch (final IOException e) {
                this.logger.error("Something went wrong", e);
            }

            return Results.ok("Check your console.");
        });
    }
}
