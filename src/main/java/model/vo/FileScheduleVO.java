package model.vo;

import com.transfer.socket.client.FileInfo;
import lombok.Data;

import java.util.List;

/**
 * @program: socket-transfer-client
 * @description: 文件传输进度实体类
 * @author: LiyuanLiu
 * @create: 2020-10-06 13:23
 **/
@Data
public class FileScheduleVO {
    /**
     * 文件名称
     */
    private String fileFullName;
    /**
     * 文件被分割块数
     */
    private Integer fileSplitNum;
    /**
     * 文件总长
     */
    private Long fileFullLength;
    /**
     * 文件已传输总长
     */
    private Long fileTransferFullLength;
    /**
     * 文件传输百分比
     */
    private String transferFullPercentage;
    /**
     * 文件状态
     * 0:传输完成
     * 1:正在传输
     * 2:暂停
     * 3:传输失败
     */
    private Integer status;
    /**
     * 切割块信息实体
     */
    private List<SplitFileInfo> splitFileInfos;
}
