package com.ducvt.news.news.service.impl;

import com.ducvt.news.account.models.User;
import com.ducvt.news.account.repository.UserRepository;
import com.ducvt.news.fw.constant.MessageEnum;
import com.ducvt.news.fw.exceptions.BusinessLogicException;
import com.ducvt.news.news.models.Comment;
import com.ducvt.news.news.models.News;
import com.ducvt.news.news.payload.request.CommentRequest;
import com.ducvt.news.news.repository.CommentRepository;
import com.ducvt.news.news.repository.NewsRepository;
import com.ducvt.news.news.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void insert(CommentRequest commentRequest) {
        Comment comment = new Comment();
        comment.setContent(commentRequest.getContent());
        News news = newsRepository.getById(commentRequest.getNewsId());
        if(news == null) {
            throw new BusinessLogicException(MessageEnum.NOT_FOUND_USER_BY_ID.getMessage());
        }
        comment.setNews(news);

        User user = userRepository.getById(commentRequest.getUserId());
        if(user == null) {
            throw new BusinessLogicException(MessageEnum.NOT_FOUND_USER_BY_ID.getMessage());
        }
        comment.setUser(user);
        comment.setStatus(1);
        comment.setCreateTime(new Date());
        comment.setUpdateTime(new Date());
        commentRepository.save(comment);
    }
}
