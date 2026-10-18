package com.mftplus.mftonlinestoreee.organization.cache;

import com.mftplus.mftonlinestoreee.organization.cache.CacheInvalidation.Region;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.TransactionPhase;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbException;
import jakarta.transaction.TransactionSynchronizationRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.RedisClient;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.jedis.params.SetParams;

import java.util.Optional;
import java.util.Set;

@ApplicationScoped
public class RedisDtoCache {
    private static final Logger log = LoggerFactory.getLogger(RedisDtoCache.class);
    private static final String PREFIX = "mft-store:cache:";
    private static final int TTL_SECONDS = 300;

    @Resource(lookup = "java:comp/TransactionSynchronizationRegistry")
    private TransactionSynchronizationRegistry transactions;

    private RedisClient client;
    private Jsonb jsonb;

    @PostConstruct
    void start() {
        String host = setting("STORE_REDIS_HOST", "localhost");
        int port = Integer.parseInt(setting("STORE_REDIS_PORT", "6379"));
        client = RedisClient.create(host, port);
        jsonb = JsonbBuilder.create();
        clear(PREFIX + "*");
    }

    @PreDestroy
    void stop() {
        client.close();
        try {
            jsonb.close();
        } catch (Exception exception) {
            log.warn("Could not close JSON binding", exception);
        }
    }

    public boolean inTransaction() {
        return transactions.getTransactionKey() != null;
    }

    public <T> Optional<T> get(Region region, Long id, Class<T> type) {
        if (id == null) {
            return Optional.empty();
        }
        try {
            String value = client.get(key(region, id));
            return value == null ? Optional.empty() : Optional.of(jsonb.fromJson(value, type));
        } catch (JedisException | JsonbException exception) {
            log.warn("Could not read Redis cache", exception);
            return Optional.empty();
        }
    }

    public void put(Region region, Long id, Object value) {
        if (id == null || value == null) {
            return;
        }
        try {
            client.set(key(region, id), jsonb.toJson(value), SetParams.setParams().ex(TTL_SECONDS));
        } catch (JedisException | JsonbException exception) {
            log.warn("Could not write Redis cache", exception);
        }
    }

    public void invalidate(@Observes(during = TransactionPhase.AFTER_SUCCESS) CacheInvalidation change) {
        if (change.region() == Region.ALL_DEPARTMENTS) {
            clear(PREFIX + "department:*");
            return;
        }
        try {
            client.del(key(change.region(), change.id()));
        } catch (JedisException exception) {
            log.warn("Could not invalidate Redis cache", exception);
        }
    }

    private void clear(String pattern) {
        try {
            Set<String> keys = client.keys(pattern);
            if (!keys.isEmpty()) {
                client.del(keys.toArray(String[]::new));
            }
        } catch (JedisException exception) {
            log.warn("Could not clear Redis cache", exception);
        }
    }

    private static String key(Region region, Long id) {
        return PREFIX + (region == Region.ORGANIZATION ? "organization:" : "department:") + id;
    }

    private static String setting(String name, String defaultValue) {
        return System.getProperty(name, System.getenv().getOrDefault(name, defaultValue));
    }
}
