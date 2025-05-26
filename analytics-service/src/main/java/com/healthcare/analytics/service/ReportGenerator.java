package com.healthcare.analytics.service;

import com.healthcare.analytics.model.UserActivity;
import com.healthcare.analytics.repository.UserActivityRepository;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ReportGenerator {

    @Autowired
    private SparkSession sparkSession;

    @Autowired
    private UserActivityRepository userActivityRepository;

    public String generateUserActivityReport(String userId, LocalDate startDate, LocalDate endDate) {
        // Using Spark for more complex analytics/report generation
        Dataset<Row> activityDF = sparkSession.read()
                .format("org.apache.spark.sql.cassandra")
                .option("keyspace", "healthcare_analytics")
                .option("table", "user_activity")
                .load();

        activityDF.createOrReplaceTempView("user_activities");

        String query = String.format(
                "SELECT activity_type, COUNT(*) as count " +
                        "FROM user_activities " +
                        "WHERE user_id = '%s' AND activity_time >= '%s' AND activity_time <= '%s' " +
                        "GROUP BY activity_type",
                userId, startDate, endDate);

        Dataset<Row> results = sparkSession.sql(query);

        // Convert results to HTML report
        StringBuilder report = new StringBuilder();
        report.append("<html><body>");
        report.append("<h1>User Activity Report</h1>");
        report.append("<p>User ID: ").append(userId).append("</p>");
        report.append("<p>Period: ").append(startDate).append(" to ").append(endDate).append("</p>");
        report.append("<table border='1'><tr><th>Activity Type</th><th>Count</th></tr>");

        for (Row row : results.collectAsList()) {
            report.append("<tr>")
                    .append("<td>").append(row.getString(0)).append("</td>")
                    .append("<td>").append(row.getLong(1)).append("</td>")
                    .append("</tr>");
        }

        report.append("</table></body></html>");
        return report.toString();
    }
}