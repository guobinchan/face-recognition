package com.facerecognition.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("face_library")
public class FaceLibrary {
    @TableId
    private Long id;
    
    private String name;
    private String description;
    
    @TableField("created_at")
    private LocalDateTime createdAt;
    
    @TableField("updated_at")
    private LocalDateTime updatedAt;
    
    @TableField("is_deleted")
    private Integer isDeleted;
    
    @TableField("file_path")
    private String filePath;
    
    @TableField("face_embedding")
    private String faceEmbedding;
}