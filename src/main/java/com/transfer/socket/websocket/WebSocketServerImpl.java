package com.transfer.socket.websocket;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.alibaba.fastjson.JSONObject;
import com.transfer.socket.client.FileInfo;
import com.transfer.socket.client.FileUtils;
import com.transfer.socket.client.SendFile;
import com.transfer.socket.model.vo.FileScheduleVO;
import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


/**
 * WebSocketServer
 *
 * @author zhengkai.blog.csdn.net
 */
@ServerEndpoint("/ws/{userId}")
@Component
public class WebSocketServerImpl {

    static Log log = LogFactory.get(WebSocketServerImpl.class);
    /**
     * 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
     */
    private static int onlineCount = 0;
    /**
     * concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
     */
    private static ConcurrentHashMap<String, WebSocketServerImpl> webSocketMap = new ConcurrentHashMap<>();
    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;
    /**
     * 接收userId
     */
    private String userId = "";
    /**
     * 是否正在发送文件标志位
     */
    private Boolean running = true;
    /**
     * stringFileInfoMap临时备份
     */
    private Map<String, FileInfo> stringFileInfoMapTemp;

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        this.session = session;
        this.userId = userId;
        if (webSocketMap.containsKey(userId)) {
            webSocketMap.remove(userId);
            webSocketMap.put(userId, this);
            //加入set中
        } else {
            webSocketMap.put(userId, this);
            //加入set中
            addOnlineCount();
            //在线数加1
        }

        log.info("用户连接:" + userId + ",当前在线人数为:" + getOnlineCount());

        try {
            sendMessage(getFileList());
        } catch (IOException e) {
            log.error("用户:" + userId + ",网络异常!!!!!!");
        }
    }

    private String getFileList() {
        List<FileScheduleVO> fileScheduleVOS = new ArrayList<>();
        File file = new File("/Users/liuliyuan/Movies/test");
        File[] fileList = file.listFiles();
        for (File oneFile : fileList) {
            FileScheduleVO fileScheduleVO = new FileScheduleVO();
            fileScheduleVO.setFileFullName(oneFile.getName());
            fileScheduleVO.setFileFullLength(oneFile.length());
            fileScheduleVO.setFileTransferFullLength(0L);
            fileScheduleVO.setFileSplitNum(5);
            fileScheduleVO.setStatus(1);
            fileScheduleVO.setTransferFullPercentage("00.00");
            fileScheduleVOS.add(fileScheduleVO);
        }
        System.out.println(JSONObject.toJSONString(fileScheduleVOS));
        return JSONObject.toJSONString(fileScheduleVOS);
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        if (webSocketMap.containsKey(userId)) {
            webSocketMap.remove(userId);
            //从set中删除
            subOnlineCount();
        }
        log.info("用户退出:" + userId + ",当前在线人数为:" + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("用户消息:" + userId + ",报文:" + message);
        message.replaceAll("\"", "");
        if (StringUtils.isNotBlank(message)) {
            try {
                /**
                 * 收到输入start命令开始传输文件
                 */
                if (message.equals("start")) {
                    Thread thread = new Thread(new SendFile());
                    thread.start();
                    Thread.sleep(200);
                    Thread threadCallBack = new Thread(new CallBack());
                    threadCallBack.start();
                    running = true;
                }
                /**
                 * 收到暂停命令暂停传输文件
                 */
                if (message.equals("pause")) {
                    FileUtils.stopTrans();
                }
                /**
                 * 收到继续命令继续传输文件
                 */
                if (message.equals("recover")) {
                    FileUtils.openTrans();
                    running = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class CallBack implements Runnable {

        @Override
        public void run() {
            //传送给对应toUserId用户的websocket
            try {
                while (running) {
                    if (FileUtils.stop) {
                        Thread.sleep(1000);
                        continue;
                    }
                    String fileInfo = getFileScheduleJSON();
                    if (StringUtils.isNotBlank(userId) && webSocketMap.containsKey(userId)) {
                        webSocketMap.get(userId).sendMessage(fileInfo);
                    } else {
                        running = false;
                        log.error("请求的userId:" + userId + "不在该服务器上");
                    }
                    Thread.sleep(1500);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private String getFileScheduleJSON() {
        List<FileScheduleVO> fileScheduleVOS;
        Map<String, FileScheduleVO> stringFileScheduleVOMap = new ConcurrentHashMap<>();
        //获取后台发送程序存储信息的类
        Map<String, FileInfo> stringFileInfoMap = FileUtils.fileMap;
        boolean isFinal = false;
        if (stringFileInfoMap.isEmpty()) {
            isFinal = true;
            running = false;
        } else {
            stringFileInfoMapTemp = (Map<String, FileInfo>) SerializationUtils.clone((ConcurrentHashMap) stringFileInfoMap);
        }
        if (ObjectUtil.isEmpty(stringFileInfoMapTemp)) {
            return "0";
        }
        //对获取的信息类进行处理
        for (Map.Entry<String, FileInfo> entries : stringFileInfoMapTemp.entrySet()) {
            FileInfo fileInfo = entries.getValue();
            //记录父文件的名称
            String fileName = fileInfo.getFileName();
            Long transferLength = fileInfo.getTransferLength();
            Long fileLength = fileInfo.getFileLength();
            Object percentage = Double.longBitsToDouble(transferLength) / Double.longBitsToDouble(fileLength) * 100;

            //如果不存在已有map映射则初始化并put到map
            FileScheduleVO fileScheduleVO = new FileScheduleVO();
            fileScheduleVO.setFileFullName(fileName);
            fileScheduleVO.setFileSplitNum(5);
            fileScheduleVO.setFileFullLength(fileLength);
            fileScheduleVO.setFileTransferFullLength(transferLength);
            if (isFinal) {
                fileScheduleVO.setTransferFullPercentage("100.00");
                fileScheduleVO.setFileTransferFullLength(fileScheduleVO.getFileFullLength());
                fileScheduleVO.setStatus(0);
            } else {
                fileScheduleVO.setTransferFullPercentage(new DecimalFormat("#.00").format(percentage));
                fileScheduleVO.setStatus(1);
            }
            //放入映射map
            stringFileScheduleVOMap.put(fileName, fileScheduleVO);
        }

        //将map的value转化为list
        fileScheduleVOS = stringFileScheduleVOMap.values().stream().collect(Collectors.toList());

        return JSONObject.toJSONString(fileScheduleVOS);
    }

    /**
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("用户错误:" + this.userId + ",原因:" + error.getMessage());
        error.printStackTrace();
    }

    /**
     * 实现服务器主动推送
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }


    /**
     * 发送自定义消息
     */
    public static void sendInfo(String message, @PathParam("userId") String userId) throws IOException {
        log.info("发送消息到:" + userId + "，报文:" + message);
        if (StringUtils.isNotBlank(userId) && webSocketMap.containsKey(userId)) {
            webSocketMap.get(userId).sendMessage(message);
        } else {
            log.error("用户" + userId + ",不在线！");
        }
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocketServerImpl.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketServerImpl.onlineCount--;
    }
}
