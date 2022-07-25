package com.ducvt.news.source.service;

import com.ducvt.news.fw.constant.MessageEnum;
import com.ducvt.news.fw.exceptions.BusinessLogicException;
import com.ducvt.news.news.client.DataAnalystClient;
import com.ducvt.news.news.models.Topic;
import com.ducvt.news.news.repository.TopicRepository;
import com.ducvt.news.news.service.TopicService;
import com.ducvt.news.source.models.Source;
import com.ducvt.news.source.models.SourceCrawl;
import com.ducvt.news.source.models.TaskDefinition;
import com.ducvt.news.source.repository.SourceCrawlRepository;
import com.ducvt.news.source.repository.SourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ScheduledFuture;

@Component
public class ScheduledTask {
    private static final Logger logger = LoggerFactory.getLogger(ScheduledTask.class);
    @Autowired
    private DataAnalystClient dataAnalystClient;

    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    @Autowired
    private SourceCrawlRepository sourceCrawlRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private TopicService topicService;

    @Autowired
    private AutowireCapableBeanFactory beanFactory;

    @Autowired
    private SourceRepository sourceRepository;

//    private static final String cronTest = "";

    Map<Long, ScheduledFuture<?>> jobsMap = new HashMap<>();

    @Scheduled(cron = "0/40 * * ? * *")
    public void scheduleTaskWithFixedRate() {
        for(Map.Entry<Long, ScheduledFuture<?>> entry : jobsMap.entrySet()) {
            ScheduledFuture<?> scheduledTask = entry.getValue();
            if(scheduledTask != null) {
                scheduledTask.cancel(true);
//                jobsMap.remove(entry.getKey());
            } else {
//                jobsMap.remove(entry.getKey());
            }
        }
        jobsMap.clear();
        List<SourceCrawl> sourceCrawlList = sourceCrawlRepository.findAllByStatus(1);
        if(sourceCrawlList != null && sourceCrawlList.size() >  0) {
            // call send email method here
            for(SourceCrawl sourceCrawl : sourceCrawlList) {
                Source source = sourceRepository.findById(sourceCrawl.getSourceId()).get();
                if(source.getStatus() == 1 && !jobsMap.containsKey(sourceCrawl.getId())) {
                    Topic topic = topicRepository.findById(sourceCrawl.getTopicId()).get();
                    TaskDefinition taskDefinition = new TaskDefinition();
                    taskDefinition.setUrl(sourceCrawl.getCrawlUrl());
                    if (topic.getLevel() != 1) {
                        Topic topicLv1 = topicService.findLv1TopicByKey(topic.getTopicKey());
                        taskDefinition.setTopicLv1(topicLv1.getId());
                        if (topic.getLevel() == 2) {
                            taskDefinition.setTopicLv2(topic.getId());
                        } else {
                            Topic topicLv2 = topicRepository.findByTopicKeyAndStatus(topic.getParentKey(), 1).get();
                            taskDefinition.setTopicLv2(topicLv2.getId());
                            taskDefinition.setTopicLv3(topic.getId());
                        }
                    } else {
                        taskDefinition.setTopicLv1(topic.getId());
                    }
                    String crawlTime = sourceCrawl.getCrawlTime();
                    String[] times = crawlTime.split(":");
                    if (times == null || times.length == 0) {
//                    sourceRepository.delete(source);
                        throw new BusinessLogicException(MessageEnum.PROCESS_CRAWL_TIME.getMessage());
                    }
                    String cronExpression = "0 " + times[1] + " " + times[0] + " * * ?";
                    taskDefinition.setCronExpression(cronExpression);
                    TaskDefinitionBean taskDefinitionBean = new TaskDefinitionBean();
                    beanFactory.autowireBean(taskDefinitionBean);
                    taskDefinitionBean.setTaskDefinition(taskDefinition);
                    ScheduledFuture<?> scheduledTask = threadPoolTaskScheduler.schedule(taskDefinitionBean, new CronTrigger(cronExpression, TimeZone.getTimeZone(TimeZone.getDefault().getID())));
                    jobsMap.put(sourceCrawl.getId(), scheduledTask);
                }
            }
            logger.info("Scan scheduled job at {} found {} job", new Date(), jobsMap.size());
        } else {
            logger.info("Scan scheduled job at {} found {} job", new Date(), 0);
        }
        for(Map.Entry<Long, ScheduledFuture<?>> entry : jobsMap.entrySet()) {
            logger.info("Task {} is running", entry.getKey());
        }
    }

}
