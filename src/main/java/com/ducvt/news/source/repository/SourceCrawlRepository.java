package com.ducvt.news.source.repository;

import com.ducvt.news.source.models.SourceCrawl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SourceCrawlRepository extends JpaRepository<SourceCrawl, Integer> {
    List<SourceCrawl> findAllBySourceIdAndStatus(Integer sourceId, Integer status);
}
