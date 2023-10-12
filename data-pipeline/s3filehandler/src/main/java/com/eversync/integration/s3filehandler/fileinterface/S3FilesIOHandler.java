package com.eversync.integration.s3filehandler.fileinterface;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;

public interface S3FilesIOHandler {

    public void processS3File(String bucketAndFileName);

    public boolean createEverSyncBucketIfNotExists()
            throws ErrorResponseException, InsufficientDataException, InternalException, InvalidKeyException,
            InvalidResponseException, IOException, NoSuchAlgorithmException, ServerException, XmlParserException;
}
