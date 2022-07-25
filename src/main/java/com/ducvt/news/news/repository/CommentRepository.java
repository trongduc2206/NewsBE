package com.ducvt.news.news.repository;

import com.ducvt.news.account.models.User;
import com.ducvt.news.news.models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findTop5ByUserAndStatus(User user, Integer status);
}
