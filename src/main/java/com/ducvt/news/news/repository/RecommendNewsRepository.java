package com.ducvt.news.news.repository;

import com.ducvt.news.news.models.RecommendNews;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecommendNewsRepository extends JpaRepository<RecommendNews, Long> {
}
