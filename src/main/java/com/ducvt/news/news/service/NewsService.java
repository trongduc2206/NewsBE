package com.ducvt.news.news.service;

import com.ducvt.news.news.models.dto.NewsDto;
import com.ducvt.news.news.models.dto.NewsPageDto;
import com.ducvt.news.news.payload.request.GetNewsByTopicExceptRequest;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public interface NewsService {
    NewsPageDto findByTopic(String topicKey, int offset, int page);

//    NewsPageDto findByParentTopic(String parentKey, int offset, int page);

    NewsDto findById(Long id);

    NewsPageDto search(String query, int offset, int page);

    NewsPageDto findSavedNewsByUserId(Long userId, int offset, int page);

    void saveNewsByUser(Long userId, Long newsId);

    Date checkIsSavedByUser(Long userId, Long newsId);

    void softDeleteSavedNews(Long userId, Long newsId);

    List<NewsDto> recommend(Long userId);

    List<NewsDto> findByTopicExcept(GetNewsByTopicExceptRequest request);

    List<NewsDto> findTop3SameTopicTitles(String topicKey, Long newsId);

    List<NewsDto> findRelevantNews(Long newsId);
}
