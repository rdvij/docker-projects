package com.eversync.integration.s3filehandler.data;

import java.util.List;

public interface S3StagedEventsDao {
    
    public List<S3StagedEvents> handleS3Event();
}
