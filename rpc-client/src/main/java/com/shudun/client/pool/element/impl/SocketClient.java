package com.shudun.client.pool.element.impl;

import com.shudun.client.pool.element.PoolElement;
import com.shudun.client.pool.exception.PoolException;
import lombok.extern.slf4j.Slf4j;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;

/**
 * Socket客户端
 */
@Slf4j
public class SocketClient implements PoolElement<byte[], byte[]> {

    /**
     * Socket
     */
    private Socket socket;

    /**
     * 输入流
     */
    private DataInputStream inputStream;

    /**
     * 输出流
     */
    private DataOutputStream outputStream;

    /**
     * 是否已关闭
     */
    private volatile boolean closed;

    /**
     * 远程地址
     */
    private final InetSocketAddress isa;

    /**
     * 超时时间
     */
    private final int timeout;

    /**
     * 默认重置次数
     */
    private final int DEFAULT_RESET_TIMES = 3;

    /**
     * 构造
     *
     * @param isa     ip:port
     * @param timeout 超时时间
     */
    public SocketClient(InetSocketAddress isa, int timeout) {
        this.isa = isa;
        this.timeout = timeout;
        this.create();
        log.debug("SocketClient:{}.create Success!", this);
    }

    /**
     * 创建
     */
    private void create() {
        try {
            this.socket = new Socket();
            this.socket.connect(this.isa, this.timeout * 1000);
            this.socket.setSoTimeout(this.timeout + 1000);
            this.socket.setTcpNoDelay(true);
            this.socket.setKeepAlive(true);
            this.inputStream = new DataInputStream(this.socket.getInputStream());
            this.outputStream = new DataOutputStream(this.socket.getOutputStream());
            this.outputStream.flush();
            this.closed = false;
        } catch (IOException e) {
            log.error("SocketClient:{}.create Error!", this, e);
            throw new PoolException("SocketClient.create Error!", e);
        }
    }

    /**
     * 重置
     */
    @Override
    public void reSet() {
        close();
        create();
        log.debug("SocketClient:{}.reSet!", this);
    }

    /**
     * 重试到最大次数
     */
    @Override
    public void retry() {
        int retry = 1;
        while (retry <= DEFAULT_RESET_TIMES) {
            log.debug("SocketClient:{}.retry start! times:{}", this, retry);
            try {
                reSet();
                break;
            } catch (PoolException pe) {
                log.error("SocketClient:{}.retry Error! times:{}", this, retry, pe);
                retry++;
            }
        }
    }

    /**
     * 执行
     *
     * @param inData 输入数据
     * @return
     */
    @Override
    public byte[] execute(byte[] inData) {
        /*发送消息*/
        ByteBuffer lengthBuffer = ByteBuffer.allocate(4 + inData.length);
        lengthBuffer.putInt(inData.length);
        lengthBuffer.put(inData);
        lengthBuffer.flip();
        byte[] sendMessage = lengthBuffer.array();
        lengthBuffer.clear();
        try {
            send(sendMessage);
            return recv();
        } catch (IOException e) {
            log.error("SocketClient:{}.execute(),IOException Error!", this, e);
            if (e instanceof EOFException || e instanceof SocketException) {
                retry();
                try {
                    send(sendMessage);
                    return recv();
                } catch (IOException e1) {
                    throw new PoolException("SocketClient.execute Error!", e1);
                }
            }
            throw new PoolException("SocketClient.execute Error!", e);
        }
    }

    /**
     * 发送
     *
     * @param sendMessage 发送数据
     * @throws IOException
     */
    private void send(byte[] sendMessage) throws IOException {
        outputStream.write(sendMessage);
        /*刷新输出流以确保数据被发送*/
        outputStream.flush();
    }

    /**
     * 接收
     *
     * @return 接收数据
     * @throws IOException
     */
    private byte[] recv() throws IOException {
        int recvLen = inputStream.readInt();
        byte[] recvData = new byte[recvLen];
        inputStream.read(recvData);
        return recvData;
    }

    @Override
    public boolean isHealth() {
        if (null != socket) {
            return socket.isConnected() && !socket.isClosed() && !this.closed;
        }
        return false;
    }

    /**
     * 关闭资源
     */
    @Override
    public void close() {
        try {
            this.closed = true;
            if (null != outputStream) {
                outputStream.close();
            }
            if (null != inputStream) {
                inputStream.close();
            }
            if (null != socket) {
                socket.close();
            }
        } catch (IOException e) {
            // Ignore IOException on close
        } finally {
            this.outputStream = null;
            this.inputStream = null;
            this.socket = null;
        }
    }

    public boolean isClosed() {
        return closed;
    }

    public InetSocketAddress getIsa() {
        return isa;
    }

    public int getTimeout() {
        return timeout;
    }
}
