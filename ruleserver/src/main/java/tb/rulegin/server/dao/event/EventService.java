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
package tb.rulegin.server.dao.event;


import tb.rulegin.server.common.data.Event;
import tb.rulegin.server.common.data.id.EntityId;
import tb.rulegin.server.common.data.page.TimePageData;
import tb.rulegin.server.common.data.page.TimePageLink;

import java.util.Optional;

public interface EventService {

    Event save(Event event);

    Optional<Event> saveIfNotExists(Event event);

    Optional<Event> findEvent(EntityId entityId, String eventType, String eventUid);

    TimePageData<Event> findEvents( EntityId entityId, TimePageLink pageLink);

    TimePageData<Event> findEvents( EntityId entityId, String eventType, TimePageLink pageLink);
}
