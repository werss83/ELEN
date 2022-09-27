package actors.cares;

import actors.AActor;
import actors.cares.payloads.Check;
import actors.cares.payloads.CheckAll;
import com.typesafe.config.Config;
import com.zero_x_baadf00d.play.module.redis.PlayRedis;
import io.ebean.Expr;
import io.ebean.Model;
import io.ebean.QueryIterator;
import models.care.BookedCareModel;
import models.care.EffectiveCareModel;
import models.care.enumeration.BookedCareType;
import models.care.enumeration.EffectiveCareStatus;
import models.unavailability.EffectiveUnavailabilityModel;
import org.joda.time.DateTime;
import redis.RedisKeys;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;

/**
 * CareCreatorActor.
 *
 * @author Pierre Adam
 * @since 20.07.25
 */
public class CareCreatorActor extends AActor {

    /**
     * The Plan month in future.
     */
    private final int planMonthInFuture;

    /**
     * Instantiates a new Care creator actor.
     *
     * @param playRedis the play redis
     * @param config    the config
     */
    @Inject
    public CareCreatorActor(final PlayRedis playRedis, final Config config) {
        super(playRedis);
        this.planMonthInFuture = config.getInt("settings.bookedCare.monthsInAdvance");
    }

    @Override
    public Receive createReceive() {
        return this.receiveBuilder()
            .match(Check.class, this::checkEntryPoint)
            .match(CheckAll.class, this::checkAllEntryPoint)
            .build();
    }

    /**
     * Check all entry point.
     *
     * @param ignored the ignored
     */
    public void checkAllEntryPoint(final CheckAll ignored) {
        try {
            this.redisLockOrSkip(RedisKeys.careCreatorActorLock, 3600, () -> {
                final QueryIterator<BookedCareModel> iterate = BookedCareModel.find.query()
                    .where()
                    .or(
                        Expr.isNull("endDate"),
                        Expr.gt("endDate", DateTime.now())
                    )
                    .findIterate();

                while (iterate.hasNext()) {
                    final BookedCareModel bookedCare = iterate.next();
                    this.checkBookedCare(bookedCare);
                }
                return true;
            });
        } catch (final Exception e) {
            this.logger.error("An error occurred while processing all the booked care models", e);
        }
    }

    /**
     * Check entry point.
     *
     * @param payload the payload
     */
    public void checkEntryPoint(final Check payload) {
        try {
            payload.getToCheck().forEach(this::checkBookedCare);
        } catch (final Exception e) {
            this.logger.error("An error occurred while processing a list of booked care models", e);
        }
    }

    /**
     * Check booked care.
     *
     * @param bookedCare the booked care
     */
    private void checkBookedCare(@NotNull final BookedCareModel bookedCare) {
        assert bookedCare != null;

        if (BookedCareType.ONE_TIME.equals(bookedCare.getType()) && bookedCare.getEffectiveCares().size() == 1) {
            return;
        }

        this.redisLockOrSkip(String.format(RedisKeys.careCreatorActorBookedCareLock, bookedCare.getId()), 600, () ->
            this.safeDB(() -> {
                final EffectiveCareModel lastEffectiveCare = EffectiveCareModel.find.query()
                    .where()
                    .eq("bookedCare", bookedCare)
                    .or(
                        Expr.isNull("endDate"),
                        Expr.gt("endDate", DateTime.now())
                    )
                    .order("startDate DESC")
                    .setMaxRows(1)
                    .findOne();

                if (lastEffectiveCare != null) {
                    this.removeOutdatedEffectiveCare(bookedCare, lastEffectiveCare);
                    this.createCares(bookedCare, lastEffectiveCare);
                } else {
                    final EffectiveCareModel newEffectiveCare = new EffectiveCareModel(bookedCare.getStartDate(),
                        bookedCare.getStartDate().plus(bookedCare.getDuration()), bookedCare);
                    newEffectiveCare.save();
                    this.createCares(bookedCare, newEffectiveCare);
                }

                return true;
            })
        );
    }

    /**
     * Remove outdated effective care.
     *
     * @param bookedCare        the booked care
     * @param lastEffectiveCare the last effective care
     */
    private void removeOutdatedEffectiveCare(final BookedCareModel bookedCare, final EffectiveCareModel lastEffectiveCare) {
        if (lastEffectiveCare.getStartDate().isAfter(bookedCare.getEndDate())) {
            DateTime endDate = bookedCare.getEndDate();
            if (DateTime.now().isAfter(endDate)) {
                endDate = DateTime.now();
            }
            EffectiveCareModel.find.query()
                .where()
                .eq("bookedCare", bookedCare)
                .gt("startDate", endDate)
                .findEach(Model::delete);
        }
    }

    /**
     * Create cares.
     *
     * @param bookedCare        the booked care
     * @param lastEffectiveCare the last effective care
     */
    private void createCares(final BookedCareModel bookedCare, final EffectiveCareModel lastEffectiveCare) {
        final DateTime bookedEndDate = bookedCare.getEndDate();
        final EffectiveUnavailabilityModel leu = lastEffectiveCare.getEffectiveUnavailability();
        EffectiveCareModel newCare = lastEffectiveCare;

        while (newCare.getStartDate().isBefore(DateTime.now().plusMonths(this.planMonthInFuture))) {
            final DateTime newStartDate = newCare.getStartDate().plusDays(7);

            if (bookedEndDate != null && newStartDate.isAfter(bookedEndDate)) {
                return;
            }

            newCare = new EffectiveCareModel(newStartDate, newCare.getEndDate().plusDays(7), bookedCare);

            if (leu != null) {
                newCare.setStatus(EffectiveCareStatus.ASSIGNED);
                final EffectiveUnavailabilityModel eu = new EffectiveUnavailabilityModel(newCare.getStartDate(),
                    newCare.getEndDate(), leu.getPlannedUnavailability());
                newCare.setEffectiveUnavailability(eu);
                eu.setEffectiveCare(newCare);
            }
            newCare.save();
        }
    }
}
