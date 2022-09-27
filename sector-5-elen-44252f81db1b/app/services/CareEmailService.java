package services;

import com.sendgrid.helpers.mail.objects.Email;
import com.typesafe.config.Config;
import models.care.BookedCareModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.i18n.Lang;
import play.i18n.Messages;
import play.i18n.MessagesApi;
import play.mvc.Http;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

/**
 * CareEmailService.
 *
 * @author Pierre Adam
 * @since 20.09.13
 */
public class CareEmailService {

    /**
     * The Logger.
     */
    private final Logger logger;

    /**
     * The Email service.
     */
    private final EmailService emailService;

    /**
     * The Config.
     */
    private final Config config;

    /**
     * The Messages.
     */
    private final Messages messages;

    /**
     * Instantiates a new Care email service.
     *
     * @param emailService the email service
     * @param config       the config
     * @param messagesApi  the messages api
     */
    @Inject
    public CareEmailService(final EmailService emailService, final Config config, final MessagesApi messagesApi) {
        this.logger = LoggerFactory.getLogger(CafService.class);
        this.emailService = emailService;
        this.config = config;
        final ArrayList<Lang> langs = new ArrayList<>();
        langs.add(new Lang(Locale.FRANCE));
        this.messages = messagesApi.preferred(langs);
    }

    /**
     * New care created.
     *
     * @param bookedCareModel the booked care model
     * @param request         the request
     */
    public void newCareCreated(final BookedCareModel bookedCareModel, final Http.Request request) {
        try {
            this.emailService.sendMail(
                new Email(this.config.getString("email.careCreated.from.email"), this.config.getString("email.careCreated.from.name")),
                null,
                this.config.getString("email.careCreated.to"),
                this.messages.at("services.careEmailService.subject"),
                null,
                views.html.emailviews.NewCareEmail.render(
                    bookedCareModel,
                    request,
                    this.messages
                )
            );
        } catch (final IOException e) {
            this.logger.error("Unable to send an email.", e);
        }
    }
}
