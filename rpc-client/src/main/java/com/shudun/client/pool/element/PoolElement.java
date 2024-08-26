package com.shudun.client.pool.element;

/**
 * 池元素
 *
 * @param <T> execute的入参
 * @param <R> execute的返回值
 */
public interface PoolElement<T, R> {

    /**
     * 重置
     */
    void reSet();

    /**
     * 重试到最大次数
     */
    void retry();

    /**
     * 执行
     *
     * @param t 池元素入参
     * @return 池元素返回值
     */
    R execute(T t);

    /**
     * 健康检查
     *
     * @return 健康状态
     */
    boolean isHealth();

    /**
     * 关闭
     */
    void close();

}
