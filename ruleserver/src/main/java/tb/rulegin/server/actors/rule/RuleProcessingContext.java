/**
 * Copyright © 2016-2017 The Thingsboard Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tb.rulegin.server.actors.rule;

import tb.rulegin.server.actors.ActorSystemContext;
import tb.rulegin.server.common.data.id.*;
import tb.rulegin.server.core.rule.RuleContext;
import tb.rulegin.server.common.data.Event;
import tb.rulegin.server.common.data.alarm.Alarm;
import tb.rulegin.server.dao.event.EventService;
import tb.rulegin.server.dao.timeseries.TimeseriesService;

import java.util.Optional;

public class RuleProcessingContext implements RuleContext {

    private final TimeseriesService tsService;
    private final EventService eventService;
    //private final AlarmService alarmService;
    private final RuleId ruleId;
    private UserId customerId;
    private DeviceId deviceId;

    public RuleProcessingContext(ActorSystemContext systemContext, RuleId ruleId) {
        this.tsService = systemContext.getTsService();
        this.eventService = systemContext.getEventService();
       // this.alarmService = systemContext.getAlarmService();
        this.ruleId = ruleId;
    }


    @Override
    public RuleId getRuleId() {
        return ruleId;
    }


    @Override
    public Event save(Event event) {
        return eventService.save(event);
    }

    @Override
    public Optional<Event> saveIfNotExists(Event event) {
        return eventService.saveIfNotExists(event);
    }

    @Override
    public Optional<Event> findEvent(String eventType, String eventUid) {
        return null;
    }

    @Override
    public Optional<Alarm> findLatestAlarm(EntityId originator, String alarmType) {
        return null;
    }

    @Override
    public Alarm createOrUpdateAlarm(Alarm alarm) {
        return null;
    }

    @Override
    public org.springframework.util.concurrent.ListenableFuture<Boolean> clearAlarm(AlarmId id, long clearTs) {
        return null;
    }


}
