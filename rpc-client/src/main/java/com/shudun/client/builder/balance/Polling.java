package com.shudun.client.builder.balance;

import com.shudun.base.enums.ErrorEnum;
import com.shudun.base.exception.NoHostsException;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class Polling implements LoadBalance{

    private final AtomicLong INCR = new AtomicLong(0);

    private static final long MAX =8000000000000000000L;

    @Override
    public InetSocketAddress next(List<InetSocketAddress> addresses) {
        if (addresses.isEmpty()) {
            throw new NoHostsException(ErrorEnum.ALIVE_HOST_IS_NULL.getMessage());
        } else if (addresses.size() == 1) {
            return addresses.get(0);
        } else {
            long andIncrement = INCR.getAndIncrement();
            if (andIncrement > MAX) INCR.set(0);
            return addresses.get((int) (andIncrement % addresses.size()));
        }
    }
}
