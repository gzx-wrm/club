package com.gzx.club.oss.utils;

import com.gzx.club.oss.entity.FileInfo;
import io.minio.*;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: club
 * @description: Minio工具类
 * @author: gzx
 * @create: 2024-06-19 23:25
 **/
@Component
public class MinioUtil {

    @Resource
    MinioClient minioClient;

    /**
     * @Author: gzx
     * @Description: 判断桶是否存在
     * @Params:
     * @return:
     */

    @SneakyThrows
    public boolean isBucketExist(String bucket) {
        return minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
    }

    /**
     * @Author: gzx
     * @Description: 创建桶
     * @Params:
     * @return:
     */

    @SneakyThrows
    public void createBucket(String bucket) {
        if (isBucketExist(bucket)) {
            return;
        }
        minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
    }

    /**
     * @Author: gzx
     * @Description: 上传文件
     * @Params: 
     * @return: 
     */
    public void uploadFile(InputStream inputStream, String bucket, String objectName) throws Exception {
        minioClient.putObject(PutObjectArgs.builder().bucket(bucket).object(objectName)
                .stream(inputStream, -1, Integer.MAX_VALUE).build());
    }

    /**
     * @Author: gzx
     * @Description: 列出所有桶
     * @Params:
     * @return: 桶的名称
     */
    @SneakyThrows
    public List<String> getAllBuckets() {
        List<Bucket> buckets = minioClient.listBuckets();
        return buckets.stream().map(Bucket::name).collect(Collectors.toList());
    }

    @SneakyThrows
    public List<FileInfo> getAllFiles(String bucket) {
        Iterable<Result<Item>> results = minioClient.listObjects(ListObjectsArgs.builder().bucket(bucket).build());
        ArrayList<FileInfo> fileInfos = new ArrayList<>();
        for (Result<Item> result : results) {
            fileInfos.add(new FileInfo(result.get().objectName(), result.get().isDir(), result.get().etag()));
        }
        return fileInfos;
    }

    /**
     * 下载文件
     */
    @SneakyThrows
    public InputStream downLoad(String bucket, String objectName) {
        return minioClient.getObject(
                GetObjectArgs.builder().bucket(bucket).object(objectName).build()
        );
    }

    /**
     * 删除桶
     */
    @SneakyThrows
    public void deleteBucket(String bucket) {
        minioClient.removeBucket(
                RemoveBucketArgs.builder().bucket(bucket).build()
        );
    }

    /**
     * 删除文件
     */
    @SneakyThrows
    public void deleteObject(String bucket, String objectName) {
        minioClient.removeObject(
                RemoveObjectArgs.builder().bucket(bucket).object(objectName).build()
        );
    }

    @SneakyThrows
    public String getUrl(String bucket, String object) {
        return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder().bucket(bucket).object(object).build());
    }
}
