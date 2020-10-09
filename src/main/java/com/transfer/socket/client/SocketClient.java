package com.transfer.socket.client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * @author admin
 * @version 1.0
 * @date 2020/10/4 19:04
 * @className ThreadLocal
 * @projectName socket-transfer
 */
public class SocketClient extends Socket {
    private Socket client;                //  Socket-客户端

    /**
     * 构造器
     *
     * @param ip   服务端IP地址
     * @param port 服务端开放的端口
     * @throws UnknownHostException
     * @throws IOException
     */
    public SocketClient(String ip, Integer port) throws UnknownHostException, IOException {
        super(ip, port);
        this.client = this;
        if (client.getLocalPort() > 0) {
            System.out.println("Cliect[port:" + client.getLocalPort() + "] 成功连接服务端");
        } else {
            System.out.println("服务器连接失败");
        }
    }
}
