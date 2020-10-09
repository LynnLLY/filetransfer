package com.transfer.socket.client;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author admin
 * @version 1.0
 * @date 2020/10/4 19:05
 * @className SendFile
 * @projectName socket-transfer
 */
public class SendFile implements Runnable{

    public void sendFile() {
        //开启文件传输，默认情况下文件传输是关闭的
        FileUtils.openTrans();
        //全部的多线程
        List<Thread> threads = new ArrayList<>();
        try {
            //传送的文件夹路径
            FileUtils.split("/Users/liuliyuan/Movies/test");
            //"D:\\split" 代表着 传送的文件切割完毕之后存放的文件夹的路径
            File file = new File("/Users/liuliyuan/Movies/split");
            File[] files = file.listFiles();
            for (File ele : files) {
                Thread thread = new Thread(new ThreadSendFile(ele.getPath()));
                //文件传送
                thread.start();
                threads.add(thread);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ////文件停止传输
        //FileUtils.stopTrans();

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
        System.out.println("没有删除前fileMap" + FileUtils.fileMap.toString());
        //文件传送完毕之后，需要删除本地的切的碎文件，以及清除FileMap
        FileUtils.deleteSplitFiles();
        //删除完毕的
//        fileMap.entrySet().removeIf(entry -> entry.getValue().getTransferLength() == entry.getValue().getFileLength());
        //删除完毕的之后，再删除本地的切碎的文件
        System.out.println("删除后fileMap" + FileUtils.fileMap.toString());
    }


    @Override
    public void run() {
        //开启文件传输，默认情况下文件传输是关闭的
        FileUtils.openTrans();
        //全部的多线程
        List<Thread> threads = new ArrayList<>();
        try {
            //传送的文件夹路径
            FileUtils.split("/Users/liuliyuan/Movies/test");
            //"D:\\split" 代表着 传送的文件切割完毕之后存放的文件夹的路径
            File file = new File("/Users/liuliyuan/Movies/split");
            File[] files = file.listFiles();
            for (File ele : files) {
                Thread thread = new Thread(new ThreadSendFile(ele.getPath()));
                //文件传送
                thread.start();
                threads.add(thread);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ////文件停止传输
        //FileUtils.stopTrans();

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
        System.out.println("没有删除前fileMap" + FileUtils.fileMap.toString());
        //文件传送完毕之后，需要删除本地的切的碎文件，以及清除FileMap
        FileUtils.deleteSplitFiles();
        //删除完毕的
//        fileMap.entrySet().removeIf(entry -> entry.getValue().getTransferLength() == entry.getValue().getFileLength());
        //删除完毕的之后，再删除本地的切碎的文件
        System.out.println("删除后fileMap" + FileUtils.fileMap.toString());
    }
}
