package com.ducvt.news.news.service;

import com.ducvt.news.news.payload.request.CommentRequest;
import org.springframework.stereotype.Service;

@Service
public interface CommentService {
    void insert(CommentRequest commentRequest);
}
