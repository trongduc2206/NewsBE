package com.ducvt.news.news.repository;

import com.ducvt.news.news.models.ClickTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ClickTopicRepository extends JpaRepository<ClickTopic, Long> {
    @Query(value = "select * from click_topic where user_id = ?1 group by topic_id having count(id)  > 5 order by count(id) desc limit 3", nativeQuery = true)
    List<ClickTopic> find3MostClickedTopic(Long userId);
}
