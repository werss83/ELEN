import actors.BankHolidayActor;
import actors.cares.CareCreatorActor;
import com.google.inject.AbstractModule;
import com.typesafe.config.Config;
import interfaces.EmailValidation;
import interfaces.ResetPassword;
import play.Environment;
import play.libs.akka.AkkaGuiceSupport;
import redis.RedisEmailValidation;
import redis.RedisResetPassword;
import services.*;

/**
 * This class is a Guice module that tells Guice how to bind several
 * different types. This Guice module is created when the Play
 * application starts.
 * <p>
 * Play will automatically use any class called `Module` that is in
 * the root package. You can create modules in other locations by
 * adding `play.modules.enabled` settings to the `application.conf`
 * configuration file.
 */
public class Module extends AbstractModule implements AkkaGuiceSupport {

    /**
     * Instantiates a new Module.
     *
     * @param environment the environment
     * @param config      the config
     */
    public Module(final Environment environment, final Config config) {
    }

    @Override
    public void configure() {
        this.bind(OnStartupService.class).asEagerSingleton();
        this.bind(EmailService.class).asEagerSingleton();
        this.bind(EmailValidation.class).to(RedisEmailValidation.class);
        this.bind(ResetPassword.class).to(RedisResetPassword.class);
        this.bind(AccountService.class).asEagerSingleton();
        this.bind(GoogleService.class).asEagerSingleton();
        this.bind(CafService.class).asEagerSingleton();
        this.bind(TimezoneService.class).asEagerSingleton();
        this.bind(PricingService.class).asEagerSingleton();

        this.bindActor(CareCreatorActor.class, "CareCreatorActor");
        this.bindActor(BankHolidayActor.class, "BankHolidayActor");
    }
}
