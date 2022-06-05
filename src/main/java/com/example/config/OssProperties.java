package com.example.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledFuture;

@Data
@Component
public class OssProperties {

    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.accesskey}")
    private String accessKey;

    @Value("${minio.secretKey}")
    private String secretKey;

    /**
     * 默认存储桶
     */
    @Value("${minio.bucket.bucketName}")
    private String defaultBucket;

    /**
     * 分片存储桶
     */
    @Value("${minio.bucket.m3u8}")
    private String m3u8;

    /**
     * @program: simple-demo
     * @description: 定时任务控制类
     * @author: CaoTing
     * @date: 2019/5/23
     **/
    public static final class ScheduledTask {

        public volatile ScheduledFuture<?> future;
        /**
         * 取消定时任务
         */
        public void cancel() {
            ScheduledFuture<?> future = this.future;
            if (future != null) {
                future.cancel(true);
            }
        }
    }
}
