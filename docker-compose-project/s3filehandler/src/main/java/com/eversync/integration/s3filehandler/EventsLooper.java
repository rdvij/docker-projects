package com.eversync.integration.s3filehandler;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.eversync.integration.s3filehandler.data.S3StagedEvents;
import com.eversync.integration.s3filehandler.data.S3StagedEventsDao;
import com.eversync.integration.s3filehandler.fileinterface.S3FilesIOHandler;

import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class EventsLooper {

    @Autowired
    S3StagedEventsDao s3StagedEventsDao;

    @Autowired
    S3FilesIOHandler filesIOHandler;

    @EventListener
    public void initValidations(ContextRefreshedEvent refreshedEvent) {
        try {
            TimeUnit.SECONDS.sleep(20);
            filesIOHandler.createEverSyncBucketIfNotExists();
        } catch (InvalidKeyException | ErrorResponseException | InsufficientDataException | InternalException
                | InvalidResponseException | NoSuchAlgorithmException | ServerException | XmlParserException
                | IOException | InterruptedException e) {
            log.error("Mandatory Bucket neither exists nor could be created.", e);
            exitApplicationForRetry(refreshedEvent.getApplicationContext());
        }
    }

    private void exitApplicationForRetry(ApplicationContext applicationContext) {
        SpringApplication.exit(applicationContext, () -> 1);
    }

    @Scheduled(fixedRate = 10, timeUnit = TimeUnit.MINUTES, initialDelay = 2)
    public void seekNextEvent() {
        log.info("Seek Event triggered");
        s3StagedEventsDao.handleS3Event().stream().forEach(new Consumer<S3StagedEvents>() {
            @Override
            public void accept(S3StagedEvents event) {
                log.info("Pulling file from MinIO : ", event.getBucketAndFileName());
                filesIOHandler.processS3File(event.getBucketAndFileName());
            }
        });
    }
}
