
package com.github.rulegin.actors.stats;

import com.github.rulegin.common.data.id.EntityId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public final class StatsPersistMsg {
    private long messagesProcessed;
    private long errorsOccurred;
    private EntityId entityId;
}
