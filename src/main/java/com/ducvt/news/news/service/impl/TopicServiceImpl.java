package com.ducvt.news.news.service.impl;

import com.ducvt.news.fw.constant.MessageConstant;
import com.ducvt.news.fw.constant.MessageEnum;
import com.ducvt.news.fw.exceptions.BusinessLogicException;
import com.ducvt.news.news.models.ClickTopic;
import com.ducvt.news.news.models.InteractNews;
import com.ducvt.news.news.models.News;
import com.ducvt.news.news.models.Topic;
import com.ducvt.news.news.models.dto.TopicDto;
import com.ducvt.news.news.models.enums.InteractType;
import com.ducvt.news.news.payload.request.ClickTopicRequest;
import com.ducvt.news.news.repository.ClickTopicRepository;
import com.ducvt.news.news.repository.InteractNewsRepository;
import com.ducvt.news.news.repository.NewsRepository;
import com.ducvt.news.news.repository.TopicRepository;
import com.ducvt.news.news.service.TopicService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TopicServiceImpl implements TopicService {
    private static Logger logger = LoggerFactory.getLogger(TopicServiceImpl.class);
    @Autowired
    TopicRepository topicRepository;

    @Autowired
    ClickTopicRepository clickTopicRepository;

    @Autowired
    InteractNewsRepository interactNewsRepository;

    @Autowired
    NewsRepository newsRepository;

    public TopicServiceImpl(TopicRepository topicRepository) {
        topicRepository = topicRepository;
    }

    @Override
    public List<Topic> findNonChildrenTopic() {
        return topicRepository.findByParentKeyNullAndStatus(MessageConstant.ACTIVE_STATUS);
    }

    @Override
    public List<Topic> findNonChildrenTopicSorted(Long userId) {
        List<Topic> topics = topicRepository.findByParentKeyNullAndStatus(1);
        List<InteractNews> interactNews = interactNewsRepository.findTop5ByUserIdAndTypeAndStatusOrderByCreateTimeDesc(userId, InteractType.READ, 1);
        if(interactNews != null && interactNews.size() > 0) {
            Collections.reverse(interactNews);
            for(InteractNews interactNew : interactNews) {
                News news = newsRepository.findByIdAndStatus(interactNew.getNewsId(), 1).get();
                if(topics.get(0).getId() != news.getTopicLv1().getId()) {
                    topics.remove(news.getTopicLv1());
                    topics.add(0, news.getTopicLv1());
                }
            }
        }
        return topics;
    }

    @Override
    public Topic findByTopicKey(String topicKey) {
        Optional<Topic> topicOptional = topicRepository.findByTopicKeyAndStatus(topicKey, MessageConstant.ACTIVE_STATUS);
        if (topicOptional.isPresent()) {
            return topicOptional.get();
        } else {
            throw new BusinessLogicException(MessageEnum.NOT_FOUND_TOPIC_BY_KEY.getMessage());
        }
    }

    @Override
    public List<Topic> findTopicBreadcrumb(String topicKey) {
        return null;
    }

    @Override
    public List<TopicDto> findByParentKey(String parentKey) {
        Optional<List<Topic>> optionalTopics = topicRepository.findByParentKeyAndStatus(parentKey, MessageConstant.ACTIVE_STATUS);
        if (optionalTopics != null && optionalTopics.isPresent()) {
            if (optionalTopics.get().size() > 0) {
                return mapListTopicToListDto(optionalTopics.get());
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public List<TopicDto> getListTopicToDisplay() {
        List<Topic> parentTopic = topicRepository.findByParentKeyNullAndStatus(MessageConstant.ACTIVE_STATUS);

        List<TopicDto> topicDtoList = new ArrayList<>();
        for (Topic topic : parentTopic) {
            TopicDto topicDto = mapTopicToDto(topic);
            Optional<List<Topic>> childrenTopicList = topicRepository.findByParentKeyAndStatus(topic.getTopicKey(), MessageConstant.ACTIVE_STATUS);
            if (childrenTopicList != null && childrenTopicList.isPresent()) {
                List<TopicDto> childrenTopicDtoList = mapListTopicToListDto(childrenTopicList.get());
                topicDto.setChildren(childrenTopicDtoList);
            }
            topicDtoList.add(topicDto);
        }
        return topicDtoList;
    }

    @Override
    public List<TopicDto> getListTopicToDisplayUser(Long userId) {
        //get most clicked child topic
        List<ClickTopic> mostClickTopic = clickTopicRepository.find3MostClickedTopic(userId);

        List<Integer> mostClickTopicId = new ArrayList<>();
        List<TopicDto> topicToDisplay = new ArrayList<>();
        if (mostClickTopic != null && mostClickTopic.size() > 0) {
            for (ClickTopic clickTopic : mostClickTopic) {
                Topic topic = topicRepository.findById(clickTopic.getTopicId()).get();
//                if (topic.getParentKey() == null) {
//                    mostClickTopicId.add(topic.getId());
//                } else {
//                    Topic parentTopic = topicRepository.findByTopicKeyAndStatus(topic.getParentKey(), 1).get();
//                    mostClickTopicId.add(parentTopic.getId());
//                }
                TopicDto topicDto = mapTopicToDto(topic);
                topicDto.setRecommended(true);
                topicToDisplay.add(topicDto);
            }
        }

        // get default parent topic
        List<Topic> parentTopic = topicRepository.findByParentKeyNullAndStatus(MessageConstant.ACTIVE_STATUS);
        for (Topic topic : parentTopic) {
//            if (mostClickTopicId.size() > 0) {
//                if (!mostClickTopicId.contains(topic.getId())) {
//                 topicToDisplay.add(mapTopicToDto(topic));
//                }
//            } else {
//                topicToDisplay.add(mapTopicToDto(topic));
//            }
            topicToDisplay.add(mapTopicToDto(topic));
        }
        return topicToDisplay;
    }


    @Override
    public void saveTopicClick(ClickTopicRequest request) {
        ClickTopic clickTopic = new ClickTopic();
        clickTopic.setUserId(request.getUserId());

        Topic topic = topicRepository.findByTopicKeyAndStatus(request.getTopicKey(), 1).get();
        clickTopic.setTopicId(topic.getId());
        clickTopic.setClickTime(new Date());
        clickTopic.setCreateTime(new Date());
        clickTopic.setUpdateTime(new Date());
        clickTopic.setStatus(MessageConstant.ACTIVE_STATUS);
        clickTopicRepository.save(clickTopic);
        logger.info("saved topic click with user -> {} and topic -> {}", clickTopic.getUserId(), clickTopic.getTopicId());
    }

    @Override
    public Topic findLv1TopicByKey(String topicKey) {
        Topic topic = topicRepository.findByTopicKeyAndStatus(topicKey, 1).get();
        if(topic.getLevel() == 1) {
            return topic;
        } else if(topic.getLevel() == 2) {
            return topicRepository.findByTopicKeyAndStatus(topic.getParentKey(), 1).get();
        } else {
            Topic topicLv2 = topicRepository.findByTopicKeyAndStatus(topic.getParentKey(), 1).get();
            return topicRepository.findByTopicKeyAndStatus(topicLv2.getParentKey(), 1).get();
        }
    }



    private TopicDto mapTopicToDto(Topic topic) {
        TopicDto topicDto = new TopicDto();
        topicDto.setLabel(topic.getName());
        topicDto.setKey(topic.getTopicKey());
        Optional<List<Topic>> childrenTopicList = topicRepository.findByParentKeyAndStatus(topic.getTopicKey(), MessageConstant.ACTIVE_STATUS);
        if (childrenTopicList != null && childrenTopicList.isPresent()) {
            List<TopicDto> childrenTopicDtoList = mapListTopicToListDto(childrenTopicList.get());
            topicDto.setChildren(childrenTopicDtoList);
        }
        return topicDto;
    }

    private List<TopicDto> mapListTopicToListDto(List<Topic> topics) {
        List<TopicDto> topicDtoList = new ArrayList<>();
        for (Topic topic : topics) {
            topicDtoList.add(mapTopicToDto(topic));
        }
        return topicDtoList;
    }


}
