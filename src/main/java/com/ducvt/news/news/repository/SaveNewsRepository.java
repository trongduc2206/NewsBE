package com.ducvt.news.news.repository;

import com.ducvt.news.news.models.SaveNews;
import com.ducvt.news.news.models.SaveNewsId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.List;
import java.util.Optional;

public interface SaveNewsRepository  extends JpaRepository<SaveNews, SaveNewsId> {
    Page<SaveNews> findAllByUserIdAndStatus(Long userId, Integer status, Pageable pageable);

    List<SaveNews> findByUserIdAndStatus(Long userId, Integer status);

    Optional<SaveNews> findByUserIdAndNewsIdAndStatus(Long userId, Long newsId, Integer status);
}
