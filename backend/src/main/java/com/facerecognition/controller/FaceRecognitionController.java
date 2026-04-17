package com.facerecognition.controller;

import com.facerecognition.model.FaceLibrary;
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
    
    // 获取图片 - 通过文件 ID 访问，防止路径遍历攻击
    @GetMapping("/image/{id}")
    public void getImage(@PathVariable Long id, HttpServletResponse response) throws IOException {
        try {
            FaceLibrary faceLibrary = faceRecognitionService.getFaceLibraryById(id);
            if (faceLibrary == null || faceLibrary.getFilePath() == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            
            File file = new File(faceLibrary.getFilePath());
            
            // 验证文件是否存在
            if (!file.exists()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            
            // 验证文件是否在上传目录内，防止路径遍历攻击
            String uploadDir = System.getProperty("user.dir") + "/uploads";
            File uploadDirectory = new File(uploadDir);
            String canonicalPath = file.getCanonicalPath();
            String canonicalUploadDir = uploadDirectory.getCanonicalPath();
            
            if (!canonicalPath.startsWith(canonicalUploadDir)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            
            // 设置响应类型为图片
            String contentType = getContentType(file);
            response.setContentType(contentType);
            
            // 使用 try-with-resources 确保资源正确关闭
            try (FileInputStream fis = new FileInputStream(file);
                 OutputStream os = response.getOutputStream()) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
                os.flush();
            }
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    
    // 根据文件扩展名获取 Content-Type
    private String getContentType(File file) {
        String fileName = file.getName().toLowerCase();
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (fileName.endsWith(".png")) {
            return "image/png";
        } else if (fileName.endsWith(".gif")) {
            return "image/gif";
        } else {
            return "application/octet-stream";
        }
    }
}