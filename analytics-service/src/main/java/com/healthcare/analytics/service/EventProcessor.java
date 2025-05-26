package com.healthcare.analytics.service;

import com.healthcare.analytics.model.UserActivity;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.Serializable;

@Service
public class EventProcessor implements Serializable {

    @Autowired
    private transient SparkSession sparkSession;

    @Autowired
    private transient AnalyticsService analyticsService;

    @PostConstruct
    public void init() {
        // Initialize any required Spark structures
    }

    public void processActivity(UserActivity activity) {
        // Process the activity in real-time
        if ("VIEW".equals(activity.getActionType())) {
            analyticsService.incrementViewCount(activity.getKey().getUserId().toString(), "USER_VIEW");
        }

        // You could also batch process activities using Spark
        // batchProcessActivities();
    }

    private void batchProcessActivities() {
        // Example of batch processing with Spark
        Dataset<Row> activityDF = sparkSession.read()
                .format("org.apache.spark.sql.cassandra")
                .options(getCassandraOptions("healthcare_analytics", "user_activity"))
                .load();

        // Perform some analytics
        activityDF.createOrReplaceTempView("activities");
        Dataset<Row> results = sparkSession.sql(
                "SELECT user_id, activity_type, COUNT(*) as count " +
                        "FROM activities " +
                        "GROUP BY user_id, activity_type");

        results.show();
    }

    private java.util.Map<String, String> getCassandraOptions(String keyspace, String table) {
        java.util.Map<String, String> options = new java.util.HashMap<>();
        options.put("keyspace", keyspace);
        options.put("table", table);
        return options;
    }
}