package com.eversync.integration.s3filehandler.data;

import java.sql.Date;

public class S3StagedEvents {
    
    private String bucketAndFileName;
    private Date eventInsertTime;
    private char eventState;
    
    public String getBucketAndFileName() {
        return bucketAndFileName;
    }
    public void setBucketAndFileName(String bucketAndFileName) {
        this.bucketAndFileName = bucketAndFileName;
    }
    public Date getEventInsertTime() {
        return eventInsertTime;
    }
    public void setEventInsertTime(Date eventInsertTime) {
        this.eventInsertTime = eventInsertTime;
    }
    public char getEventState() {
        return eventState;
    }
    public void setEventState(char eventState) {
        this.eventState = eventState;
    }

    
}
