package com.ducvt.news.source.repository;

import com.ducvt.news.source.models.Source;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SourceRepository extends JpaRepository<Source, Integer> {
    Page<Source> findAllBy(Pageable pageable);

    Page<Source> findByNameContains(String username, Pageable pageable);
}
