package com.shudun.client.builder.protocol.socket;

import com.shudun.client.builder.pool.element.PoolElement;
import com.shudun.client.builder.pool.exception.PoolException;
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
 * Socket element
 */
@Slf4j
public class SocketElement extends PoolElement<byte[], byte[]> {

    /*Socket*/
    private Socket socket;

    /*input*/
    private DataInputStream inputStream;

    /*output*/
    private DataOutputStream outputStream;

    /*remote address*/
    private final InetSocketAddress isa;

    /**
     * constructor
     *
     * @param isa     remote address
     * @param timeout timeout
     */
    public SocketElement(InetSocketAddress isa, int timeout) {
        this(isa, timeout, 3);
    }

    /**
     * constructor
     *
     * @param isa        remote address
     * @param timeout    timeout
     * @param retryTimes retry times
     */
    public SocketElement(InetSocketAddress isa, int timeout, int retryTimes) {
        this.retryTimes = retryTimes;
        this.isa = isa;
        this.timeout = timeout;
        this.init();
    }

    /**
     * get
     *
     * @return
     */
    public Socket get() {
        return this.socket;
    }

    /**
     * init
     */
    @Override
    public void init() {
        try {
            this.socket = new Socket();
            this.socket.connect(this.isa, this.timeout * 1000);
            this.socket.setSoTimeout(this.timeout * 1000);
            this.socket.setTcpNoDelay(true);
            this.socket.setKeepAlive(true);
            this.inputStream = new DataInputStream(this.socket.getInputStream());
            this.outputStream = new DataOutputStream(this.socket.getOutputStream());
            this.outputStream.flush();
            this.closed = false;
        } catch (IOException e) {
            log.error("SocketElement:{}.init Error!", this, e);
            throw new PoolException("SocketElement.init Error!", e);
        }
    }


    /**
     * execute
     *
     * @param inData input param
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
            log.error("SocketElement:{}.execute(),IOException Error!", this, e);
            if (e instanceof EOFException || e instanceof SocketException) {
                retry();
                try {
                    send(sendMessage);
                    return recv();
                } catch (IOException e1) {
                    throw new PoolException("SocketElement.execute Error!", e1);
                }
            }
            throw new PoolException("SocketElement.execute Error!", e);
        }
    }

    /**
     * send data
     *
     * @param sendMessage data
     * @throws IOException
     */
    private void send(byte[] sendMessage) throws IOException {
        outputStream.write(sendMessage);
        /*刷新输出流以确保数据被发送*/
        outputStream.flush();
    }

    /**
     * recv data
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
        log.debug("SocketElement:{}.close! ", this);
    }
}
