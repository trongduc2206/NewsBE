package com.ducvt.news.news.service.impl;

import com.ducvt.news.fw.constant.MessageConstant;
import com.ducvt.news.fw.constant.MessageEnum;
import com.ducvt.news.fw.exceptions.BusinessLogicException;
import com.ducvt.news.news.client.DataAnalystClient;
import com.ducvt.news.news.models.*;
import com.ducvt.news.news.models.dto.CommentDto;
import com.ducvt.news.news.models.dto.NewsDto;
import com.ducvt.news.news.models.dto.NewsPageDto;
import com.ducvt.news.news.models.enums.InteractType;
import com.ducvt.news.news.payload.request.GetNewsByTopicExceptRequest;
import com.ducvt.news.news.payload.request.RecommendRequest;
import com.ducvt.news.news.payload.response.RecommendResponse;
import com.ducvt.news.news.repository.*;
import com.ducvt.news.news.service.NewsService;
import com.ducvt.news.source.service.TaskDefinitionBean;
import com.ducvt.news.source.service.TaskSchedulingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class NewsServiceImpl implements NewsService {
    private static Logger logger = LoggerFactory.getLogger(NewsServiceImpl.class);
    @Autowired
    private TopicRepository topicRepository;
    @Autowired
    private NewsRepository newsRepository;
    @Autowired
    private SaveNewsRepository saveNewsRepository;
    @Autowired
    private InteractNewsRepository interactNewsRepository;
    @Autowired
    private DataAnalystClient dataAnalystClient;
    @Autowired
    private RecommendNewsRepository recommendNewsRepository;
    @Override
    public NewsPageDto findByTopic(String topicKey, int offset, int page) {
        Optional<Topic> topicOptional = topicRepository.findByTopicKeyAndStatus(topicKey, MessageConstant.ACTIVE_STATUS);
        if(topicOptional != null && topicOptional.isPresent()) {
            Topic topic = topicOptional.get();
            Pageable pageable = PageRequest.of(page, offset, Sort.by(Sort.Direction.DESC,"createTime"));
            Page<News> newsPage;
            if(topic.getLevel() == 1) {
                newsPage = newsRepository.findAllByTopicLv1AndStatus(topic, 1, pageable);
            } else if(topic.getLevel() ==2) {
                 newsPage = newsRepository.findAllByTopicLv2AndStatus(topic, 1, pageable);
            } else {
                newsPage = newsRepository.findAllByTopicLv3AndStatus(topic, 1, pageable);
            }
//            Pageable pageable = PageRequest.of(page, offset, Sort.by("createTime"));
//            Page<News> newsPage = newsRepository.findAllByTopicAndStatus(topic, MessageConstant.ACTIVE_STATUS, pageable);

            NewsPageDto newsPageDto = new NewsPageDto();
            List<NewsDto> newsDtos = mapListNewsToListNewsDto(newsPage.getContent());
            newsPageDto.setContent(newsDtos);
            newsPageDto.setTotalElements(newsPage.getTotalElements());

            return newsPageDto;
        } else {
            return null;
        }
    }

//    @Override
//    public NewsPageDto findByParentTopic(String parentKey, int offset, int page) {
//        Pageable pageable = PageRequest.of(page, offset, Sort.by("createTime"));
//        Page<News> newsPage = newsRepository.findAllByTopic_ParentKeyAndStatus(parentKey, MessageConstant.ACTIVE_STATUS , pageable);
//
//        NewsPageDto newsPageDto = new NewsPageDto();
//        List<NewsDto> newsDtos = mapListNewsToListNewsDto(newsPage.getContent());
//        newsPageDto.setContent(newsDtos);
//        newsPageDto.setTotalElements(newsPage.getTotalElements());
//
//        return newsPageDto;
//    }

    @Override
    public NewsPageDto search(String query, int offset, int page){
        Pageable pageable = PageRequest.of(page, offset, Sort.by(Sort.Direction.DESC,"createTime"));
//        Page<News> newsPage = newsRepository.findAllByTitleContainingIgnoreCaseAndStatusOrContentContainingIgnoreCaseAndStatus(query, MessageConstant.ACTIVE_STATUS, query, MessageConstant.ACTIVE_STATUS, pageable);
        Page<News> newsPage = newsRepository.findAllByTitleContainingIgnoreCaseAndStatusOrSummaryContainingIgnoreCaseAndStatus(query, MessageConstant.ACTIVE_STATUS, query, MessageConstant.ACTIVE_STATUS, pageable);

        NewsPageDto newsPageDto = new NewsPageDto();
        List<NewsDto> newsDtos = mapListNewsToListNewsDto(newsPage.getContent());
        newsPageDto.setContent(newsDtos);
        newsPageDto.setTotalElements(newsPage.getTotalElements());

        return newsPageDto;
    }

    @Override
    public NewsPageDto findSavedNewsByUserId(Long userId, int offset, int page) {
        Pageable pageable = PageRequest.of(page, offset, Sort.by(Sort.Direction.DESC,"createTime"));
        Page<SaveNews> saveNewsPage = saveNewsRepository.findAllByUserIdAndStatus(userId, MessageConstant.ACTIVE_STATUS, pageable);
        List<NewsDto> newsDtos = new ArrayList<>();
        for(SaveNews saveNews : saveNewsPage) {
            Optional<News> newsOptional = newsRepository.findByIdAndStatus(saveNews.getNewsId(), MessageConstant.ACTIVE_STATUS);
            if(newsOptional != null && newsOptional.isPresent()) {
                NewsDto newsDto = mapNewsToNewsDto(newsOptional.get());
                newsDto.setSavedTime(saveNews.getSaveTime());
                newsDtos.add(newsDto);
            }
        }
        NewsPageDto newsPageDto = new NewsPageDto();
        newsPageDto.setContent(newsDtos);
        newsPageDto.setTotalElements(saveNewsPage.getTotalElements());
        return newsPageDto;
    }

    @Override
    public NewsPageDto findLikedNewsByUserId(Long userId, int offset, int page) {
        List<Long> newsIds = interactNewsRepository.findInteractedNewsIdByUserAndStatusAndType(userId, 1, InteractType.LIKE);
        Pageable pageable = PageRequest.of(page, offset, Sort.by(Sort.Direction.DESC,"createTime"));
        Page<News> likedNewsPage = newsRepository.findByIdInAndStatus(newsIds, 1, pageable);
        List<NewsDto> newsDtos = mapListNewsToListNewsDto(likedNewsPage.getContent());

        NewsPageDto newsPageDto = new NewsPageDto();
        newsPageDto.setContent(newsDtos);
        newsPageDto.setTotalElements(likedNewsPage.getTotalElements());
        return newsPageDto;
    }

    @Override
    public void saveNewsByUser(Long userId, Long newsId) {
        SaveNewsId saveNewsId = new SaveNewsId();
        saveNewsId.setNewsId(newsId);
        saveNewsId.setUserId(userId);
        if(saveNewsRepository.existsById(saveNewsId)) {
            SaveNews saveNews = saveNewsRepository.getById(saveNewsId);
            if(saveNews.getStatus() == 0) {
                saveNews.setStatus(MessageConstant.ACTIVE_STATUS);
                saveNewsRepository.save(saveNews);
            } else {
                throw new BusinessLogicException(MessageEnum.NEWS_SAVED_ALREADY.getMessage());
            }
        } else {
            SaveNews saveNews = new SaveNews();
            saveNews.setNewsId(newsId);
            saveNews.setUserId(userId);
            saveNews.setSaveTime(new Date());
            saveNews.setStatus(MessageConstant.ACTIVE_STATUS);
            saveNews.setCreateTime(new Date());
            saveNews.setUpdateTime(new Date());
            saveNewsRepository.save(saveNews);
        }
    }

    @Override
    public Date checkIsSavedByUser(Long userId, Long newsId) {
        SaveNewsId saveNewsId = new SaveNewsId();
        saveNewsId.setNewsId(newsId);
        saveNewsId.setUserId(userId);
        if(saveNewsRepository.existsById(saveNewsId)) {
            SaveNews saveNews = saveNewsRepository.getById(saveNewsId);
            if(saveNews.getStatus() == MessageConstant.ACTIVE_STATUS) {
                return saveNews.getSaveTime();
            }
        }
        return null;
    }

    @Override
    public void softDeleteSavedNews(Long userId, Long newsId) {
        SaveNewsId saveNewsId = new SaveNewsId();
        saveNewsId.setNewsId(newsId);
        saveNewsId.setUserId(userId);
        SaveNews saveNews = saveNewsRepository.getById(saveNewsId);
        saveNews.setStatus(0);
        saveNewsRepository.save(saveNews);
    }

    @Override
    public List<NewsDto> recommend(Long userId) {
        List<String> contentList = new ArrayList<>();

        //get history news of user
        List<InteractNews> userRead = interactNewsRepository.findTop10ByUserIdAndTypeAndStatusOrderByCreateTimeDesc(userId, InteractType.READ, 1);
        List<Long> historyNewsIds = new ArrayList<>();
        if(userRead != null && userRead.size() > 0) {
            for(InteractNews interactNews : userRead) {
//                if(interactNews.getInteractTime().before(timeToQuery)) {
                    historyNewsIds.add(interactNews.getNewsId());
//                }
            }
        } else {
            return null;
        }
        //get liked news of user
        List<InteractNews> userLike = interactNewsRepository.findTop5ByUserIdAndTypeAndStatusOrderByCreateTimeDesc(userId, InteractType.LIKE, 1);
        //get shared news of user
        List<InteractNews> userShare = interactNewsRepository.findTop5ByUserIdAndTypeAndStatusOrderByCreateTimeDesc(userId, InteractType.SHARE, 1);

        //add to history list
        List<News> historyNews = new ArrayList<>();
        for(Long id : historyNewsIds) {
            News news = newsRepository.findByIdAndStatus(id,1).get();
            historyNews.add(news);
        }
        if(userLike != null && userLike.size() > 0) {
            for(InteractNews interactNews : userLike) {
                News news = newsRepository.findByIdAndStatus(interactNews.getNewsId(), 1).get();
                if(news != null) {
                    historyNews.add(news);
                }
            }
        }
        if(userShare != null && userShare.size() > 0) {
            for(InteractNews interactNews : userShare) {
                News news = newsRepository.findByIdAndStatus(interactNews.getNewsId(), 1).get();
                if(news != null) {
                    historyNews.add(news);
                }
            }
        }
        if(historyNews == null || historyNews.size() < 10) {
            logger.info("not enough history news to recommend for user ", userId);
            return null;
        }

        //get topics of history news
//        List<Topic> historyTopics = new ArrayList<>();
//        for(News news : historyNews) {
//            historyTopics.add(news.getTopicLv1());
//        }

        //get news id list that user have read
//        List<InteractNews> historyInteracts = interactNewsRepository.findDistinctByUserIdAndStatus(userId, 1);
//        List<Long> readNewsId = new ArrayList<>();
//        for(InteractNews interactNews : historyInteracts) {
//            readNewsId.add(interactNews.getNewsId());
//        }
        List<Long> interactNewsId = interactNewsRepository.findInteractedNewsIdByUserAndStatus(userId, 1);

        //get current uploaded news by topic
        RecommendRequest recommendRequestTopic = new RecommendRequest();
        List<Topic> topicLv1List = topicRepository.findByLevelAndStatus(1,1);
        // list news dc chon ra tu tat ca topic - moi topic chon lay 1 news cao nhat
        List<News> newsFromAllTopicsToRecommend = new ArrayList<>();
        for(Topic topicLv1 : topicLv1List) {
            List<String> contentByTopicToRecommend = new ArrayList<>();
            List<News> newsCurrentTopicToRecommend = new ArrayList<>();
            List<News> newsByTopic = newsRepository.findTop5ByTopicLv1AndStatusOrderByCreateTimeDesc(topicLv1, 1);
            for(News news : newsByTopic) {
                if(interactNewsId!=null && !interactNewsId.contains(news.getId())) {
                    contentByTopicToRecommend.add(news.getContent());
                    newsCurrentTopicToRecommend.add(news);
                }
            }
            if(contentByTopicToRecommend != null && contentByTopicToRecommend.size() > 0) {
                for(News news: historyNews) {
                    contentByTopicToRecommend.add(news.getContent());
                }
                recommendRequestTopic.setData(contentByTopicToRecommend);
                recommendRequestTopic.setRecommendNum(1);
                recommendRequestTopic.setHistoryNum(historyNews.size());
                RecommendResponse recommendResponseTopic = dataAnalystClient.getRecommend(recommendRequestTopic);
                List<Integer> indexs = recommendResponseTopic.getData();
                for (Integer index : indexs) {
                    News news = newsCurrentTopicToRecommend.get(index);
                    newsFromAllTopicsToRecommend.add(news);
                }
            }

        }



        // get current news uploaded and add to list
//        List<News> newsListToday = newsRepository.findByCreateTimeAfterAndTopicLv2InAndStatus(timeToQuery, historyTopics, 1);
//        if(newsListToday == null || newsListToday.size() < 14) {
//            logger.info("not enough today news of history topic for user ", userId);
//            List<News> newsListTodayLoose = newsRepository.findByCreateTimeAfterAndStatus(timeToQuery, 1);
//            if(newsListTodayLoose != null) {
//                newsListToday.addAll(newsListTodayLoose);
//            }
//            if(newsListToday != null && newsListToday.size() < 14) {
//                logger.info("not enough today news after adding loose topic news");
//                //get latest uploaded news
//                newsListToday.addAll(newsRepository.findTop15ByStatusOrderByCreateTime(1));
//                if(newsListToday != null && newsListToday.size() < 14) {
//                    logger.info("not enough today news after adding lastest uploaded news");
//                    return null;
//                }
//            }
//        }

//        for(News news : newsListToday) {
//                contentList.add(news.getContent());
//        }
        for(News news : newsFromAllTopicsToRecommend) {
            contentList.add(news.getContent());
        }
        // add history news to the end of the list
        for(News news: historyNews) {
            contentList.add(news.getContent());
        }
        if(contentList.size() > 15) {
            //call api to get recommend news
            RecommendRequest recommendRequest = new RecommendRequest();
            recommendRequest.setData(contentList);
                recommendRequest.setRecommendNum(5);
                recommendRequest.setHistoryNum(historyNews.size());
            RecommendResponse recommendResponse = dataAnalystClient.getRecommend(recommendRequest);
            logger.info(String.valueOf(recommendResponse));
            List<NewsDto> recommendNews = new ArrayList<>();
            List<Integer> indexs = recommendResponse.getData();
            for (Integer index : indexs) {
//            News news = newsListToday.get(index);
                News news = newsFromAllTopicsToRecommend.get(index);
                recommendNews.add(mapNewsToNewsDto(news));
            }
            return recommendNews;
        } else return null;
    }

    @Override
    public void saveRecommendNews(Long userId) {
        List<String> contentList = new ArrayList<>();

        //get history news of user
        List<InteractNews> userRead = interactNewsRepository.findTop10ByUserIdAndTypeAndStatusOrderByCreateTimeDesc(userId, InteractType.READ, 1);
        List<Long> historyNewsIds = new ArrayList<>();
        if(userRead != null && userRead.size() > 0) {
            for(InteractNews interactNews : userRead) {
//                if(interactNews.getInteractTime().before(timeToQuery)) {
                historyNewsIds.add(interactNews.getNewsId());
//                }
            }
        } else {
            return ;
        }
        //get liked news of user
        List<InteractNews> userLike = interactNewsRepository.findTop5ByUserIdAndTypeAndStatusOrderByCreateTimeDesc(userId, InteractType.LIKE, 1);
        //get shared news of user
        List<InteractNews> userShare = interactNewsRepository.findTop5ByUserIdAndTypeAndStatusOrderByCreateTimeDesc(userId, InteractType.SHARE, 1);

        //add to history list
        List<News> historyNews = new ArrayList<>();
        for(Long id : historyNewsIds) {
            News news = newsRepository.findByIdAndStatus(id,1).get();
            historyNews.add(news);
        }
        if(userLike != null && userLike.size() > 0) {
            for(InteractNews interactNews : userLike) {
                News news = newsRepository.findByIdAndStatus(interactNews.getNewsId(), 1).get();
                if(news != null) {
                    historyNews.add(news);
                }
            }
        }
        if(userShare != null && userShare.size() > 0) {
            for(InteractNews interactNews : userShare) {
                News news = newsRepository.findByIdAndStatus(interactNews.getNewsId(), 1).get();
                if(news != null) {
                    historyNews.add(news);
                }
            }
        }
        if(historyNews == null || historyNews.size() < 10) {
            logger.info("not enough history news to recommend for user ", userId);
            return ;
        }
        List<Long> interactNewsId = interactNewsRepository.findInteractedNewsIdByUserAndStatus(userId, 1);

        //get current uploaded news by topic
        RecommendRequest recommendRequestTopic = new RecommendRequest();
        List<Topic> topicLv1List = topicRepository.findByLevelAndStatus(1,1);
        // list news dc chon ra tu tat ca topic - moi topic chon lay 1 news cao nhat
        List<News> newsFromAllTopicsToRecommend = new ArrayList<>();
        for(Topic topicLv1 : topicLv1List) {
            List<String> contentByTopicToRecommend = new ArrayList<>();
            List<News> newsCurrentTopicToRecommend = new ArrayList<>();
            List<News> newsByTopic = newsRepository.findTop5ByTopicLv1AndStatusOrderByCreateTimeDesc(topicLv1, 1);
            for(News news : newsByTopic) {
                if(interactNewsId!=null && !interactNewsId.contains(news.getId())) {
                    contentByTopicToRecommend.add(news.getContent());
                    newsCurrentTopicToRecommend.add(news);
                }
            }
            if(contentByTopicToRecommend != null && contentByTopicToRecommend.size() > 0) {
                for(News news: historyNews) {
                    contentByTopicToRecommend.add(news.getContent());
                }
                recommendRequestTopic.setData(contentByTopicToRecommend);
                recommendRequestTopic.setRecommendNum(1);
                recommendRequestTopic.setHistoryNum(historyNews.size());
                RecommendResponse recommendResponseTopic = dataAnalystClient.getRecommend(recommendRequestTopic);
                List<Integer> indexs = recommendResponseTopic.getData();
                for (Integer index : indexs) {
                    News news = newsCurrentTopicToRecommend.get(index);
                    newsFromAllTopicsToRecommend.add(news);
                }
            }

        }

        for(News news : newsFromAllTopicsToRecommend) {
            contentList.add(news.getContent());
        }
        // add history news to the end of the list
        for(News news: historyNews) {
            contentList.add(news.getContent());
        }
        if(contentList.size() > 15) {
            //call api to get recommend news
            RecommendRequest recommendRequest = new RecommendRequest();
            recommendRequest.setData(contentList);
            recommendRequest.setRecommendNum(5);
            recommendRequest.setHistoryNum(historyNews.size());
            RecommendResponse recommendResponse = dataAnalystClient.getRecommend(recommendRequest);
            logger.info(String.valueOf(recommendResponse));
//            List<NewsDto> recommendNews = new ArrayList<>();
            List<String> recommendNewsIds = new ArrayList<>();
            List<Integer> indexs = recommendResponse.getData();
            for (Integer index : indexs) {
//            News news = newsListToday.get(index);
                News news = newsFromAllTopicsToRecommend.get(index);
//                recommendNews.add(mapNewsToNewsDto(news));
                recommendNewsIds.add(news.getId().toString());
            }
            String listIdsToSave = String.join(",", recommendNewsIds);
            Optional<RecommendNews> recommendNews = recommendNewsRepository.findById(userId);
            if(recommendNews.isPresent()) {
                RecommendNews updateRecommendNews = recommendNews.get();
                updateRecommendNews.setListNewsId(listIdsToSave);
                updateRecommendNews.setUpdateTime(new Date());
                recommendNewsRepository.save(updateRecommendNews);
            } else {
                RecommendNews newRecommendNews = new RecommendNews();
                newRecommendNews.setUserId(userId);
                newRecommendNews.setListNewsId(listIdsToSave);
                newRecommendNews.setCreateTime(new Date());
                newRecommendNews.setUpdateTime(new Date());
                recommendNewsRepository.save(newRecommendNews);
            }
//            return recommendNews;
        } else return ;
    }

    @Override
    public List<NewsDto> getRecommendNews(Long userId){
        Optional<RecommendNews> recommendNews = recommendNewsRepository.findByUserId(userId);
        if(recommendNews.isPresent()) {
            String listNewsId = recommendNews.get().getListNewsId();
            List<String> newsIds = Arrays.asList(listNewsId.split(","));
            List<News> newsList = new ArrayList<>();
            for(String newsId : newsIds) {
                Long newsIdLong = Long.valueOf(newsId);
                News news = newsRepository.findById(newsIdLong).get();
                newsList.add(news);
            }
            return mapListNewsToListNewsDto(newsList);
        } else {
            return null;
        }
    }


    @Override
    public List<NewsDto> findByTopicExcept(GetNewsByTopicExceptRequest request) {
        Topic topic = topicRepository.findByTopicKeyAndStatus(request.getTopicKey(), 1).get();
//        List<News> newsList = newsRepository.findTop5ByStatusAndTopic_ParentKeyAndIdNotInOrderByCreateTime(1, request.getParentKey(), request.getNewsId());
        List<News> newsList = newsRepository.findTop5ByStatusAndTopicLv1AndIdNotInOrderByCreateTimeDesc(1, topic, request.getNewsId());
        return mapListNewsToListNewsDto(newsList);
    }

    @Override
    public List<NewsDto> findTop3SameTopicTitles(String topicKey, Long newsId) {
        Topic topic = topicRepository.findByTopicKeyAndStatus(topicKey, 1).get();
        List<News> newsList;
        if(topic.getLevel() == 3) {
            newsList = newsRepository.findTop3ByTopicLv3AndStatusAndIdNotOrderByCreateTimeDesc(topic, 1, newsId);
        } else {
            newsList = newsRepository.findTop3ByTopicLv2AndStatusAndIdNotOrderByCreateTimeDesc(topic, 1, newsId);
        }
        return mapListNewsToListNewsDto(newsList);
    }

    @Override
    public List<NewsDto> findRelevantNews(Long newsId) {
        List<String> contentToCalSimilarity = new ArrayList<>();
        List<News> candidateNewsList = new ArrayList<>();
        // target news
        News targetNews = newsRepository.findByIdAndStatus(newsId, 1).get();
        // build list candidate
        if(targetNews.getTopicLv3() != null) {
            Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC,"createTime"));
            Page<News> newsPage = newsRepository.findAllByTopicLv3AndStatus(targetNews.getTopicLv3(), 1, pageable);
            List<News> newsList = newsPage.getContent();
            if(newsList != null && newsList.size() > 0) {
                for(News news: newsList) {
                    if(news.getId() != newsId) {
                        contentToCalSimilarity.add(news.getContent());
                        candidateNewsList.add(news);
                    }
                }
            }
        } else {
            Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC,"createTime"));
            Page<News> newsPage = newsRepository.findAllByTopicLv2AndStatus(targetNews.getTopicLv2(), 1, pageable);
            List<News> newsList = newsPage.getContent();
            if(newsList != null && newsList.size() > 0) {
                for(News news: newsList) {
                    if(news.getId() != newsId) {
                        contentToCalSimilarity.add(news.getContent());
                        candidateNewsList.add(news);
                    }
                }
            }
        }

        contentToCalSimilarity.add(targetNews.getContent());
        if(contentToCalSimilarity.size() > 5 && candidateNewsList.size() > 5) {
            RecommendRequest recommendRequest = new RecommendRequest();
            recommendRequest.setData(contentToCalSimilarity);
            recommendRequest.setRecommendNum(5);
            RecommendResponse recommendResponse = dataAnalystClient.getRelevant(recommendRequest);
            logger.info(String.valueOf(recommendResponse));
            List<NewsDto> relevantNews = new ArrayList<>();
            List<Integer> indexs = recommendResponse.getData();
            for (Integer index : indexs) {
//            News news = newsListToday.get(index);
                News news = candidateNewsList.get(index);
                relevantNews.add(mapNewsToNewsDto(news));
            }
            return relevantNews;
        } else {
            return null;
        }
    }



    @Override
    public NewsDto findById(Long id) {
        Optional<News> newsOptional = newsRepository.findById(id);
        if(newsOptional != null && newsOptional.isPresent()) {
            News news = newsOptional.get();
            return mapNewsToNewsDto(news);
        } else {
            return null;
        }
    }

    private NewsDto mapNewsToNewsDto(News news) {
        NewsDto newsDto = new NewsDto();
        newsDto.setId(news.getId());
        newsDto.setTitle(news.getTitle());
        newsDto.setTopicLv1(news.getTopicLv1());
        newsDto.setTopicLv2(news.getTopicLv2());
        newsDto.setTopicLv3(news.getTopicLv3());
        newsDto.setContent(news.getContent());
        newsDto.setImageUrl(news.getImageUrl());
        newsDto.setSummary(news.getSummary());
        newsDto.setPubDate(news.getPubDate());
        newsDto.setSourceId(news.getSourceId());
        newsDto.setKeyword(news.getKeyword());
        newsDto.setStatus(news.getStatus());
        newsDto.setCreateTime(news.getCreateTime());
        newsDto.setUpdateTime(news.getUpdateTime());
        List<CommentDto> commentDtoList = new ArrayList<>();
        for(Comment comment : news.getComments()) {
            CommentDto commentDto = new CommentDto();
            commentDto.setId(comment.getId());
            commentDto.setContent(comment.getContent());
            commentDto.setCreateTime(comment.getCreateTime());
            commentDto.setUsername(comment.getUser().getUsername());
            commentDtoList.add(commentDto);
        }
        newsDto.setComments(commentDtoList);
        return newsDto;
    }

    private List<NewsDto> mapListNewsToListNewsDto(List<News> newsList) {
        List<NewsDto> newsDtos = new ArrayList<>();
        for(News news: newsList) {
            newsDtos.add(mapNewsToNewsDto(news));
        }
        return newsDtos;
    }


}
