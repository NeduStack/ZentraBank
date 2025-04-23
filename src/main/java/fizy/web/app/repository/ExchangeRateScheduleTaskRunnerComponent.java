package fizy.web.app.repository;


import fizy.web.app.service.ExchangeRateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class ExchangeRateScheduleTaskRunnerComponent implements CommandLineRunner {
    private final Logger logger = LoggerFactory.getLogger(ExchangeRateScheduleTaskRunnerComponent.class);
    private final ExchangeRateService rateService;
    private final ScheduledExecutorService scheduler;

    public ExchangeRateScheduleTaskRunnerComponent(ExchangeRateService rateService, ScheduledExecutorService scheduler) {
        this.rateService = rateService;
        this.scheduler = scheduler;
    }


    @Override
    public void run(String... args) throws Exception {
        try {
            logger.info("Starting exchange rate schedule task...");

            scheduler.scheduleWithFixedDelay(() -> {
                try {
                    rateService.getExchangeRate();
                    logger.info("Exchange rates updated successfully.");
                } catch (Exception e) {
                    logger.error("Failed to update exchange rates", e);
                    // Consider adding alerting/metrics here
                }
            }, 0, 24, TimeUnit.HOURS);

        } catch (Exception e) {
            logger.error("Failed to schedule exchange rate task", e);
            // Additional error handling for scheduler failure
            throw e; // Re-throw if you want the failure to propagate
        }
    }
}
