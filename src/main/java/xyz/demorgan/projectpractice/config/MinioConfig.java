package xyz.demorgan.projectpractice.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {
    @Value("${minio.endpoint}")
    private String minioEndpoint;

    @Value("${minio.root-user}")
    private String minioUser;

    @Value("${minio.root-password}")
    private String minioPassword;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(minioEndpoint)
                .credentials(minioUser, minioPassword)
                .build();
    }

    @Bean
    public boolean initializeResumeBucket(MinioClient minioClient) {
        try {
            String bucketName = "resume";
            boolean isExist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!isExist) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Error creating bucket", e);
        }
    }

    @Bean
    public boolean initializePresentationBucket(MinioClient minioClient) {
        try {
            String bucketName = "presentation";
            boolean isExist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!isExist) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Error creating bucket", e);
        }
    }

    @Bean
    public boolean initializeTechnicalSpecificationsBucket(MinioClient minioClient) {
        try {
            String bucketName = "technical_specifications";
            boolean isExist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!isExist) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Error creating bucket", e);
        }
    }
}

