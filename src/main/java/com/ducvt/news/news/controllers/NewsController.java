package com.ducvt.news.news.controllers;

import com.ducvt.news.fw.utils.ResponseFactory;
import com.ducvt.news.news.models.dto.NewsDto;
import com.ducvt.news.news.models.dto.NewsPageDto;
import com.ducvt.news.news.payload.request.GetNewsByTopicExceptRequest;
import com.ducvt.news.news.payload.request.SaveNewsRequest;
import com.ducvt.news.news.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/news")
public class NewsController {
    @Autowired
    private NewsService newsService;

    @GetMapping
    public ResponseEntity findByTopic(@RequestParam String topicKey, @RequestParam int page, @RequestParam int offset) {
        NewsPageDto newsList = newsService.findByTopic(topicKey, offset, page);
        return ResponseFactory.success(newsList);
    }

    @GetMapping(value = "/topic-by-user")
    public ResponseEntity findByTopicByUser(@RequestParam String topicKey, @RequestParam int page, @RequestParam int offset, @RequestParam Long userId) {
        NewsPageDto newsList = newsService.findByTopicByUser(topicKey, offset, page, userId);
        return ResponseFactory.success(newsList);
    }

//    @GetMapping(value = "/parentTopic")
//    public ResponseEntity findByParentTopic(@RequestParam String key, @RequestParam int page, @RequestParam int offset) {
//        NewsPageDto newsList = newsService.findByParentTopic(key, offset, page);
//        return ResponseFactory.success(newsList);
//    }

    @GetMapping(value = "/{id}")
    public ResponseEntity findById(@PathVariable Long id) {
        NewsDto news = newsService.findById(id);
        return ResponseFactory.success(news);
    }

    @GetMapping(value = "/search")
    public ResponseEntity search(@RequestParam String query, @RequestParam int page, @RequestParam int offset){
        NewsPageDto newsPageDto = newsService.search(query, offset, page);
        return ResponseFactory.success(newsPageDto);
    }

    @GetMapping(value = "/search-by-user")
    public ResponseEntity searchByUser(@RequestParam String query, @RequestParam int page, @RequestParam int offset, @RequestParam Long userId){
        NewsPageDto newsPageDto = newsService.searchByUser(query, offset, page, userId);
        return ResponseFactory.success(newsPageDto);
    }

    @GetMapping(value = "/save/{userId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity getSaveNews(@PathVariable Long userId, @RequestParam int page, @RequestParam int offset){
        NewsPageDto newsPageDto = newsService.findSavedNewsByUserId(userId, offset, page);
        return ResponseFactory.success(newsPageDto);
    }

    @GetMapping(value = "/like/{userId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity getLikedNews(@PathVariable Long userId, @RequestParam int page, @RequestParam int offset) {
        NewsPageDto newsPageDto = newsService.findLikedNewsByUserId(userId, offset, page);
        return ResponseFactory.success(newsPageDto);
    }

    @PostMapping(value = "/save")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity saveNewsByUser(@RequestBody SaveNewsRequest saveNewsRequest) {
        newsService.saveNewsByUser(saveNewsRequest.getUserId(), saveNewsRequest.getNewsId());
        return ResponseFactory.success();
    }

    @GetMapping(value = "check-save")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity checkNewsSavedByUser(@RequestParam Long userId, @RequestParam Long newsId) {
        Date rs = newsService.checkIsSavedByUser(userId, newsId);
        return ResponseFactory.success(rs);
    }

    @GetMapping(value = "recommend/{userId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity recommendNews(@PathVariable Long userId) {
        List<NewsDto> newsDtoList = newsService.recommend(userId);
        return ResponseFactory.success(newsDtoList);
    }

    @GetMapping(value = "get-recommend/{userId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity getRecommendNews(@PathVariable Long userId){
        List<NewsDto> newsDtoList = newsService.getRecommendNews(userId);
        return ResponseFactory.success(newsDtoList);
    }

    @GetMapping(value = "save-recommend/{userId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity saveRecommendNews(@PathVariable Long userId) {
        newsService.saveRecommendNews(userId);
        return ResponseFactory.success();
    }

    @PutMapping(value = "/save/soft-delete")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity softDeleteSavedNews(@RequestBody SaveNewsRequest saveNewsRequest) {
        newsService.softDeleteSavedNews(saveNewsRequest.getUserId(), saveNewsRequest.getNewsId());
        return ResponseFactory.success();
    }

    @PutMapping(value = "/topic/except")
    public ResponseEntity getNewsTopicExcept (@RequestBody GetNewsByTopicExceptRequest request) {
        List<NewsDto> newsDtos = newsService.findByTopicExcept(request);
        return ResponseFactory.success(newsDtos);
    }

    @PutMapping(value = "/topic/except/{userId}")
    public ResponseEntity getNewsTopicExcept (@RequestBody GetNewsByTopicExceptRequest request, @PathVariable Long userId) {
        List<NewsDto> newsDtos = newsService.findByTopicExceptByUser(request, userId);
        return ResponseFactory.success(newsDtos);
    }

    @GetMapping(value = "/same-topic")
    public ResponseEntity getTitlesSameTopic(@RequestParam String topicKey, @RequestParam Long newsId) {
        List<NewsDto> newsDtos = newsService.findTop3SameTopicTitles(topicKey, newsId);
        return ResponseFactory.success(newsDtos);
    }

    @GetMapping(value = "/relevant/{newsId}")
    public ResponseEntity getRelevantNews(@PathVariable Long newsId) {
        List<NewsDto> newsDtos = newsService.findRelevantNews(newsId);
        return ResponseFactory.success(newsDtos);
    }

}
