package com.shudun.client.pool;



import com.shudun.client.pool.element.PoolElement;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * pool map
 *
 * @param <K>
 * @param <V>
 */
public class PoolMap<K, V extends PoolElement> {

    /**
     * 池 map
     */
    private final Map<K, FixedPool<K, V>> poolMap = new ConcurrentHashMap<>();

    /**
     * 每个 pool 容量
     */
    private final int poolMax;

    /**
     * 创建 V
     */
    private final Function<K, V> function;


    /**
     * 构造方法
     *
     * @param poolMax  pool容量
     * @param function 创建 V
     */
    public PoolMap(int poolMax, Function<K, V> function) {
        this.poolMax = poolMax;
        this.function = function;
    }

    /**
     * 获取池
     *
     * @param k 创建 V 参数
     * @return
     */
    public FixedPool<K, V> get(K k) {
        FixedPool<K, V> pool = poolMap.get(k);
        if (null == pool) {
            pool = FixedPool.build(poolMax, function, k);
            poolMap.put(k, pool);
        }
        return pool;
    }

    /**
     * 销毁池
     *
     * @param k
     */
    public void destory(K k) {
        FixedPool<K, V> pool = poolMap.get(k);
        if (null != pool) {
            pool.close();
        }
    }

    /**
     * 销毁全部池
     */
    public void destory() {
        poolMap.forEach((key, value) -> value.close());
    }
}
