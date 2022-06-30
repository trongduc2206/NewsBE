package com.ducvt.news.news.controllers;

import com.ducvt.news.fw.utils.ResponseFactory;
import com.ducvt.news.news.models.Topic;
import com.ducvt.news.news.models.dto.TopicDto;
import com.ducvt.news.news.payload.request.ClickTopicRequest;
import com.ducvt.news.news.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/topic")
public class TopicController {
    @Autowired
    TopicService topicService;

    public TopicController(TopicService topicService) {
        this.topicService = topicService;
    }

    @GetMapping
    public ResponseEntity getNonChildrenTopic() {
        List<Topic> topics = topicService.findNonChildrenTopic();
        return ResponseFactory.success(topics);
    }

    @GetMapping(value = "/display")
    public ResponseEntity getTopicToDisplay() {
        List<TopicDto> topicDtoList = topicService.getListTopicToDisplay();
        return ResponseFactory.success(topicDtoList);
    }

    @GetMapping(value = "/display/{userId}")
    public ResponseEntity getTopicToDisplayUser(@PathVariable Long userId) {
        List<TopicDto> topicDtoList = topicService.getListTopicToDisplayUser(userId);
        return ResponseFactory.success(topicDtoList);
    }

    @GetMapping(value = "/{key}")
    public ResponseEntity getTopicByKey(@PathVariable String key) {
        Topic topic = topicService.findByTopicKey(key);
        return ResponseFactory.success(topic);
    }

    @GetMapping(value = "/parent/{key}")
    public ResponseEntity getTopicsByParentKey(@PathVariable String key) {
        List<TopicDto> topics = topicService.findByParentKey(key);
        return ResponseFactory.success(topics);
    }

    @PostMapping(value = "/click")
    public ResponseEntity saveTopicClick(@RequestBody ClickTopicRequest request) {
        topicService.saveTopicClick(request);
        return ResponseFactory.success();
    }

    @GetMapping(value = "/lv1/{topicKey}")
    public ResponseEntity getTopicLv1(@PathVariable String topicKey) {
        return ResponseFactory.success(topicService.findLv1TopicByKey(topicKey));
    }
}
