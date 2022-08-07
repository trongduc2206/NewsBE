package com.ducvt.news.source.service;

import com.ducvt.news.account.models.ERole;
import com.ducvt.news.account.models.Role;
import com.ducvt.news.account.models.User;
import com.ducvt.news.account.repository.RoleRepository;
import com.ducvt.news.account.repository.UserRepository;
import com.ducvt.news.news.client.DataAnalystClient;
import com.ducvt.news.news.service.NewsService;
import com.ducvt.news.source.models.TaskDefinition;
import com.ducvt.news.source.payload.request.CrawlRequest;
import com.ducvt.news.source.payload.response.CrawlInformation;
import com.ducvt.news.source.payload.response.CrawlResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskDefinitionBean implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(TaskDefinitionBean.class);
    private TaskDefinition taskDefinition;

    @Autowired
    DataAnalystClient dataAnalystClient;

    @Autowired
    NewsService newsService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    public TaskDefinitionBean(){
    }

    @Override
    @Async
    public void run() {
//        System.out.println("Running action: " + taskDefinition.getActionType());
//        System.out.println("With Data: " + taskDefinition.getData());
        logger.info("Start crawl job with url -> {} with thread -> {}", taskDefinition.getUrl(), Thread.currentThread().getId());
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
                CrawlInformation crawlInformation = crawlResponse.getData();
                if(crawlInformation.getInserted() > 0) {
                    List<User> userList = userRepository.findAllByStatus(1);
                    if(userList != null && userList.size() > 0) {
                        logger.info("Calculate recommend news for all active users");
                        Role role = roleRepository.findByName(ERole.ROLE_USER).get();
                        for(User user : userList) {
                            Boolean isUser = false;
                            for(Role roleCheck : user.getRoles()) {
                                if(roleCheck.getName().equals(ERole.ROLE_USER)) {
                                    isUser = true;
                                }
                            }
                            if(isUser) {
                                newsService.saveRecommendNews(user.getId());
                            }
                        }
                    }
                }
            } else {
                logger.info("Fail crawl with error -> {}", crawlResponse.getError());
            }
        } catch (Exception e) {
            logger.error("Exception in crawl process " + e.getMessage());
        }

    }

    public TaskDefinition getTaskDefinition() {
        return taskDefinition;
    }

    public void setTaskDefinition(TaskDefinition taskDefinition) {
        this.taskDefinition = taskDefinition;
    }
}
