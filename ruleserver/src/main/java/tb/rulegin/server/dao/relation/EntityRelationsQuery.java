
package tb.rulegin.server.dao.relation;

import lombok.Data;

import java.util.List;

/**
 * Created by ashvayka on 02.05.17.
 */
@Data
public class EntityRelationsQuery {

    private RelationsSearchParameters parameters;
    private List<EntityTypeFilter> filters;

}
