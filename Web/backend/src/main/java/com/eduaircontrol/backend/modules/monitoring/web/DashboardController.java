package com.eduaircontrol.backend.modules.monitoring.web;

import com.eduaircontrol.backend.modules.monitoring.application.DashboardSeriesService;
import com.eduaircontrol.backend.modules.monitoring.application.DashboardService;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final DashboardSeriesService dashboardSeriesService;

    @GetMapping("/summary")
    public Map<String, Object> summary() {
        DashboardService.DashboardSummary summary = dashboardService.summary();
        return Map.of(
                "counts", summary.counts(),
                "averages", summary.averages(),
                "lastUpdated", summary.lastUpdated() != null ? summary.lastUpdated() : "");
    }

    @GetMapping("/series")
    public List<DashboardSeriesService.SeriesPoint> series(
            @RequestParam(name = "period", defaultValue = "day") String period,
            @RequestParam(name = "variable", defaultValue = "temperature") String variable,
            @RequestParam(name = "environmentId", required = false) UUID environmentId) {
        return dashboardSeriesService.series(period, variable, environmentId);
    }
}
