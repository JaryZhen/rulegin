package com.github.rulegin.dao.timeseries;

import com.github.rulegin.common.data.id.EntityId;
import com.github.rulegin.common.data.kv.TsKvEntry;
import com.github.rulegin.common.data.kv.TsKvQuery;
import com.google.common.util.concurrent.ListenableFuture;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by Jary on 2017/10/10 0010.
 */
@Component
public class TimeseriesDaoImpl implements TimeseriesDao {
    @Override
    public ListenableFuture<List<TsKvEntry>> findAllAsync(EntityId entityId, List<TsKvQuery> queries) {
        return null;
    }

    @Override
    public ListenableFuture<TsKvEntry> findLatest(EntityId entityId, String key) {
        return null;
    }

    @Override
    public ListenableFuture<List<TsKvEntry>> findAllLatest(EntityId entityId) {
        return null;
    }

    @Override
    public ListenableFuture<Void> save(EntityId entityId, TsKvEntry tsKvEntry, long ttl) {
        return null;
    }

    @Override
    public ListenableFuture<Void> savePartition(EntityId entityId, long tsKvEntryTs, String key, long ttl) {
        return null;
    }

    @Override
    public ListenableFuture<Void> saveLatest(EntityId entityId, TsKvEntry tsKvEntry) {
        return null;
    }
}
