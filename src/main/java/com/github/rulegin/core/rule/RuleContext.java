
package com.github.rulegin.core.rule;


import com.github.rulegin.common.data.Event;
import com.github.rulegin.common.data.alarm.Alarm;
import com.github.rulegin.common.data.id.AlarmId;
import com.github.rulegin.common.data.id.EntityId;
import com.github.rulegin.common.data.id.RuleId;
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
