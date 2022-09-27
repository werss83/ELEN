package services;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Attachments;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import com.typesafe.config.Config;
import play.twirl.api.Html;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;

/**
 * EmailService.
 *
 * @author Pierre Adam
 * @since 20.06.04
 */
@Singleton
public class EmailService extends Service {

    /**
     * The Config.
     */
    private final Config config;

    /**
     * The Send grid.
     */
    private final SendGrid sendGrid;

    /**
     * Instantiates a new Email service.
     *
     * @param config the config
     */
    @Inject
    public EmailService(final Config config) {
        super();
        this.config = config;
        this.sendGrid = new SendGrid(this.config.getString("sendgrid.api-key"));
    }

    /**
     * Send mail.
     *
     * @param from        the from
     * @param to          the to
     * @param subject     the subject
     * @param textBody    the text body
     * @param htmlBody    the html body
     * @param attachments the attachments
     * @throws IOException the io exception
     */
    public void sendMail(final Email from,
                         final Email replyTo,
                         final String to,
                         final String subject,
                         final String textBody,
                         final Html htmlBody,
                         final Attachments... attachments
    ) throws IOException {
        // Create the mail.
        final Mail mail;
        if (from == null) {
            mail = this.getStandardMail();
        } else {
            mail = this.getStandardMail(from);
        }
        mail.setSubject(subject);

        // Set the destination.
        final Email toEmail = new Email(to);
        final Personalization personalization = new Personalization();
        personalization.addTo(toEmail);
        mail.addPersonalization(personalization);

        if (replyTo != null) {
            mail.setReplyTo(replyTo);
        }

        // Add a plain text body if there is one
        if (textBody != null) {
            mail.addContent(new Content("text/plain", textBody));
        }
        // Add an HTML body if there is one
        if (htmlBody != null) {
            mail.addContent(new Content("text/html", htmlBody.body()));
        }

        // Add the attachments
        for (final Attachments attachment : attachments) {
            mail.addAttachments(attachment);
        }

        // Send the mail
        this.sendMail(mail);
    }

    /**
     * Send mail response.
     *
     * @param mail the mail
     * @return the response
     * @throws IOException the io exception
     */
    public Response sendMail(final Mail mail) throws IOException {
        final Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());
        return this.sendGrid.api(request);
    }

    /**
     * Gets standard mail.
     *
     * @param from the from
     * @return the standard mail
     */
    public Mail getStandardMail(final String from, final String name) {
        return this.getStandardMail(new Email(from, name));
    }

    /**
     * Gets standard mail.
     *
     * @param from the from
     * @return the standard mail
     */
    public Mail getStandardMail(final Email from) {
        final Mail mail = new Mail();
        mail.setFrom(from);
        return mail;
    }

    /**
     * Gets standard mail.
     *
     * @return the standard mail
     */
    public Mail getStandardMail() {
        return this.getStandardMail(this.config.getString("sendgrid.from.email"), this.config.getString("sendgrid.from.name"));
    }
}
