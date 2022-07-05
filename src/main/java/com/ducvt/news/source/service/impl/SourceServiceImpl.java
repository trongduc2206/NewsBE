package com.ducvt.news.source.service.impl;

import com.ducvt.news.news.models.Topic;
import com.ducvt.news.news.repository.TopicRepository;
import com.ducvt.news.source.models.Source;
import com.ducvt.news.source.models.SourceCrawl;
import com.ducvt.news.source.models.dto.SourceCrawlDto;
import com.ducvt.news.source.models.dto.SourceDto;
import com.ducvt.news.source.models.dto.SourcePageDto;
import com.ducvt.news.source.repository.SourceCrawlRepository;
import com.ducvt.news.source.repository.SourceRepository;
import com.ducvt.news.source.service.SourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SourceServiceImpl implements SourceService {
    @Autowired
    SourceRepository sourceRepository;

    @Autowired
    SourceCrawlRepository sourceCrawlRepository;

    @Autowired
    TopicRepository topicRepository;

    @Override
    public SourcePageDto findAllSource(int page, int offset) {
        Pageable pageable = PageRequest.of(page, offset, Sort.by(Sort.Direction.ASC,"createTime"));
        Page<Source> sourcePage = sourceRepository.findAllBy(pageable);
        List<Source> sourceList = sourcePage.getContent();
        List<SourceDto> sourceDtos = mapListSourceToListSourceDto(sourceList);

        SourcePageDto sourcePageDto = new SourcePageDto();
        sourcePageDto.setContent(sourceDtos);
        sourcePageDto.setTotalElements(sourcePage.getTotalElements());
        return sourcePageDto;
    }

    @Override
    public SourcePageDto searchByName(String name, int page, int offset) {
        Pageable pageable = PageRequest.of(page, offset, Sort.by(Sort.Direction.ASC,"createTime"));
        Page<Source> sourcePage = sourceRepository.findByNameContains(name, pageable);
        List<Source> sourceList = sourcePage.getContent();
        List<SourceDto> sourceDtos = mapListSourceToListSourceDto(sourceList);

        SourcePageDto sourcePageDto = new SourcePageDto();
        sourcePageDto.setContent(sourceDtos);
        sourcePageDto.setTotalElements(sourcePage.getTotalElements());
        return sourcePageDto;
    }


    private List<SourceCrawlDto> mapSourceCrawlToSourceCrawlDto(List<SourceCrawl> sourceCrawls) {
        List<SourceCrawlDto> sourceCrawlDtos = new ArrayList<>();
        Map<String, List<String>> map = new HashMap<>();
        for(SourceCrawl sourceCrawl : sourceCrawls) {
            Topic topic = topicRepository.findById(sourceCrawl.getTopicId()).get();
            List<String> topicList;
            if(map.containsKey(sourceCrawl.getCrawlTime())) {
                topicList = map.get(sourceCrawl.getCrawlTime());
            } else {
                topicList = new ArrayList<>();
            }
            topicList.add(topic.getTopicKey());
            map.put(sourceCrawl.getCrawlTime(), topicList);
        }
        for(Map.Entry<String, List<String>> entry : map.entrySet()) {
            SourceCrawlDto sourceCrawlDto = new SourceCrawlDto();
            sourceCrawlDto.setCrawlTime(entry.getKey());
            sourceCrawlDto.setTopicList(entry.getValue());
            sourceCrawlDtos.add(sourceCrawlDto);
        }
        return sourceCrawlDtos;
    }

    private SourceDto mapSourceToSourceDto(Source source) {
        List<SourceCrawl> sourceCrawls = sourceCrawlRepository.findAllBySourceIdAndStatus(source.getId(), 1);
        List<SourceCrawlDto> sourceCrawlDtos = mapSourceCrawlToSourceCrawlDto(sourceCrawls);
        SourceDto sourceDto = new SourceDto();
        sourceDto.setId(source.getId());
        sourceDto.setName(source.getName());
        sourceDto.setStatus(source.getStatus());
        sourceDto.setMode(source.getMode());
        sourceDto.setCreateTime(source.getCreateTime());
        sourceDto.setUpdateTime(source.getUpdateTime());
        sourceDto.setFrequency(source.getFrequency());
        sourceDto.setSourceCrawls(sourceCrawlDtos);
        return sourceDto;
    }

    private List<SourceDto> mapListSourceToListSourceDto(List<Source> sources) {
        List<SourceDto> sourceDtos = new ArrayList<>();
        for(Source source : sources) {
            sourceDtos.add(mapSourceToSourceDto(source));
        }
        return sourceDtos;
    }
}
