package actors;

import com.fasterxml.jackson.databind.JsonNode;
import com.zero_x_baadf00d.play.module.redis.PlayRedis;
import org.joda.time.LocalDate;
import play.libs.Json;
import play.libs.ws.WSClient;
import utils.date.DateChecker;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * BankHolidayActor.
 *
 * @author Pierre Adam
 * @since 20.08.24
 */
public class BankHolidayActor extends AActor {

    private final WSClient wsClient;

    @Inject
    public BankHolidayActor(final PlayRedis playRedis, final WSClient wsClient) {
        super(playRedis);
        this.wsClient = wsClient;
    }

    @Override
    public Receive createReceive() {
        return this.receiveBuilder()
            .matchAny(this::logic)
            .build();
    }

    public void logic(final Object ignore) {
        this.wsClient.url("https://etalab.github.io/jours-feries-france-data/json/metropole.json")
            .get()
            .whenComplete((wsResponse, throwable) -> {
                if (throwable == null && wsResponse.getStatus() / 100 == 2) {
                    final JsonNode jsonNode = Json.parse(wsResponse.getBody());
                    final List<LocalDate> bankHolidays = new ArrayList<>();
                    final Iterator<Map.Entry<String, JsonNode>> iterator = jsonNode.fields();

                    while (iterator.hasNext()) {
                        final LocalDate date = LocalDate.parse(iterator.next().getKey());
                        bankHolidays.add(date);
                    }

                    if (!bankHolidays.isEmpty()) {
                        DateChecker.setExtraHolidays(bankHolidays);
                    }

                    this.logger.trace("Bank Holiday updated !");
                } else {
                    this.logger.error("An error occurred while trying to retrieve the bank holiday.", throwable);
                }
            });
    }
}
