package com.ducvt.news.account.service;

import com.ducvt.news.account.models.User;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    Page<User> getAll(int page, int offset);

    Page<User> searchByUsername(String username, int page, int offset);

    void update(User user);
}
