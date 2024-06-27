package com.gzx.club.oss.adapter;

import com.gzx.club.oss.entity.FileInfo;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

/**
 * @program: club
 * @description: 文件存储服务
 * @author: gzx
 * @create: 2024-06-19 23:41
 **/
public interface StorageAdapter {

    /**
     * 创建bucket桶
     */
    void createBucket(String bucket);

    /**
     * 上传文件
     */
    void uploadFile(MultipartFile uploadFile, String bucket, String objectName);

    /**
     * 列出所有桶
     */
    List<String> getAllBucket();

    /**
     * 列出当前桶及文件
     */
    List<FileInfo> getAllFile(String bucket);

    /**
     * 下载文件
     */
    InputStream downLoad(String bucket, String objectName);

    /**
     * 删除桶
     */
    void deleteBucket(String bucket);

    /**
     * 删除文件
     */
    void deleteObject(String bucket, String objectName);

    String getUrl(String bucket, String object);
}
