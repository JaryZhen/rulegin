package tb.rulegin.server.actors.plugin;

import akka.actor.ActorRef;
import tb.rulegin.server.common.data.User;
import tb.rulegin.server.actors.service.cluster.ServerAddress;
import tb.rulegin.server.common.data.id.*;
import tb.rulegin.server.core.action.plugins.PluginApiCallSecurityContext;
import tb.rulegin.server.core.action.plugins.PluginCallback;
import tb.rulegin.server.core.action.plugins.PluginContext;
import tb.rulegin.server.actors.msg.ws.PluginWebsocketSessionRef;
import tb.rulegin.server.actors.msg.plugin.torule.PluginToRuleMsg;
import tb.rulegin.server.actors.msg.plugin.aware.PluginWebsocketMsg;
import tb.rulegin.server.common.data.Device;
import tb.rulegin.server.common.data.EntityType;
import tb.rulegin.server.common.data.plugin.PluginMetaData;
import tb.rulegin.server.common.data.rule.RuleMetaData;
import tb.rulegin.server.actors.msg.core.TimeoutMsg;
import com.google.common.base.Function;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import lombok.extern.slf4j.Slf4j;
import tb.rulegin.server.common.data.kv.AttributeKvEntry;
import tb.rulegin.server.common.data.kv.TsKvEntry;
import tb.rulegin.server.common.data.kv.TsKvQuery;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Slf4j
public final class PluginProcessingContext implements PluginContext {

    private static final Executor executor = Executors.newSingleThreadExecutor();

    private final SharedPluginProcessingContext pluginCtx;
    private final Optional<PluginApiCallSecurityContext> securityCtx;

    public PluginProcessingContext(SharedPluginProcessingContext pluginCtx, PluginApiCallSecurityContext securityCtx) {
        super();
        this.pluginCtx = pluginCtx;
        this.securityCtx = Optional.ofNullable(securityCtx);
    }

    public void persistError(String method, Exception e) {
        pluginCtx.persistError(method, e);
    }

    @Override
    public Optional<ServerAddress> resolve(EntityId entityId) {
        return null;
    }

    @Override
    public void send(PluginWebsocketMsg<?> wsMsg) throws IOException {
        pluginCtx.msgEndpoint.send(wsMsg);
    }

    @Override
    public void close(PluginWebsocketSessionRef sessionRef) throws IOException {
        pluginCtx.msgEndpoint.close(sessionRef);
    }

    @Override
    public void saveAttributes(final EntityId entityId, final String scope, final List<AttributeKvEntry> attributes, final PluginCallback<Void> callback) {
        validate(entityId, new ValidationCallback(callback, ctx -> {
            ListenableFuture<List<Void>> futures = null ;///pluginCtx.attributesService.save(entityId, scope, attributes);
            Futures.addCallback(futures, getListCallback(callback, v -> {
                if (entityId.getEntityType() == EntityType.DEVICE) {
                    onDeviceAttributesChanged( new DeviceId(entityId.getId()), scope, attributes);
                }
                return null;
            }), executor);
        }));
    }

    @Override
    public void removeAttributes(EntityId entityId, String scope, List<String> attributeKeys, PluginCallback<Void> callback) {

    }

    @Override
    public void loadAttribute(EntityId entityId, String attributeType, String attributeKey, PluginCallback<Optional<AttributeKvEntry>> callback) {

    }

    @Override
    public void loadAttributes(EntityId entityId, String attributeType, Collection<String> attributeKeys, PluginCallback<List<AttributeKvEntry>> callback) {

    }

    @Override
    public void loadAttributes(EntityId entityId, String attributeType, PluginCallback<List<AttributeKvEntry>> callback) {

    }

    @Override
    public void loadAttributes(EntityId entityId, Collection<String> attributeTypes, PluginCallback<List<AttributeKvEntry>> callback) {

    }

    @Override
    public void loadAttributes(EntityId entityId, Collection<String> attributeTypes, Collection<String> attributeKeys, PluginCallback<List<AttributeKvEntry>> callback) {

    }


    @Override
    public void saveTsData(final EntityId entityId, final TsKvEntry entry, final PluginCallback<Void> callback) {
        validate(entityId, new ValidationCallback(callback, ctx -> {
            ListenableFuture<List<Void>> rsListFuture = pluginCtx.tsService.save(entityId, entry);
            Futures.addCallback(rsListFuture, getListCallback(callback, v -> null), executor);
        }));
    }

    @Override
    public void saveTsData(final EntityId entityId, final List<TsKvEntry> entries, final PluginCallback<Void> callback) {
        saveTsData(entityId, entries, 0L, callback);
    }

    @Override
    public void saveTsData(final EntityId entityId, final List<TsKvEntry> entries, long ttl, final PluginCallback<Void> callback) {
        validate(entityId, new ValidationCallback(callback, ctx -> {
            ListenableFuture<List<Void>> rsListFuture = pluginCtx.tsService.save(entityId, entries, ttl);
            Futures.addCallback(rsListFuture, getListCallback(callback, v -> null), executor);
        }));
    }

    @Override
    public void loadTimeseries(final EntityId entityId, final List<TsKvQuery> queries, final PluginCallback<List<TsKvEntry>> callback) {
        validate(entityId, new ValidationCallback(callback, ctx -> {
            ListenableFuture<List<TsKvEntry>> future = pluginCtx.tsService.findAll(entityId, queries);
            Futures.addCallback(future, getCallback(callback, v -> v), executor);
        }));
    }

    @Override
    public void loadLatestTimeseries(final EntityId entityId, final PluginCallback<List<TsKvEntry>> callback) {
        validate(entityId, new ValidationCallback(callback, ctx -> {
            ListenableFuture<List<TsKvEntry>> future = pluginCtx.tsService.findAllLatest(entityId);
            Futures.addCallback(future, getCallback(callback, v -> v), executor);
        }));
    }

    @Override
    public void loadLatestTimeseries(final EntityId entityId, final Collection<String> keys, final PluginCallback<List<TsKvEntry>> callback) {
        validate(entityId, new ValidationCallback(callback, ctx -> {
            ListenableFuture<List<TsKvEntry>> rsListFuture = pluginCtx.tsService.findLatest(entityId, keys);
            Futures.addCallback(rsListFuture, getCallback(callback, v -> v), executor);
        }));
    }

    @Override
    public void reply(PluginToRuleMsg<?> msg) {
        pluginCtx.parentActor.tell(msg, ActorRef.noSender());
    }

    @Override
    public PluginId getPluginId() {
        return pluginCtx.pluginId;
    }

    //private void onDeviceAttributesDeleted( DeviceId deviceId, Set<AttributeKey> keys) {
    //    pluginCtx.toDeviceActor(DeviceAttributesEventNotificationMsg.onDelete(userId, deviceId, keys));
    //}

    @Override
    public Optional<PluginApiCallSecurityContext> getSecurityCtx() {
        return securityCtx;
    }

    private void onDeviceAttributesChanged(DeviceId deviceId, String scope, List<AttributeKvEntry> values) {
        //pluginCtx.toDeviceActor(DeviceAttributesEventNotificationMsg.onUpdate(userId, deviceId, scope, values));
    }

    private <T, R> FutureCallback<List<T>> getListCallback(final PluginCallback<R> callback, Function<List<T>, R> transformer) {
        return new FutureCallback<List<T>>() {
            @Override
            public void onSuccess(@Nullable List<T> result) {
                pluginCtx.self().tell(PluginCallbackMessage.onSuccess(callback, transformer.apply(result)), ActorRef.noSender());
            }

            @Override
            public void onFailure(Throwable t) {
                if (t instanceof Exception) {
                    pluginCtx.self().tell(PluginCallbackMessage.onError(callback, (Exception) t), ActorRef.noSender());
                } else {
                    log.error("Critical error: {}", t.getMessage(), t);
                }
            }
        };
    }

    private <T, R> FutureCallback<R> getCallback(final PluginCallback<T> callback, Function<R, T> transformer) {
        return new FutureCallback<R>() {
            @Override
            public void onSuccess(@Nullable R result) {
                try {
                    pluginCtx.self().tell(PluginCallbackMessage.onSuccess(callback, transformer.apply(result)), ActorRef.noSender());
                } catch (Exception e) {
                    pluginCtx.self().tell(PluginCallbackMessage.onError(callback, e), ActorRef.noSender());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                if (t instanceof Exception) {
                    pluginCtx.self().tell(PluginCallbackMessage.onError(callback, (Exception) t), ActorRef.noSender());
                } else {
                    log.error("Critical error: {}", t.getMessage(), t);
                }
            }
        };
    }

    @Override
    public void checkAccess(DeviceId deviceId, PluginCallback<Void> callback) {
        validate(deviceId, new ValidationCallback(callback, ctx -> callback.onSuccess(ctx, null)));
    }

    private void validate(EntityId entityId, ValidationCallback callback) {
        if (securityCtx.isPresent()) {
            final PluginApiCallSecurityContext ctx = securityCtx.get();
            if (ctx.isTenantAdmin() || ctx.isCustomerUser() || ctx.isSystemAdmin()) {
                switch (entityId.getEntityType()) {
                    case DEVICE:
                        if (ctx.isSystemAdmin()) {
                            callback.onSuccess(this, Boolean.FALSE);
                        } else {
                            ListenableFuture<Device> deviceFuture = null;//pluginCtx.deviceService.findDeviceByIdAsync(new DeviceId(entityId.getId()));
                            Futures.addCallback(deviceFuture, getCallback(callback, device -> {
                                if (device == null) {
                                    return Boolean.FALSE;
                               /* } else if (ctx.isCustomerUser() && !device.getUserId().equals(ctx.getUserId())) {
                                    return Boolean.FALSE;*/
                                } else {
                                    return Boolean.TRUE;
                                }
                            }));
                        }
                        return;
              /*      case ASSET:
                        if (ctx.isSystemAdmin()) {
                            callback.onSuccess(this, Boolean.FALSE);
                        } else {
                            ListenableFuture<Asset> assetFuture = pluginCtx.assetService.findAssetByIdAsync(new AssetId(entityId.getId()));
                            Futures.addCallback(assetFuture, getCallback(callback, asset -> {
                                if (asset == null) {
                                    return Boolean.FALSE;
                                } else {
                                    if (!asset.getUserId().equals(ctx.getUserId())) {
                                        return Boolean.FALSE;
                                    } else if (ctx.isCustomerUser() && !asset.getUserId().equals(ctx.getUserId())) {
                                        return Boolean.FALSE;
                                    } else {
                                        return Boolean.TRUE;
                                    }
                                }
                            }));
                        }
                        return;*/
                    case RULE:
                        if (ctx.isCustomerUser()) {
                            callback.onSuccess(this, Boolean.FALSE);
                        } else {
                            ListenableFuture<RuleMetaData> ruleFuture = pluginCtx.ruleService.findRuleByIdAsync(new RuleId(entityId.getId()));
                            Futures.addCallback(ruleFuture, getCallback(callback, rule -> {
                                if (rule == null) {
                                    return Boolean.FALSE;
                                } else {
                                    if (ctx.isSystemAdmin() && !rule.getId().isNullUid()) {
                                        return Boolean.FALSE;
                                    } else {
                                        return Boolean.TRUE;
                                    }
                                }
                            }));
                        }
                        return;
                    case PLUGIN:
                        if (ctx.isCustomerUser()) {
                            callback.onSuccess(this, Boolean.FALSE);
                        } else {
                            ListenableFuture<PluginMetaData> pluginFuture = pluginCtx.pluginService.findPluginByIdAsync(new PluginId(entityId.getId()));
                            Futures.addCallback(pluginFuture, getCallback(callback, plugin -> {
                                if (plugin == null) {
                                    return Boolean.FALSE;
                                } else {
                                    if (ctx.isSystemAdmin() && !plugin.getId().isNullUid()) {
                                        return Boolean.FALSE;
                                    } else {
                                        return Boolean.TRUE;
                                    }
                                }
                            }));
                        }
                        return;
                    case CUSTOMER:
                        if (ctx.isSystemAdmin()) {
                            callback.onSuccess(this, Boolean.FALSE);
                        } else {
                            ListenableFuture<User> customerFuture = (ListenableFuture<User>) pluginCtx.customerService.findUserCredentialsByUserId(new UserId(entityId.getId()));
                            Futures.addCallback(customerFuture, getCallback(callback, customer -> {
                                if (customer == null) {
                                    return Boolean.FALSE;
                                } else {
                                    if (ctx.isCustomerUser() && !customer.getId().equals(ctx.getCustomerId())) {
                                        return Boolean.FALSE;
                                    } else {
                                        return Boolean.TRUE;
                                    }
                                }
                            }));
                        }
                        return;
                   /* case TENANT:
                        if (ctx.isCustomerUser()) {
                            callback.onSuccess(this, Boolean.FALSE);
                        } else if (ctx.isSystemAdmin()) {
                            callback.onSuccess(this, Boolean.TRUE);
                        } else {
                            ListenableFuture<Tenant> tenantFuture = pluginCtx.tenantService.findTenantByIdAsync(new TenantId(entityId.getId()));
                            Futures.addCallback(tenantFuture, getCallback(callback, user -> user != null && user.getId().equals(ctx.getUserId())));
                        }
                        return;*/
                    default:
                        //TODO: add support of other entities
                        throw new IllegalStateException("Not Implemented!");
                }
            } else {
                callback.onSuccess(this, Boolean.FALSE);
            }
        }
    }


    @Override
    public void getDevice(DeviceId deviceId, PluginCallback<Device> callback) {
        /*ListenableFuture<Device> deviceFuture = pluginCtx.deviceService.findDeviceByIdAsync(deviceId);
        Futures.addCallback(deviceFuture, getCallback(callback, v -> v));*/
    }

    @Override
    public void getCustomerDevices(UserId userId, int limit, PluginCallback<List<Device>> callback) {
        /*//TODO: add caching here with async api.
        List<Device> devices = pluginCtx.deviceService.findDevicesByTenantIdAndCustomerId(userId, new TextPageLink(limit)).getDataSource();
        pluginCtx.self().tell(PluginCallbackMessage.onSuccess(callback, devices), ActorRef.noSender());*/
    }

    @Override
    public void scheduleTimeoutMsg(TimeoutMsg msg) {
        pluginCtx.scheduleTimeoutMsg(msg);
    }


    private void convertFuturesAndAddCallback(PluginCallback<List<AttributeKvEntry>> callback, List<ListenableFuture<List<AttributeKvEntry>>> futures) {
        ListenableFuture<List<AttributeKvEntry>> future = Futures.transform(Futures.successfulAsList(futures),
                (Function<? super List<List<AttributeKvEntry>>, ? extends List<AttributeKvEntry>>) input -> {
                    List<AttributeKvEntry> result = new ArrayList<>();
                    input.forEach(r -> result.addAll(r));
                    return result;
                }, executor);
        Futures.addCallback(future, getCallback(callback, v -> v), executor);
    }
}
