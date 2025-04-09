package com.example.controller;

import cn.hutool.core.io.FileUtil;
import com.example.common.Result;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;

@RestController
@RequestMapping("/files")
public class FilesController {

    private static final String ROOT_PATH = System.getProperty("user.dir") + "/files";

    /**
     * 文件上传
     */
    @PostMapping("/upload")
    public Result upload(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        long flag = System.currentTimeMillis();  // 唯一标识
        String fileName = flag + "_" + originalFilename;

        File finalFile = new File(ROOT_PATH + "/" + fileName);
        if (!finalFile.getParentFile().exists()) {
            finalFile.getParentFile().mkdirs();
        }
        file.transferTo(finalFile);
        // 返回文件的url
        String url = "http://localhost:9090/files/download?fileName=" + fileName;
        return Result.success(url);
    }

    /**
     * 文件下载
     */
    @GetMapping("/download")
    public void download(@RequestParam String fileName, HttpServletResponse response) throws IOException {
        File file = new File(ROOT_PATH + "/" + fileName);  // 文件在存盘存储的对象
        ServletOutputStream os = response.getOutputStream();
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
        response.setContentType("application/octet-stream");
        FileUtil.writeToStream(file, os);
        os.flush();
        os.close();
    }
}
