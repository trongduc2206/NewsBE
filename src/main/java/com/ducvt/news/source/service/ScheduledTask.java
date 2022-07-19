package com.ducvt.news.source.service;

import com.ducvt.news.news.client.DataAnalystClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class ScheduledTask {
    private static final Logger logger = LoggerFactory.getLogger(ScheduledTask.class);
    @Autowired
    private DataAnalystClient dataAnalystClient;

//    @Scheduled(fixedRate = 5000)
//    public void scheduleTaskWithFixedRate() {
//        // call send email method here
//        String test = dataAnalystClient.test();
//        logger.info("Test scheduled with result {}", test);
//    }

}
