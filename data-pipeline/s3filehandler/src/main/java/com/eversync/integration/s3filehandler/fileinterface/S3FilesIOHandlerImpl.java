package com.eversync.integration.s3filehandler.fileinterface;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.minio.BucketExistsArgs;
import io.minio.DownloadObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.SetBucketNotificationArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import io.minio.messages.EventType;
import io.minio.messages.NotificationConfiguration;
import io.minio.messages.QueueConfiguration;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class S3FilesIOHandlerImpl implements S3FilesIOHandler {

    @Value("${spring.minio.url:http://minio:9000}")
    public String s3EndpointyUrl;

    @Value("${minio.credentials.access.key:msBOBrevHM5urFH}")
    public String minioAccessKey;

    @Value("${minio.credentials.secret.key:SNp6gbBq9rymy2JcqAS2LaDfx2yucf}")
    public String minioSecretKey;

    private MinioClient minioClient;

    @Override
    public void processS3File(String bucketAndFileName) {

        try {
            minioClient = getMinioClient();

            if (verifyBucket(getBucketName(bucketAndFileName), minioClient)) {
                minioClient.downloadObject(DownloadObjectArgs.builder()
                        .bucket(getBucketName(bucketAndFileName))
                        .filename("/site/uploads/" + getFileName(bucketAndFileName))
                        .object(getFileName(bucketAndFileName))
                        .build());

                        // throw a new Springevent for downloaded file and let the listener process it

            } else {
                throw new Exception("No Such Bucket Exists");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private MinioClient getMinioClient() {
        this.minioClient = (minioClient == null) ? MinioClient.builder()
                .endpoint(s3EndpointyUrl)
                .credentials(minioAccessKey, minioSecretKey)
                .build() : minioClient;

        return minioClient;
    }

    private String getBucketName(String bucketAndFileName) {
        return bucketAndFileName.substring(0, bucketAndFileName.indexOf('/'));
    }

    private String getFileName(String bucketAndFileName) {
        return bucketAndFileName.substring(bucketAndFileName.indexOf('/'), bucketAndFileName.length());
    }

    private boolean verifyBucket(String bucketName, MinioClient minioClient)
            throws ErrorResponseException, InsufficientDataException, InternalException, InvalidKeyException,
            InvalidResponseException, IOException, NoSuchAlgorithmException, ServerException, XmlParserException {
        return minioClient.bucketExists(BucketExistsArgs
                .builder()
                .bucket(bucketName)
                .region("ap-south-2")
                .build());
    }

    @Override
    public boolean createEverSyncBucketIfNotExists()
            throws ErrorResponseException, InsufficientDataException, InternalException, InvalidKeyException,
            InvalidResponseException, IOException, NoSuchAlgorithmException, ServerException, XmlParserException {
        if (!verifyBucket("eversync", getMinioClient())) {
            log.info("Creating bucket <eversync>");

            minioClient.makeBucket(MakeBucketArgs
                    .builder()
                    .bucket("eversync")
                    .region("ap-south-2")
                    .build());

            log.info("Adding event notifications for bucket <eversync>");
            minioClient.setBucketNotification(getBucketNotificationDetails());
        }

        return true;
    }

    private SetBucketNotificationArgs getBucketNotificationDetails() {
        NotificationConfiguration config = new NotificationConfiguration();

        QueueConfiguration postgreSQLEventConfiguration = new QueueConfiguration();
        postgreSQLEventConfiguration.setQueue("arn:minio:sqs:ap-south-2:S3FilePushedEvent:postgresql");

        List<EventType> eventList = new LinkedList<>();
        eventList.add(EventType.OBJECT_CREATED_PUT);
        postgreSQLEventConfiguration.setEvents(eventList);

        List<QueueConfiguration> queueConfigurationList = new ArrayList<QueueConfiguration>();
        queueConfigurationList.addAll(config.queueConfigurationList());
        queueConfigurationList.add(postgreSQLEventConfiguration);

        config.setQueueConfigurationList(queueConfigurationList);

        return SetBucketNotificationArgs
                .builder()
                .config(config)
                .bucket("eversync")
                .region("ap-south-2")
                .build();
    }

}