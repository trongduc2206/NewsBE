package com.ducvt.news.news.service.impl;

import com.ducvt.news.fw.constant.MessageConstant;
import com.ducvt.news.news.models.InteractNews;
import com.ducvt.news.news.models.enums.InteractType;
import com.ducvt.news.news.payload.request.InteractNewsRequest;
import com.ducvt.news.news.repository.InteractNewsRepository;
import com.ducvt.news.news.service.InteractNewsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class InteractNewsServiceImpl implements InteractNewsService {
    private static Logger logger = LoggerFactory.getLogger(InteractNewsServiceImpl.class);
    @Autowired
    private InteractNewsRepository interactNewsRepository;
    @Override
    public void insert(InteractNewsRequest interactNewsRequest) {
        if(interactNewsRequest.getType().equals(InteractType.DISLIKE)) {
            InteractNews likeNews = interactNewsRepository.findByStatusAndUserIdAndNewsIdAndType(1, interactNewsRequest.getUserId(), interactNewsRequest.getNewsId(), InteractType.LIKE);
            if(likeNews != null) {
                likeNews.setStatus(0);
                interactNewsRepository.save(likeNews);
            }
        } else if(interactNewsRequest.getType().equals(InteractType.LIKE)) {
            InteractNews likeNews = interactNewsRepository.findByStatusAndUserIdAndNewsIdAndType(1, interactNewsRequest.getUserId(), interactNewsRequest.getNewsId(), InteractType.LIKE);
            if(likeNews == null) {
                insertNewInteract(interactNewsRequest);
            }
        } else {
            insertNewInteract(interactNewsRequest);
        }
    }

    @Override
    public Boolean checkLike(Long userId, Long newsId) {
        InteractNews likeNews = interactNewsRepository.findByStatusAndUserIdAndNewsIdAndType(1, userId, newsId, InteractType.LIKE);
        if(likeNews != null) {
            return true;
        }
        return false;
    }


    private void insertNewInteract(InteractNewsRequest interactNewsRequest) {
        InteractNews interactNews = new InteractNews();
        interactNews.setUserId(interactNewsRequest.getUserId());
        interactNews.setNewsId(interactNewsRequest.getNewsId());
        interactNews.setType(interactNewsRequest.getType());
        interactNews.setStatus(MessageConstant.ACTIVE_STATUS);
        if (interactNewsRequest.getInteractTime() != null) {
            interactNews.setInteractTime(interactNewsRequest.getInteractTime());
        } else {
            interactNews.setInteractTime(new Date());
        }
        interactNews.setCreateTime(new Date());
        interactNews.setUpdateTime(new Date());
        interactNewsRepository.save(interactNews);
        logger.info("interact saved to db -> " + interactNewsRequest);
    }

    @Override
    public InteractNews getById(Long id){
        return interactNewsRepository.findById(id).get();
    }
}
