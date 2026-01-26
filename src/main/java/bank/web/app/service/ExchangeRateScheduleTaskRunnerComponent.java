package bank.web.app.service;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ExchangeRateScheduleTaskRunnerComponent implements CommandLineRunner {

    private final Logger logger = LoggerFactory.getLogger(ExchangeRateScheduleTaskRunnerComponent.class);
    private final ExchangeRateService rateService;
    private final ScheduledExecutorService scheduler;

    public ExchangeRateScheduleTaskRunnerComponent(ExchangeRateService rateService,
            ScheduledExecutorService scheduler) {
        this.rateService = rateService;
        this.scheduler = scheduler;
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("Calling currency API endpoint to fetch exchange rates...");
        scheduler.scheduleWithFixedDelay(rateService::getExchangeRates, 0, 12, TimeUnit.HOURS);
        logger.info("Ended Calling currency API endpoint");
    }
}
