package com.ducvt.news.news.service;

import com.ducvt.news.news.models.InteractNews;
import com.ducvt.news.news.payload.request.InteractNewsRequest;
import org.springframework.stereotype.Service;

@Service
public interface InteractNewsService {
    void insert(InteractNewsRequest interactNewsRequest);

    InteractNews getById(Long id);
}
