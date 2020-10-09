package com.transfer.socket.client;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author admin
 * @version 1.0
 * @date 2020/10/4 19:05
 * @className SendFile
 * @projectName socket-transfer
 */
public class SendFile implements Runnable {

    public static void main(String[] args) {
        //开启文件传输，默认情况下文件传输是关闭的
        FileUtils.openTrans();
        //全部的多线程
        List<Thread> threads = new ArrayList<>();
        Map<String, FileInfo> fileMap = FileUtils.fileMap;
        try {
            //传送的文件夹路径
            //FileUtils.split("D:\\sendTest");
            //"D:\\split" 代表着 传送的文件切割完毕之后存放的文件夹的路径
            File file = new File("/Users/liuliyuan/Movies/test");
            File[] fileList = file.listFiles();
            for (File oneFile : fileList) {
                //将文件的信息进行存储
                fileMap.put(oneFile.getName(), new FileInfo(oneFile.getName(), oneFile.length(), 0));

                //此处精确启动每一个文件需要的传送线程，默认每个文件分为5份进行传送
                File sendFile = new File(oneFile.getPath());
                long fileLen = sendFile.length();
                //每一份的平均长度
                long transferLen = fileLen / 5;
                // offset 偏移量
                long offset = 0;
                for (int loop = 0; loop < 5; loop++) {
                    Thread thread = null;
                    offset = loop * transferLen;
                    //最后一次传送字节数量 = 文件总长度 - 已偏移数
                    if (loop == 5 - 1) {
                        thread = new Thread(new ThreadSendFile(sendFile, loop, offset, fileLen - offset));
                    } else {
                        thread = new Thread(new ThreadSendFile(sendFile, loop, offset, transferLen));
                    }
                    //文件传送
                    thread.start();
                    threads.add(thread);
                }
            }
//            Thread.sleep(6000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //文件停止传输
//        FileUtils.stopTrans();

        //判断线程是否结束
        while (true) {
            if (threads.size() != 0) {
                Iterator<Thread> it = threads.iterator();
                while (it.hasNext()) {
                    Thread next = it.next();
                    if (Thread.State.TERMINATED.equals(next.getState())) {
                        it.remove();
                    }
                }
            } else {
                break;
            }
        }
        System.out.println("没有删除前fileMap" + fileMap.toString());
        //文件传送完毕之后，需要删除本地的切的碎文件，以及清除FileMap
        FileUtils.deleteSplitFiles();
        //删除完毕的
//        fileMap.entrySet().removeIf(entry -> entry.getValue().getTransferLength() == entry.getValue().getFileLength());
        //删除完毕的之后，再删除本地的切碎的文件
        System.out.println("删除后fileMap" + fileMap.toString());
    }


    @Override
    public void run() {
        //开启文件传输，默认情况下文件传输是关闭的
        FileUtils.openTrans();
        //全部的多线程
        List<Thread> threads = new ArrayList<>();
        Map<String, FileInfo> fileMap = FileUtils.fileMap;
        try {
            //传送的文件夹路径
            //FileUtils.split("D:\\sendTest");
            //"D:\\split" 代表着 传送的文件切割完毕之后存放的文件夹的路径
            File file = new File("/Users/liuliyuan/Movies/test");
            File[] fileList = file.listFiles();
            for (File oneFile : fileList) {
                //将文件的信息进行存储
                fileMap.put(oneFile.getName(), new FileInfo(oneFile.getName(), oneFile.length(), 0));

                //此处精确启动每一个文件需要的传送线程，默认每个文件分为5份进行传送
                File sendFile = new File(oneFile.getPath());
                long fileLen = sendFile.length();
                //每一份的平均长度
                long transferLen = fileLen / 5;
                // offset 偏移量
                long offset = 0;
                for (int loop = 0; loop < 5; loop++) {
                    Thread thread = null;
                    offset = loop * transferLen;
                    //最后一次传送字节数量 = 文件总长度 - 已偏移数
                    if (loop == 5 - 1) {
                        thread = new Thread(new ThreadSendFile(sendFile, loop, offset, fileLen - offset));
                    } else {
                        thread = new Thread(new ThreadSendFile(sendFile, loop, offset, transferLen));
                    }
                    //文件传送
                    thread.start();
                    threads.add(thread);
                }
            }
//            Thread.sleep(6000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //文件停止传输
//        FileUtils.stopTrans();

        //判断线程是否结束
        while (true) {
            if (threads.size() != 0) {
                Iterator<Thread> it = threads.iterator();
                while (it.hasNext()) {
                    Thread next = it.next();
                    if (Thread.State.TERMINATED.equals(next.getState())) {
                        it.remove();
                    }
                }
            } else {
                break;
            }
        }
        System.out.println("没有删除前fileMap" + fileMap.toString());
        //文件传送完毕之后，需要删除本地的切的碎文件，以及清除FileMap
        FileUtils.deleteSplitFiles();
        //fileMap.clear();
        //删除完毕的
//        fileMap.entrySet().removeIf(entry -> entry.getValue().getTransferLength() == entry.getValue().getFileLength());
        //删除完毕的之后，再删除本地的切碎的文件
        System.out.println("删除后fileMap" + fileMap.toString());
    }
}
