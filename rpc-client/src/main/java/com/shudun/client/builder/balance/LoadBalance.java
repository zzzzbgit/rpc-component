package com.shudun.client.builder.balance;

import java.net.InetSocketAddress;
import java.util.List;

public interface LoadBalance {

    InetSocketAddress next(List<InetSocketAddress> addresses);

}
