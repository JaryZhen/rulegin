

package tb.rulegin.server.dao.entity;

import tb.rulegin.server.common.data.id.EntityId;
import tb.rulegin.server.dao.relation.RelationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;


@Slf4j
public abstract class AbstractEntityService {

    @Autowired
    protected RelationService relationService;

    protected void deleteEntityRelations(EntityId entityId) {
        log.trace("Executing deleteEntityRelations [{}]", entityId);
        relationService.deleteEntityRelations(entityId);
    }


}
