package com.transfer.socket.model.vo;

import lombok.Data;

/**
 * @program: socket-transfer-client
 * @description: 切割文件实体
 * @author: LiyuanLiu
 * @create: 2020-10-06 17:37
 **/
@Data
public class SplitFileInfo {
    /**
     * 被切割文件名
     */
    private String splitFileName;
    /**
     * 总长
     */
    private Long splitFileLength;
    /**
     * 已发送的长度
     */
    private Long splitFileTransferLength;
    /**
     * 分块文件传输百分比
     */
    private String transferPercentage;

}
