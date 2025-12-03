package org.l5g7.mealcraft.app.statistics;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        DailyStatsTest.class,
        StatisticsServiceTest.class,
        StatisticsControllerTest.class,
        DailyStatsJobSchedulerTest.class
})
public class StatisticsTestSuite {
}

