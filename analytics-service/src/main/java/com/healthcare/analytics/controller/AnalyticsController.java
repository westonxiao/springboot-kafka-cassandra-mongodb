package com.healthcare.analytics.controller;

import com.healthcare.analytics.dto.AnalyticsRequest;
import com.healthcare.analytics.dto.AnalyticsResponse;
import com.healthcare.analytics.model.UserActivity;
import com.healthcare.analytics.service.AnalyticsService;
import com.healthcare.analytics.service.ReportGenerator;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final ReportGenerator reportGenerator;

    public AnalyticsController(AnalyticsService analyticsService, ReportGenerator reportGenerator) {
        this.analyticsService = analyticsService;
        this.reportGenerator = reportGenerator;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AnalyticsResponse>> getUserAnalytics(
            @PathVariable String userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        // Create request object using setter methods
        AnalyticsRequest request = new AnalyticsRequest();
        request.setUserId(userId);
        request.setStartDate(startDate);
        request.setEndDate(endDate);

        List<AnalyticsResponse> response = analyticsService.getAnalyticsData(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}/report")
    public ResponseEntity<String> getUserActivityReport(
            @PathVariable String userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        String report = reportGenerator.generateUserActivityReport(userId, startDate, endDate);
        return ResponseEntity.ok()
                .header("Content-Type", "text/html")
                .body(report);
    }

    @PostMapping("/activity")
    public ResponseEntity<Void> recordUserActivity(@RequestBody UserActivity activity) {
        analyticsService.recordUserActivity(activity);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/view/{viewId}")
    public ResponseEntity<Void> incrementViewCount(
            @PathVariable String viewId,
            @RequestParam String viewType) {

        analyticsService.incrementViewCount(viewId, viewType);
        return ResponseEntity.ok().build();
    }
}