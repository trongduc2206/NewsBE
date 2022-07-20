package com.ducvt.news.source.service;

import com.ducvt.news.news.client.DataAnalystClient;
import com.ducvt.news.source.models.TaskDefinition;
import com.ducvt.news.source.payload.request.CrawlRequest;
import com.ducvt.news.source.payload.response.CrawlResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskDefinitionBean implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(TaskDefinitionBean.class);
    private TaskDefinition taskDefinition;

    @Autowired
    DataAnalystClient dataAnalystClient;

    public TaskDefinitionBean(){
    }

    @Override
    public void run() {
//        System.out.println("Running action: " + taskDefinition.getActionType());
//        System.out.println("With Data: " + taskDefinition.getData());
        logger.info("Start crawl job with url -> {}", taskDefinition.getUrl());
        CrawlRequest crawlRequest = new CrawlRequest();
        crawlRequest.setUrl(taskDefinition.getUrl());
        crawlRequest.setTopicLv1(taskDefinition.getTopicLv1());
        if(taskDefinition.getTopicLv2() != null) {
            crawlRequest.setTopicLv2(taskDefinition.getTopicLv2());
        }
        if(taskDefinition.getTopicLv3() != null) {
            crawlRequest.setTopicLv3(taskDefinition.getTopicLv3());
        }
        logger.info("crawl request to crawl -> {}", crawlRequest);
        try {
            CrawlResponse crawlResponse = dataAnalystClient.crawl(crawlRequest);
            if(crawlResponse.getData() != null) {
                logger.info("Success crawl with result -> {}", crawlResponse.getData());
            } else {
                logger.info("Fail crawl with error -> {}", crawlResponse.getError());
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

    }

    public TaskDefinition getTaskDefinition() {
        return taskDefinition;
    }

    public void setTaskDefinition(TaskDefinition taskDefinition) {
        this.taskDefinition = taskDefinition;
    }
}
