package com.healthcare.analytics.service;

import com.healthcare.analytics.model.UserActivity;
import com.healthcare.analytics.model.AnalyticsView;
import com.healthcare.analytics.repository.UserActivityRepository;
import com.healthcare.analytics.repository.AnalyticsViewRepository;
import com.healthcare.analytics.dto.AnalyticsRequest;
import com.healthcare.analytics.dto.AnalyticsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    @Autowired
    private UserActivityRepository userActivityRepository;

    @Autowired
    private AnalyticsViewRepository analyticsViewRepository;

    @Autowired
    private EventProcessor eventProcessor;

    @Autowired
    private ReportGenerator reportGenerator;

    public void recordUserActivity(UserActivity activity) {
        userActivityRepository.save(activity);
        eventProcessor.processActivity(activity);
    }

    public List<AnalyticsResponse> getAnalyticsData(AnalyticsRequest request) {
        List<UserActivity> activities = userActivityRepository.findByUserIdAndActivityTimeBetween(
                request.getUserId(),
                request.getStartDate(),
                request.getEndDate());

        return activities.stream()
                .map(activity -> new AnalyticsResponse( //test
                        new Long(1),
                        new BigDecimal("1"),
                        new HashMap<>(),
                        new HashMap<>()))
                .collect(Collectors.toList());
    }

    public void incrementViewCount(String viewId, String viewType) {
        //analyticsViewRepository.incrementViewCount(viewId, new Date(), viewType);
    }

    public String generateReport(String userId, LocalDate startDate, LocalDate endDate) {
        return reportGenerator.generateUserActivityReport(userId, startDate, endDate);
    }
}