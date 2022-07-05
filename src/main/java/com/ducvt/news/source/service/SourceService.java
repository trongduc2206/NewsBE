package com.ducvt.news.source.service;

import com.ducvt.news.source.models.Source;
import com.ducvt.news.source.models.dto.SourcePageDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface SourceService {
    SourcePageDto findAllSource(int page, int offset);

    SourcePageDto searchByName(String name, int page, int offset);
}