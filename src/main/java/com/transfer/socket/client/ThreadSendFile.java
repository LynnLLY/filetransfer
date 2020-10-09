package com.transfer.socket.client;


import com.transfer.socket.client.FileUpLoadClient;

import java.io.File;

/**
 * @author admin
 * @version 1.0
 * @date 2020/10/4 19:05
 * @className SendFile
 * @projectName socket-transfer
 */
public class ThreadSendFile implements  Runnable {
    //String filePath;
    File file;
    /**
     * 第几份
     */
    int fragIndex;
    /**
     * 文件分为5份，每一份的开始的偏移量
     */
    long offset;
    /**
     * 第几份文件传送的总长
     */
    long transferLen;

    ThreadSendFile(File file, int fragIndex, long offset, long transferLen){
        this.file = file;
        this.fragIndex = fragIndex;
        this.offset = offset;
        this.transferLen = transferLen;
    }
    @Override
    public void run() {
        FileUpLoadClient fileClient = new FileUpLoadClient();
        fileClient.sendFile(file, fragIndex, offset, transferLen);
    }
}
