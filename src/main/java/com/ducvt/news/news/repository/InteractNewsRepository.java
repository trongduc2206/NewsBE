package com.ducvt.news.news.repository;

import com.ducvt.news.news.models.InteractNews;
import com.ducvt.news.news.models.InteractNewsId;
import com.ducvt.news.news.models.enums.InteractType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InteractNewsRepository extends JpaRepository<InteractNews, Long> {
    List<InteractNews> findTop10ByUserIdAndTypeAndStatusOrderByCreateTimeDesc(Long userId, InteractType type, Integer status);
    List<InteractNews> findTop5ByUserIdAndTypeAndStatusOrderByCreateTimeDesc(Long userId, InteractType type, Integer status);
    List<InteractNews> findDistinctByUserIdAndStatus(Long userId, Integer status);
    @Query("SELECT DISTINCT i.newsId FROM InteractNews i WHERE i.userId = ?1 and i.status = ?2")
    List<Long> findInteractedNewsIdByUserAndStatus(Long userId, Integer status);

    InteractNews findByStatusAndUserIdAndNewsIdAndType(Integer status, Long userId, Long newsId, InteractType type);

}
