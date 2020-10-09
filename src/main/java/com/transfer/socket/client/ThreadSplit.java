package com.transfer.socket.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * @author admin
 * @version 1.0
 * @date 2020/10/4 22:28
 * @className ThreadSplit
 * @projectName socket-transfer
 */
public class ThreadSplit implements Runnable {
    /**
     * 所需传输的文件全路径
     */
    String FilePath;
    /**
     * 文件的下架标
     */
    int count;
    /**
     * 切的文件的开始的位置
     */
    long startOffset;
    /**
     * 切的文件的结束位置
     */
    long endOffset;

    public ThreadSplit(String FilePath, int count, long startOffset, long endOffset) {
        this.FilePath = FilePath;
        this.count = count;
        this.startOffset = startOffset;
        this.endOffset = endOffset;
    }

    @Override
    public void run() {
        File file = new File(FilePath);
        FileInputStream fis = null;
        FileOutputStream fos = null;
        //将文件分割成5份
        file.getAbsolutePath();
        file.getName();
        String split = "D:\\split\\";
        File directory = new File(split);
        if(!directory.exists()) {
            directory.mkdir();
        }

        try {
            fis = new FileInputStream(file);
            fos = new FileOutputStream(split  + file.getName() + "_" + count);
            fis.skip(startOffset);

            // 开始接收文件
            byte[] bytes = new byte[1024*1024];
            int length = 0;
            if(startOffset + bytes.length >= endOffset){
                length =(int)(endOffset - startOffset) ;
            }else{
                length = bytes.length;
            }
            while ((length = fis.read(bytes, 0, length)) != -1 && startOffset <= endOffset) {
                fos.write(bytes, 0, length);
                fos.flush();
                startOffset += length;
                //是否本次读取完毕
                //判断这次读取数据是不是刚好读取到最后
                if (startOffset == endOffset) {
                    break;
                }
                //判断下次读取数据是不是读取到最后
                if (startOffset + length >= endOffset) {
                    //那么下一次的读取的剩余数量就是
                    length = (int) (endOffset - startOffset);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception e) {
            }

        }
    }
}
