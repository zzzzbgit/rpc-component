package com.shudun.client.builder.pool;


import com.shudun.client.builder.pool.element.PoolElement;

public interface AbstractPool<E> {
    E acquire();

    void release(PoolElement element);

    void close();
}
