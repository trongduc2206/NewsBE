package com.ducvt.news.news.repository;

import com.ducvt.news.news.models.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Integer> {
    List<Topic> findByParentKeyNullAndStatus(Integer status);
    Optional<List<Topic>> findByParentKeyAndStatus(String parentKey, Integer status);
    Optional<Topic> findByTopicKeyAndStatus(String topicKey, Integer status);
    List<Topic> findByLevelAndStatus(Integer level, Integer status);
}
