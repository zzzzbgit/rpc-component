package com.leyi.client.builder;

import com.leyi.client.builder.protocol.Client;
import com.leyi.client.config.Config;

/**
 * 定时扫描存活服务
 */
public class RefreshAliveHostThread extends Thread{

    public static void Launch(Client client) {
        Config config = client.getConfig();
        new RefreshAliveHostThread(config.getDynamicInterval(), client).start();
    }

    private long intervalTime;

    private Client client;

    /**
     * 构造方法
     * @param intervalTime intervalTime 间隔时间（秒）
     * @param client client
     */
    public RefreshAliveHostThread(Integer intervalTime, Client client) {
        this.intervalTime = intervalTime * 1000;
        this.client = client;
    }


    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        while (true) {
            /*初始化完成时已经获取过了*/
            try {
                Thread.sleep(intervalTime);
                client.resetAliveHost();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

