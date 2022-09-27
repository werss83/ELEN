package services;

import actors.cares.payloads.CheckAll;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import org.joda.time.DateTime;
import play.Application;
import scala.concurrent.duration.Duration;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.concurrent.TimeUnit;

/**
 * OnStartupService.
 *
 * @author Pierre Adam
 * @since 19.05.20
 */
public final class OnStartupService extends Service {

    /**
     * The Actor system.
     */
    private final ActorSystem actorSystem;

    /**
     * Instantiates a new On startup service.
     *
     * @param application      the application
     * @param actorSystem      the actor system
     * @param careCreatorActor the care creator actor
     */
    @Inject
    private OnStartupService(final Application application,
                             final ActorSystem actorSystem,
                             @Named("CareCreatorActor") final ActorRef careCreatorActor,
                             @Named("BankHolidayActor") final ActorRef bankHolidayActor) {
        super();
        this.actorSystem = actorSystem;
        this.easyScheduleOnce(bankHolidayActor, 5, new CheckAll());
        this.easyDailySchedule(bankHolidayActor, 3600, new CheckAll());
        this.easyScheduleOnce(careCreatorActor, 20, new CheckAll());
        this.easyDailySchedule(careCreatorActor, 3600, new CheckAll());
    }

    /**
     * Easy schedule once.
     *
     * @param actorRef     the actor ref
     * @param secondsDelay the seconds delay
     * @param payload      the payload
     */
    private void easyScheduleOnce(final ActorRef actorRef, final long secondsDelay, final Object payload) {
        this.actorSystem.scheduler().scheduleOnce(
            Duration.create(secondsDelay, TimeUnit.SECONDS),
            actorRef,
            payload,
            this.actorSystem.dispatcher(),
            ActorRef.noSender()
        );
    }

    /**
     * Easy daily schedule.
     *
     * @param actorRef    the actor ref
     * @param secondOfDay the second of day
     * @param payload     the payload
     */
    private void easyDailySchedule(final ActorRef actorRef, final long secondOfDay, final Object payload) {
        long scheduleIn = secondOfDay - DateTime.now().getSecondOfDay();

        while (scheduleIn < 0) {
            scheduleIn += 24 * 3600;
        }

        this.actorSystem.scheduler().scheduleAtFixedRate(
            Duration.create(scheduleIn, TimeUnit.SECONDS),
            Duration.create(1, TimeUnit.DAYS),
            actorRef,
            payload,
            this.actorSystem.dispatcher(),
            ActorRef.noSender());
    }

    /**
     * Easy daily schedule.
     *
     * @param actorRef     the actor ref
     * @param initialDelay the initial delay
     * @param interval     the interval
     * @param payload      the payload
     */
    private void intervalSchedule(final ActorRef actorRef, final long initialDelay, final long interval, final Object payload) {
        this.actorSystem.scheduler().scheduleAtFixedRate(
            Duration.create(initialDelay, TimeUnit.SECONDS),
            Duration.create(interval, TimeUnit.SECONDS),
            actorRef,
            payload,
            this.actorSystem.dispatcher(),
            ActorRef.noSender());
    }
}
