package com.eversync.integration.s3filehandler.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class S3StagedEventsDaoimpl implements S3StagedEventsDao {

    @Autowired
    JdbcTemplate jdbcTemplate;

    private static final String DEQUE_EVENT_QUERY = "DELETE\r\n" + //
            "FROM s3stagedevents events\r\n" + //
            "WHERE events.bucketfilename =\r\n" + //
            "      (SELECT s3stagedevents_inner.bucketfilename\r\n" + //
            "       FROM s3stagedevents s3stagedevents_inner\r\n" + //
            "       ORDER BY s3stagedevents_inner.event_inserted_time ASC\r\n" + //
            "           FOR UPDATE SKIP LOCKED\r\n" + //
            "       LIMIT 1)\r\n" + //
            "RETURNING events.bucketfilename, events.event_inserted_time, events.state;";

    @Override
    public List<S3StagedEvents> handleS3Event() {

       return jdbcTemplate.query(DEQUE_EVENT_QUERY, new RowMapper<S3StagedEvents>() {
            @Override
            @Nullable
            public S3StagedEvents mapRow(ResultSet resultSet, int arg1) throws SQLException {
                S3StagedEvents s3StagedEvent = new S3StagedEvents();
                s3StagedEvent.setBucketAndFileName(resultSet.getString(1));
                log.info("reading file : " + resultSet.getString(1));
                return s3StagedEvent;
            }
        });
    }

}
