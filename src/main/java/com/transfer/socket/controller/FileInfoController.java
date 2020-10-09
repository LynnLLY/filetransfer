package com.transfer.socket.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: socket-transfer-client
 * @description: mvc
 * @author: LiyuanLiu
 * @create: 2020-10-08 11:38
 **/
@RestController
@RequestMapping("/list")
public class FileInfoController {
    @RequestMapping
    public String getFileList(){

        return "";
    }
}
