package com.ducvt.news.news.service;

import com.ducvt.news.news.models.Topic;
import com.ducvt.news.news.models.dto.TopicDto;
import com.ducvt.news.news.payload.request.ClickTopicRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface TopicService {
    List<Topic> findNonChildrenTopic();

    List<TopicDto> getListTopicToDisplay();

    List<TopicDto> getListTopicToDisplayUser(Long userId);

    List<TopicDto> findByParentKey(String parentKey);

    Topic findByTopicKey(String topicKey);

    List<Topic> findTopicBreadcrumb(String topicKey);

    void saveTopicClick(ClickTopicRequest request);

    Topic findLv1TopicByKey(String topicKey);
}
