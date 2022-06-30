package com.ducvt.news.news.repository;

import com.ducvt.news.news.models.News;
import com.ducvt.news.news.models.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.xml.crypto.Data;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface NewsRepository extends JpaRepository<News, Long> {
    Page<News> findAllByTopicLv1AndStatus(Topic topic, Integer status, Pageable pageable);

    Page<News> findAllByTopicLv2AndStatus(Topic topic, Integer status, Pageable pageable);

    Page<News> findAllByTopicLv3AndStatus(Topic topic, Integer status, Pageable pageable);

//    Page<News> findAllByTopic_ParentKeyAndStatus(String parentKey, Integer status, Pageable pageable);

    Optional<News> findByIdAndStatus(Long id, Integer status);

    List<News> findByIdInAndStatus(List<Long> ids, Integer status);

    Page<News> findAllByTitleContainingIgnoreCaseAndStatusOrContentContainingIgnoreCaseAndStatus(String queryTitle, Integer status1, String queryContent, Integer status2, Pageable pageable);

    List<News> findByCreateTimeAfterAndTopicLv2InAndStatus(Date time, List<Topic> topics, Integer status);

    List<News> findByCreateTimeAfterAndStatus(Date time, Integer status);

    List<News> findTop15ByStatusOrderByCreateTime(Integer status);

    List<News> findTop5ByStatusAndTopicLv1AndIdNotInOrderByCreateTime(Integer status, Topic topic, List<Long> ids);

    List<News> findTop5ByTopicLv1AndStatusOrderByCreateTimeDesc(Topic topic, Integer status);

    List<News> findTop3ByTopicLv2AndStatusAndIdNotOrderByCreateTimeDesc(Topic topic, Integer status, Long id);

    List<News> findTop3ByTopicLv3AndStatusAndIdNotOrderByCreateTimeDesc(Topic topic, Integer status, Long id);

}
