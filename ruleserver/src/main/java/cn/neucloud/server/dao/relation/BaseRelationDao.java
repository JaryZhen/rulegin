package cn.neucloud.server.dao.relation;

import cn.neucloud.server.common.data.EntityType;
import cn.neucloud.server.common.data.id.EntityId;
import cn.neucloud.server.common.data.page.TimePageLink;
import com.google.common.util.concurrent.ListenableFuture;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Jary on 2017/9/27 0027.
 */
@Service
public class BaseRelationDao implements RelationDao{
    @Override
    public ListenableFuture<List<EntityRelation>> findAllByFrom(EntityId from, RelationTypeGroup typeGroup) {
        return null;
    }

    @Override
    public ListenableFuture<List<EntityRelation>> findAllByFromAndType(EntityId from, String relationType, RelationTypeGroup typeGroup) {
        return null;
    }

    @Override
    public ListenableFuture<List<EntityRelation>> findAllByTo(EntityId to, RelationTypeGroup typeGroup) {
        return null;
    }

    @Override
    public ListenableFuture<List<EntityRelation>> findAllByToAndType(EntityId to, String relationType, RelationTypeGroup typeGroup) {
        return null;
    }

    @Override
    public ListenableFuture<Boolean> checkRelation(EntityId from, EntityId to, String relationType, RelationTypeGroup typeGroup) {
        return null;
    }

    @Override
    public ListenableFuture<EntityRelation> getRelation(EntityId from, EntityId to, String relationType, RelationTypeGroup typeGroup) {
        return null;
    }

    @Override
    public ListenableFuture<Boolean> saveRelation(EntityRelation relation) {
        return null;
    }

    @Override
    public ListenableFuture<Boolean> deleteRelation(EntityRelation relation) {
        return null;
    }

    @Override
    public ListenableFuture<Boolean> deleteRelation(EntityId from, EntityId to, String relationType, RelationTypeGroup typeGroup) {
        return null;
    }

    @Override
    public ListenableFuture<Boolean> deleteOutboundRelations(EntityId entity) {
        return null;
    }

    @Override
    public ListenableFuture<List<EntityRelation>> findRelations(EntityId from, String relationType, RelationTypeGroup typeGroup, EntityType toType, TimePageLink pageLink) {
        return null;
    }
}
