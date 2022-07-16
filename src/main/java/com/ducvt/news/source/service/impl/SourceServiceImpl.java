package com.ducvt.news.source.service.impl;

import com.ducvt.news.fw.constant.MessageEnum;
import com.ducvt.news.fw.exceptions.BusinessLogicException;
import com.ducvt.news.news.models.Topic;
import com.ducvt.news.news.repository.TopicRepository;
import com.ducvt.news.source.models.Source;
import com.ducvt.news.source.models.SourceCrawl;
import com.ducvt.news.source.models.dto.SourceCrawlDto;
import com.ducvt.news.source.models.dto.SourceDto;
import com.ducvt.news.source.models.dto.SourcePageDto;
import com.ducvt.news.source.models.dto.TopicCrawl;
import com.ducvt.news.source.repository.SourceCrawlRepository;
import com.ducvt.news.source.repository.SourceRepository;
import com.ducvt.news.source.service.SourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SourceServiceImpl implements SourceService {
    private static Logger logger = LoggerFactory.getLogger(SourceServiceImpl.class);
    @Autowired
    SourceRepository sourceRepository;

    @Autowired
    SourceCrawlRepository sourceCrawlRepository;

    @Autowired
    TopicRepository topicRepository;

    @Override
    public SourcePageDto findAllSource(int page, int offset) {
        Pageable pageable = PageRequest.of(page, offset, Sort.by(Sort.Direction.ASC, "createTime"));
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
        Pageable pageable = PageRequest.of(page, offset, Sort.by(Sort.Direction.ASC, "createTime"));
        Page<Source> sourcePage = sourceRepository.findByNameContains(name, pageable);
        List<Source> sourceList = sourcePage.getContent();
        List<SourceDto> sourceDtos = mapListSourceToListSourceDto(sourceList);

        SourcePageDto sourcePageDto = new SourcePageDto();
        sourcePageDto.setContent(sourceDtos);
        sourcePageDto.setTotalElements(sourcePage.getTotalElements());
        return sourcePageDto;
    }

    @Override
    public void update(SourceDto sourceDto) {
        Source source = sourceRepository.findById(sourceDto.getId()).get();
        if (sourceDto.getName() == null || sourceDto.getName().isEmpty()) {
            throw new BusinessLogicException(MessageEnum.EMPTY_SOURCE_NAME.getMessage());
        }
        if (!sourceDto.getName().equals(source.getName())) {
            if (sourceRepository.existsByName(sourceDto.getName())) {
                throw new BusinessLogicException(MessageEnum.DUPLICATE_SOURCE_NAME.getMessage());
            }
            source.setName(sourceDto.getName());
        }
        if (sourceDto.getStatus() != null) {
            source.setStatus(sourceDto.getStatus());
        }
        if (sourceDto.getMode() != null) {
            source.setMode(sourceDto.getMode());
            // soft delete old source crawl data
            List<SourceCrawl> sourceCrawls = sourceCrawlRepository.findAllBySourceIdAndStatus(sourceDto.getId(), 1);
            for (SourceCrawl sourceCrawl : sourceCrawls) {
                sourceCrawl.setStatus(0);
                sourceCrawlRepository.save(sourceCrawl);
            }
            if (sourceDto.getMode() == 1) {
                source.setFrequency(sourceDto.getFrequency());
            } else {
                source.setFrequency(null);
                // xu ly sourceCrawls custom mode
                if(sourceDto.getSourceCrawls() != null && sourceDto.getSourceCrawls().size() > 0) {
                    for (SourceCrawlDto sourceCrawlDto : sourceDto.getSourceCrawls()) {
                        List<TopicCrawl> topicCrawls = sourceCrawlDto.getTopicCrawls();
                        for (TopicCrawl topicCrawl : topicCrawls) {
                            SourceCrawl sourceCrawl = new SourceCrawl();
                            Topic topic;
                            if (topicCrawl.getTopicKey() != null && !topicCrawl.getTopicKey().isEmpty()) {
                                topic = topicRepository.findByTopicKeyAndStatus(topicCrawl.getTopicKey(), 1).get();
                            } else {
                                throw new BusinessLogicException(MessageEnum.EMPTY_SOURCE_CRAWL_TOPIC_KEY.getMessage());
                            }
                            sourceCrawl.setSourceId(sourceDto.getId());
                            sourceCrawl.setTopicId(topic.getId());
                            if (topicCrawl.getCrawlUrl() != null && !topicCrawl.getCrawlUrl().isEmpty()) {
                                sourceCrawl.setCrawlUrl(topicCrawl.getCrawlUrl());
                            } else {
                                throw new BusinessLogicException(MessageEnum.EMPTY_SOURCE_CRAWL_URL.getMessage());
                            }
                            if (sourceCrawlDto.getCrawlTime() != null && !sourceCrawlDto.getCrawlTime().contains("new-")) {
                                sourceCrawl.setCrawlTime(sourceCrawlDto.getCrawlTime());
                            } else {
                                throw new BusinessLogicException(MessageEnum.EMPTY_SOURCE_CRAWL_TIME.getMessage());
                            }
                            sourceCrawl.setStatus(1);
                            sourceCrawl.setCreateTime(new Date());
                            sourceCrawl.setUpdateTime(new Date());
                            sourceCrawlRepository.save(sourceCrawl);
                        }
                    }
                } else {
                    throw new BusinessLogicException(MessageEnum.EMPTY_SOURCE_CRAWLS.getMessage());
                }
            }
        } else {
            throw new BusinessLogicException(MessageEnum.EMPTY_SOURCE_MODE.getMessage());
        }
        source.setUpdateTime(new Date());
        sourceRepository.save(source);
    }

    @Override
    public void create(SourceDto sourceDto) {
        Source source = new Source();
        if (sourceDto.getName() == null || sourceDto.getName().isEmpty()) {
            throw new BusinessLogicException(MessageEnum.EMPTY_SOURCE_NAME.getMessage());
        }
        if (sourceRepository.existsByName(sourceDto.getName())) {
            throw new BusinessLogicException(MessageEnum.DUPLICATE_SOURCE_NAME.getMessage());
        }
        source.setName(sourceDto.getName());

        if (sourceDto.getStatus() != null) {
            source.setStatus(sourceDto.getStatus());
        } else {
            throw new BusinessLogicException(MessageEnum.EMPTY_SOURCE_STATUS.getMessage());
        }
        if (sourceDto.getMode() != null) {
            source.setMode(sourceDto.getMode());
            // soft delete old source crawl data
//            List<SourceCrawl> sourceCrawls = sourceCrawlRepository.findAllBySourceIdAndStatus(sourceDto.getId(), 1);
//            for (SourceCrawl sourceCrawl : sourceCrawls) {
//                sourceCrawl.setStatus(0);
//                sourceCrawlRepository.save(sourceCrawl);
//            }
            if (sourceDto.getMode() == 1) {
                if(sourceDto.getFrequency() != null) {
                    source.setFrequency(sourceDto.getFrequency());
                } else {
                    throw new BusinessLogicException(MessageEnum.EMPTY_SOURCE_FREQUENCY.getMessage());
                }
                source.setCreateTime(new Date());
                source.setUpdateTime(new Date());
                sourceRepository.save(source);
            } else {
                source.setFrequency(null);
                source.setCreateTime(new Date());
                source.setUpdateTime(new Date());
                sourceRepository.save(source);
                Integer sourceId = source.getId();
                // xu ly sourceCrawls custom mode
                //todo validate case sourcecrawls null
                if(sourceDto.getSourceCrawls() != null && sourceDto.getSourceCrawls().size() > 0) {
                    for (SourceCrawlDto sourceCrawlDto : sourceDto.getSourceCrawls()) {
                        List<TopicCrawl> topicCrawls = sourceCrawlDto.getTopicCrawls();
                        for (TopicCrawl topicCrawl : topicCrawls) {
                            SourceCrawl sourceCrawl = new SourceCrawl();
                            Topic topic;
                            if(topicCrawl.getTopicKey() != null && !topicCrawl.getTopicKey().isEmpty()) {
                                 topic = topicRepository.findByTopicKeyAndStatus(topicCrawl.getTopicKey(), 1).get();
                            } else {
                                sourceRepository.delete(source);
                                throw new BusinessLogicException(MessageEnum.EMPTY_SOURCE_CRAWL_TOPIC_KEY.getMessage());
                            }
                            sourceCrawl.setTopicId(topic.getId());
                            sourceCrawl.setSourceId(sourceId);
                            if(topicCrawl.getCrawlUrl() != null && !topicCrawl.getCrawlUrl().isEmpty()) {
                                sourceCrawl.setCrawlUrl(topicCrawl.getCrawlUrl());
                            } else {
                                sourceRepository.delete(source);
                                throw new BusinessLogicException(MessageEnum.EMPTY_SOURCE_CRAWL_URL.getMessage());
                            }
                            if(sourceCrawlDto.getCrawlTime() != null && !sourceCrawlDto.getCrawlTime().contains("new-")) {
                                sourceCrawl.setCrawlTime(sourceCrawlDto.getCrawlTime());
                            } else {
                                sourceRepository.delete(source);
                                throw new BusinessLogicException(MessageEnum.EMPTY_SOURCE_CRAWL_TIME.getMessage());
                            }
                            sourceCrawl.setStatus(1);
                            sourceCrawl.setCreateTime(new Date());
                            sourceCrawl.setUpdateTime(new Date());
                            sourceCrawlRepository.save(sourceCrawl);
                        }
                    }
                } else {
                    sourceRepository.delete(source);
                    throw new BusinessLogicException(MessageEnum.EMPTY_SOURCE_CRAWLS.getMessage());
                }
            }
        } else {
            throw new BusinessLogicException(MessageEnum.EMPTY_SOURCE_MODE.getMessage());
        }

    }

    @Override
    public void delete(Integer sourceId) {
        Optional<Source> sourceToDelete = sourceRepository.findById(sourceId);
        if(sourceToDelete.isPresent()) {
            Source source = sourceToDelete.get();
            if(source.getMode() == 2) {
                logger.info("deleting a source with custom mode");
                List<SourceCrawl> sourceCrawls = sourceCrawlRepository.findAllBySourceIdAndStatus(sourceId, 1);
                if(sourceCrawls != null && sourceCrawls.size() > 0) {
                    sourceCrawlRepository.deleteAll(sourceCrawls);
                }
            }
            //:todo delete source
            sourceRepository.delete(source);
        } else {
            throw new BusinessLogicException(MessageEnum.NOT_FOUND_SOURCE_BY_ID.getMessage());
        }
    }

    @Override
    public void stop(Integer sourceId) {
        Optional<Source> sourceToDelete = sourceRepository.findById(sourceId);
        if(sourceToDelete.isPresent()) {
            Source source = sourceToDelete.get();
//            if(source.getMode() == 2) {
//                logger.info("soft deleting a source with custom mode");
//                List<SourceCrawl> sourceCrawls = sourceCrawlRepository.findAllBySourceIdAndStatus(sourceId, 1);
//                if(sourceCrawls != null && sourceCrawls.size() > 0) {
//                    for(SourceCrawl sourceCrawl : sourceCrawls) {
//                        sourceCrawl.setStatus(0);
//                        sourceCrawlRepository.save(sourceCrawl);
//                    }
//                }
//            }
            //:todo delete source
            source.setStatus(0);
            sourceRepository.save(source);
        } else {
            throw new BusinessLogicException(MessageEnum.NOT_FOUND_SOURCE_BY_ID.getMessage());
        }
    }

    @Override
    public void start(Integer sourceId) {
        Optional<Source> sourceToDelete = sourceRepository.findById(sourceId);
        if(sourceToDelete.isPresent()) {
            Source source = sourceToDelete.get();
            //:todo delete source
            source.setStatus(1);
            sourceRepository.save(source);
        } else {
            throw new BusinessLogicException(MessageEnum.NOT_FOUND_SOURCE_BY_ID.getMessage());
        }
    }

    private List<SourceCrawlDto> mapSourceCrawlToSourceCrawlDto(List<SourceCrawl> sourceCrawls) {
        List<SourceCrawlDto> sourceCrawlDtos = new ArrayList<>();
        Map<String, List<TopicCrawl>> map = new HashMap<>();
        for (SourceCrawl sourceCrawl : sourceCrawls) {
            Topic topic = topicRepository.findById(sourceCrawl.getTopicId()).get();
//            List<String> topicList;
//            List<String> crawlUrls;
            List<TopicCrawl> topicCrawls;
            if (map.containsKey(sourceCrawl.getCrawlTime())) {
                topicCrawls = map.get(sourceCrawl.getCrawlTime());
            } else {
                topicCrawls = new ArrayList<>();
            }
            TopicCrawl topicCrawl = new TopicCrawl();
            topicCrawl.setTopicKey(topic.getTopicKey());
            topicCrawl.setCrawlUrl(sourceCrawl.getCrawlUrl());
            topicCrawls.add(topicCrawl);
            map.put(sourceCrawl.getCrawlTime(), topicCrawls);
        }
        for (Map.Entry<String, List<TopicCrawl>> entry : map.entrySet()) {
            SourceCrawlDto sourceCrawlDto = new SourceCrawlDto();
            sourceCrawlDto.setCrawlTime(entry.getKey());
            sourceCrawlDto.setTopicCrawls(entry.getValue());
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
        for (Source source : sources) {
            sourceDtos.add(mapSourceToSourceDto(source));
        }
        return sourceDtos;
    }
}
