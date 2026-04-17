package com.facerecognition.service;

import com.facerecognition.model.FaceLibrary;
import com.facerecognition.mapper.FaceLibraryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.logging.Logger;

@Service
public class FaceRecognitionService {
    
    private static final Logger logger = Logger.getLogger(FaceRecognitionService.class.getName());
    
    @Autowired
    private FaceLibraryMapper faceLibraryMapper;
    
    @Value("${file.upload-dir}")
    private String uploadDir;
    
    // 上传人脸图片到人脸库
    public boolean uploadFaceImages(MultipartFile[] files) throws IOException {
        // 创建上传目录
        File uploadDirectory = new File(uploadDir);
        if (!uploadDirectory.exists()) {
            uploadDirectory.mkdirs();
        }
        
        // 创建模型目录
        File modelDirectory = new File("models");
        if (!modelDirectory.exists()) {
            modelDirectory.mkdirs();
        }
        
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                continue;
            }
            
            // 保存文件
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            File dest = new File(uploadDir + File.separator + fileName);
            file.transferTo(dest);
            
            // 模拟人脸检测和特征提取
            FaceLibrary faceLibrary = new FaceLibrary();
            faceLibrary.setName(file.getOriginalFilename());
            faceLibrary.setDescription("Face image");
            faceLibrary.setCreatedAt(LocalDateTime.now());
            faceLibrary.setUpdatedAt(LocalDateTime.now());
            faceLibrary.setIsDeleted(0);
            faceLibrary.setFilePath(dest.getAbsolutePath());
            
            // 生成模拟的人脸特征
            String embedding = generateMockEmbedding(file.getOriginalFilename());
            faceLibrary.setFaceEmbedding(embedding);
            
            try {
                faceLibraryMapper.insert(faceLibrary);
                logger.info("Face image uploaded successfully: " + file.getOriginalFilename());
            } catch (Exception e) {
                logger.warning("Failed to save face image to database: " + e.getMessage());
                // 即使数据库保存失败，也继续处理其他文件
            }
        }
        
        return true;
    }
    
    // 搜索人脸
    public List<SearchResult> searchFace(MultipartFile file) throws IOException {
        // 保存临时文件
        String fileName = "temp.jpg";
        File dest = new File(uploadDir + File.separator + fileName);
        file.transferTo(dest);
        
        List<SearchResult> results = new ArrayList<>();
        
        // 生成模拟的查询人脸特征
        String queryEmbedding = generateMockEmbedding(file.getOriginalFilename());
        
        try {
            // 查询所有人脸库中的记录
            List<FaceLibrary> allFaces = faceLibraryMapper.selectList(null);
            
            // 计算相似度并排序
            for (FaceLibrary face : allFaces) {
                double similarity = calculateSimilarity(queryEmbedding, face.getFaceEmbedding());
                if (similarity > 0.5) { // 相似度阈值
                    SearchResult result = new SearchResult();
                    result.setImageUrl("/api/face/image?path=" + face.getFilePath());
                    result.setFileName(face.getName());
                    result.setSimilarity((int) (similarity * 100));
                    results.add(result);
                }
            }
            logger.info("Face search completed successfully");
        } catch (Exception e) {
            logger.warning("Failed to search face in database: " + e.getMessage());
            // 即使数据库查询失败，也返回空结果
        }
        
        return results;
    }
    
    // 生成模拟的人脸特征
    private String generateMockEmbedding(String fileName) {
        // 简单地使用文件名的哈希值作为模拟的人脸特征
        return Base64.getEncoder().encodeToString(fileName.getBytes());
    }
    
    // 计算相似度（简化实现）
    private double calculateSimilarity(String embedding1, String embedding2) {
        // 解码Base64
        byte[] data1 = Base64.getDecoder().decode(embedding1);
        byte[] data2 = Base64.getDecoder().decode(embedding2);
        
        // 计算余弦相似度
        double dotProduct = 0;
        double norm1 = 0;
        double norm2 = 0;
        
        for (int i = 0; i < data1.length && i < data2.length; i++) {
            dotProduct += data1[i] * data2[i];
            norm1 += data1[i] * data1[i];
            norm2 += data2[i] * data2[i];
        }
        
        if (norm1 == 0 || norm2 == 0) {
            return 0;
        }
        
        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }
    
    // 搜索结果类
    public static class SearchResult {
        private String imageUrl;
        private String fileName;
        private int similarity;
        
        // getters and setters
        public String getImageUrl() {
            return imageUrl;
        }
        
        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }
        
        public String getFileName() {
            return fileName;
        }
        
        public void setFileName(String fileName) {
            this.fileName = fileName;
        }
        
        public int getSimilarity() {
            return similarity;
        }
        
        public void setSimilarity(int similarity) {
            this.similarity = similarity;
        }
    }
}