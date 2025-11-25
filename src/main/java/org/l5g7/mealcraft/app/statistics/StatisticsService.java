package org.l5g7.mealcraft.app.statistics;

import org.l5g7.mealcraft.app.products.ProductRepository;
import org.l5g7.mealcraft.app.recipes.RecipeRepository;
import org.l5g7.mealcraft.app.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

@Service
public class StatisticsService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final RecipeRepository recipeRepository;
    private final DailyStatsRepository dailyStatsRepository;

    @Autowired
    public StatisticsService(UserRepository userRepository,
                             ProductRepository productRepository,
                             RecipeRepository recipeRepository,
                             DailyStatsRepository dailyStatsRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.recipeRepository = recipeRepository;
        this.dailyStatsRepository = dailyStatsRepository;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void recalcYesterdayStatsDaily() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        Date yesterday = cal.getTime();

        Date from = startOfDay(yesterday);
        Date to = endOfDay(yesterday);

        long newUsers = userRepository.countByCreatedAtBetween(from, to);
        long newProducts = productRepository.countByCreatedAtBetween(from, to);
        long newRecipes = recipeRepository.countByCreatedAtBetween(from, to);

        Date dayOnly = stripTime(yesterday);

        Optional<DailyStats> existing = dailyStatsRepository.findByDay(dayOnly);

        DailyStats stats;
        if (existing.isPresent()) {
            stats = existing.get();
        } else {
            stats = new DailyStats();
            stats.setDay(dayOnly);
        }

        stats.setNewUsersCount(newUsers);
        stats.setNewProductsCount(newProducts);
        stats.setNewRecipesCount(newRecipes);

        dailyStatsRepository.save(stats);
    }

    public DailyStats getYesterdayStats() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        Date dayOnly = stripTime(cal.getTime());

        Optional<DailyStats> existing = dailyStatsRepository.findByDay(dayOnly);
        if (existing.isPresent()) {
            return existing.get();
        } else {
            return null;
        }
    }

    private Date stripTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private Date startOfDay(Date date) {
        return stripTime(date);
    }

    private Date endOfDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(stripTime(date));
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }
}
