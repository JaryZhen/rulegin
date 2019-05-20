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
package com.github.rulegin.dao.event;

import com.github.rulegin.common.data.Event;
import com.github.rulegin.common.data.id.EntityId;
import com.github.rulegin.common.data.page.TimePageLink;
import com.github.rulegin.dao.Dao;

import java.util.List;
import java.util.Optional;

/**
 * The Interface DeviceDao.
 */
public interface EventDao extends Dao<Event> {

    /**
     * Save or update event object
     *
     * @param event the event object
     * @return saved event object
     */
    Event save(Event event);

    /**
     * Save event object if it is not yet saved
     *
     * @param event the event object
     * @return saved event object
     */
    Optional<Event> saveIfNotExists(Event event);

    /**
     * Find event by userId, entityId and eventUid.
     *
     * @param entityId the entityId
     * @param eventType the eventType
     * @param eventUid the eventUid
     * @return the event
     */
    Event findEvent(EntityId entityId, String eventType, String eventUid);

    /**
     * Find events by userId, entityId and pageLink.
     *
     * @param entityId the entityId
     * @param pageLink the pageLink
     * @return the event list
     */
    List<Event> findEvents(EntityId entityId, TimePageLink pageLink);

    /**
     * Find events by userId, entityId, eventType and pageLink.
     *
     * @param entityId the entityId
     * @param eventType the eventType
     * @param pageLink the pageLink
     * @return the event list
     */
    List<Event> findEvents( EntityId entityId, String eventType, TimePageLink pageLink);
}
