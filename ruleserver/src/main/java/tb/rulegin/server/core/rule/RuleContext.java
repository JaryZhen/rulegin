
package tb.rulegin.server.core.rule;


import tb.rulegin.server.common.data.Event;
import tb.rulegin.server.common.data.alarm.Alarm;
import tb.rulegin.server.common.data.id.AlarmId;
import tb.rulegin.server.common.data.id.EntityId;
import tb.rulegin.server.common.data.id.RuleId;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.Optional;

public interface RuleContext {

    RuleId getRuleId();


    Event save(Event event);

    Optional<Event> saveIfNotExists(Event event);

    Optional<Event> findEvent(String eventType, String eventUid);

    Optional<Alarm> findLatestAlarm(EntityId originator, String alarmType);

    Alarm createOrUpdateAlarm(Alarm alarm);

    ListenableFuture<Boolean> clearAlarm(AlarmId id, long clearTs);
}
