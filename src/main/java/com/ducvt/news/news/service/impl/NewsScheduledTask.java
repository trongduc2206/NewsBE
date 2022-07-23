package com.ducvt.news.news.service.impl;

import com.ducvt.news.news.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NewsScheduledTask {
    @Autowired
    NewsService newsService;

}
