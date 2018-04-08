
package cn.neucloud.server.actors.stats;

import cn.neucloud.server.common.data.id.EntityId;
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
