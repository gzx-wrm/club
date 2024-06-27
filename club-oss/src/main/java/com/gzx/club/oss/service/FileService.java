package com.gzx.club.oss.service;

import com.gzx.club.oss.adapter.StorageAdapter;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

/**
 * 文件存储service
 *
 * @author: ChickenWing
 * @date: 2023/10/14
 */
@Service
public class FileService {

    @Resource
    private StorageAdapter storageAdapter;

    /**
     * 列出所有桶
     */
    public List<String> getAllBucket() {
        return storageAdapter.getAllBucket();
    }

    public String getUrl(String bucketName, String objectName) {
        return storageAdapter.getUrl(bucketName, objectName);
    }

    public String uploadFile(MultipartFile uploadFile, String bucket, String objectName) {
        storageAdapter.uploadFile(uploadFile, bucket, objectName);
        return this.getUrl(bucket, objectName + "/" + uploadFile.getOriginalFilename());
    }
}

