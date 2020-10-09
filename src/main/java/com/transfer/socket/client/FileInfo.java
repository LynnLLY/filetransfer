package com.transfer.socket.client;

import java.io.Serializable;

/**
 * @author admin
 * @version 1.0
 * @date 2020/10/5 0:00
 * @className FileInfo
 * @projectName socket-transfer
 */
public class FileInfo implements Serializable {
    @Override
    public String toString() {
        return "FileInfo{" +
                "fileName='" + fileName + '\'' +
                ", fileLength=" + fileLength +
                ", transferLength=" + transferLength +
                '}';
    }

    /**
     * 文件名字 原始的不带下划线的那个
     */
    String fileName;
    /**
     * 总长
     */
    long fileLength;
    /**
     * 已发送的长度
     */
    long transferLength;

    public FileInfo() {

    }

    public FileInfo(String fileName, long fileLength, long transferLength) {
        this.fileName = fileName;
        this.fileLength = fileLength;
        this.transferLength = transferLength;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileLength() {
        return fileLength;
    }

    public void setFileLength(long fileLength) {
        this.fileLength = fileLength;
    }

    public long getTransferLength() {
        return transferLength;
    }

    public void setTransferLength(long transferLength) {
        this.transferLength = transferLength;
    }
}
