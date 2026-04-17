package com.facerecognition.controller;

import com.facerecognition.service.FaceRecognitionService;
import com.facerecognition.service.FaceRecognitionService.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@RestController
@RequestMapping("/api/face")
public class FaceRecognitionController {
    
    @Autowired
    private FaceRecognitionService faceRecognitionService;
    
    // 上传人脸图片
    @PostMapping("/upload")
    public boolean uploadFaceImages(@RequestParam("files") MultipartFile[] files) throws IOException {
        return faceRecognitionService.uploadFaceImages(files);
    }
    
    // 搜索人脸
    @PostMapping("/search")
    public List<SearchResult> searchFace(@RequestParam("file") MultipartFile file) throws IOException {
        return faceRecognitionService.searchFace(file);
    }
    
    // 获取图片
    @GetMapping("/image")
    public void getImage(@RequestParam("path") String path, HttpServletResponse response) throws IOException {
        File file = new File(path);
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file);
                 OutputStream os = response.getOutputStream()) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
            }
        }
    }
}