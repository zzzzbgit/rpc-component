package com.shudun.test.client;

import com.shudun.base.constants.ConfigConstants;
import com.shudun.client.builder.ClientBuilder;
import com.shudun.client.config.Config;
import com.shudun.client.config.ConfigResolver;
import com.shudun.test.interfaces.TestService;

import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

public class TestClient {

    static TestService testService;

    static {
        Properties properties = new Properties();
        properties.put(ConfigConstants.CLENT_SERVERADDRESS, "10.1.20.79:9992");
        properties.put(ConfigConstants.CLIENT_MAXCONNECTIONS, "384");

        Config config = ConfigResolver.ResolverProperties(properties);
//        testService = ClientBuilder.getInstance(ClientBuilder.NETTY, config).build(TestService.class, null);
        testService = ClientBuilder.getInstance(ClientBuilder.NETTY, config).build(TestService.class, null);
    }

    public static void main(String[] args) throws InterruptedException {
        while (true) {
            System.out.println("清输入线程数:");
            Scanner scanner = new Scanner(System.in);
            String threadNumStr = scanner.next();
            int threadNum = Integer.parseInt(threadNumStr);
            System.out.println("清输入循环次数:");
            String loopStr = scanner.next();
            int loop = Integer.parseInt(loopStr);
            AtomicLong time = new AtomicLong(0);
            CountDownLatch countDownLatch = new CountDownLatch(threadNum);
            for (int i = 0; i < threadNum; i++) {
                new Thread(() -> {
                    long start = System.currentTimeMillis();
                    for (int j = 0; j < loop; j++) {
                        byte[] random = testService.random("1", 16);
                    }
                    long end = System.currentTimeMillis();
                    countDownLatch.countDown();
                    time.addAndGet(end - start);
                }).start();
            }
            countDownLatch.await();
            System.out.println( ((threadNum * loop) / ((double)(time.get() / threadNum) / 1000)));
        }
    }
}
