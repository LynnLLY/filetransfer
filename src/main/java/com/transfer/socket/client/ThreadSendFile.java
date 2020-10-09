package com.transfer.socket.client;


import com.transfer.socket.client.FileUpLoadClient;

/**
 * @author admin
 * @version 1.0
 * @date 2020/10/4 19:05
 * @className SendFile
 * @projectName socket-transfer
 */
public class ThreadSendFile implements  Runnable {
    String filePath;
    ThreadSendFile(String filePath){

        this.filePath = filePath;
    }
    @Override
    public void run() {
        FileUpLoadClient fileClient = new FileUpLoadClient();
        fileClient.sendFile(filePath);
    }
}
