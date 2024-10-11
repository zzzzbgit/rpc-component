package com.leyi.client.builder.pool;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * pool map
 *
 * @param <P>
 * @param <K>
 */
public abstract class PoolMap<K, P extends AbstractPool> {

    /**
     * pool map
     */
    private final Map<K, P> poolMap = new ConcurrentHashMap<>();

    protected abstract P newPool(K key);

    /**
     * get pool
     *
     * @param k get value param
     * @return
     */
    public P get(K k) {
        P pool = this.poolMap.get(k);
        if (null == pool) {
            pool = newPool(k);
            this.poolMap.put(k, pool);
        }
        return pool;
    }

    /**
     * destroy pool
     *
     * @param k key
     */
    public void destroy(K k) {
        P pool = poolMap.get(k);
        if (null != pool) {
            pool.close();
        }
    }

    /**
     * destroy all pool
     */
    public void destroy() {
        poolMap.forEach((key, value) -> value.close());
    }
}
