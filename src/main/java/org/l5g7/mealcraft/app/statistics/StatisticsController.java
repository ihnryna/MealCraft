package org.l5g7.mealcraft.app.statistics;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/yesterday")
    public ResponseEntity<DailyStats> getYesterdayStats() {
        DailyStats stats = statisticsService.getYesterdayStats();
        return ResponseEntity.ok(stats);
    }
}
