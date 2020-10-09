package com.transfer.socket.client;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author admin
 * @version 1.0
 * @date 2020/10/5 4:06
 * @className FileUtils
 * @projectName socket-transfer
 */
public class FileUtils {
    /**
     * 记录传送的文件以及文件的传送进度
     */
    public static Map<String, FileInfo> fileMap = new ConcurrentHashMap<>();

    /**
     * 是否停止传输文件，默认停止
     */
    public static volatile boolean stop = true;

    /**
     * 文件传送完成之后清除本地的碎文件以及清除FileMap中已经完成的数据
     */
    public static void deleteSplitFiles() {
        File file = new File("/Users/liuliyuan/Movies/split/");
        File[] files = file.listFiles();
        List<String> finsh = new ArrayList<>();
        //找到完成了的文件
        finsh = fileMap.entrySet().stream().filter(entry ->
                entry.getValue().getFileLength() == entry.getValue().getTransferLength())
                .map(entry -> entry.getKey()).collect(Collectors.toList());
        //fileMap 删除已经完成了的
        fileMap.entrySet().removeIf(entry -> entry.getValue().getTransferLength() == entry.getValue().getFileLength());
        //删除已经完成了的文件
        for (File ele : files) {
            //本地盘的文件 qqq.txt_1  变成 qqq.txt
            String name = ele.getName().substring(0, ele.getName().lastIndexOf("_"));
            //完成的文件 qqq.txt
            if (finsh.contains(name)) {
                ele.delete();
            }
        }


    }


    /**
     * 记录文件传送了多少
     *
     * @param fileName 文件名
     * @param length   本次传送的长度
     */
    public static synchronized void addTrans(String fileName, int length) {
        Iterator<Map.Entry<String, FileInfo>> it = fileMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, FileInfo> next = it.next();
            String key = next.getKey();
            FileInfo value = next.getValue();
            //找到需要增加文件长度的那个文件
            if (fileName.indexOf(key) != -1) {
                long transferLength = value.getTransferLength();
                transferLength += length;
                //设置添加的文件的长度
                value.setTransferLength(transferLength);
            }


        }
    }


    /**
     * 将某个目录下的文件全部切碎到某个固定的文件夹下面
     *
     * @param directoryPath 需要传送的文件的目录
     */
    public static void split(String directoryPath) {
        File file = new File(directoryPath);
        File[] files = file.listFiles();
        List<Thread> threads = new ArrayList<>();
        //开启多线程进行切割
        //先对文件进行大小的处理
        Thread thread = null;
        for (File ele : files) {
            long length = ele.length();
            long offset = length / 5;
            for (int i = 0; i < 5; i++) {
                ////切割之前，先将原来所有的文件记录一下
                FileInfo fileInfo = new FileInfo(ele.getName(), ele.length(), 0);
                fileMap.put(ele.getName(), fileInfo);

                //切割文件
                if (i == 4) {
                    thread = new Thread(new ThreadSplit(ele.getAbsolutePath(),
                            i + 1, i * offset, length));
                } else {
                    thread = new Thread(new ThreadSplit(ele.getAbsolutePath(),
                            i + 1, i * offset, offset * (i + 1)));
                }
                thread.start();
                threads.add(thread);
            }

        }
        //判断线程结束既文件是否切割完毕
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
        System.out.println("-------------------切割完毕-----------------");

    }

    /**
     * 停止文件传输
     */
    public static void stopTrans() {
        stop = true;
    }

    /**
     * 开启文件传输
     */
    public static void openTrans() {
        stop = false;
    }
}
