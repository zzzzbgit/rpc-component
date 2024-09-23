package com.shudun.client.builder.pool.element;

import com.shudun.client.builder.pool.exception.PoolException;
import lombok.extern.slf4j.Slf4j;

/**
 * pool element
 *
 * @param <P> execute param
 * @param <R> execute result
 */
@Slf4j
public abstract class PoolElement<P, R> {

    protected volatile boolean closed;

    /*timeout*/
    protected int timeout;

    /*retry*/
    protected int retryTimes;

    public boolean isClosed() {
        return closed;
    }



    public abstract void init();

    /**
     * execute
     * @param param input data
     * @return
     */
    public abstract R execute(P param);

    /**
     * reset element
     */
    public void reset() {
        close();
        init();
        log.warn("{}:{}.reset!", this.getClass().getSimpleName(), this);
    }

    /**
     * retry
     */
    public void retry() {
        int retry = 1;
        while (retry <= retryTimes) {
            log.debug("{}:{}.retry! times:{}", this.getClass().getSimpleName(), this, retry);
            try {
                reset();
                break;
            } catch (PoolException pe) {
                log.error("{}:{}.retry Error! times:{}", this.getClass().getSimpleName(), this, retry, pe);
                retry++;
            }
        }
    }

    /**
     * health
     *
     * @return health status
     */
    public abstract boolean isHealth();

    /**
     * close
     */
    public abstract void close();

}
