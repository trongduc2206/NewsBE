package com.ducvt.news.account.service.impl;

import com.ducvt.news.account.models.User;
import com.ducvt.news.account.repository.UserRepository;
import com.ducvt.news.account.service.UserService;
import com.ducvt.news.fw.constant.MessageEnum;
import com.ducvt.news.fw.exceptions.BusinessLogicException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;
    @Override
    public Page<User> getAll(int page, int offset) {
        Pageable pageable = PageRequest.of(page, offset, Sort.by(Sort.Direction.ASC,"createTime"));
        Page<User> userPage = userRepository.findAllBy(pageable);
//        List<User> userList = userPage.getContent();
//        List<User> userListDecodePassword
        return userPage;
    }

    @Override
    public Page<User> searchByUsername(String username, int page, int offset) {
        Pageable pageable = PageRequest.of(page, offset, Sort.by(Sort.Direction.ASC, "createTime"));
        Page<User> userPage = userRepository.findByUsernameContains(username, pageable);
        return userPage;
    }

    @Override
    public void update(User user) {
        if(user.getUsername() == null || user.getUsername().isEmpty()) {
            throw new BusinessLogicException(MessageEnum.EMPTY_USERNAME.getMessage());
        }
        User oldUser = userRepository.findById(user.getId()).get();
        if(!user.getUsername().equals(oldUser.getUsername())) {
            if (userRepository.existsByUsername(user.getUsername())) {
                throw new BusinessLogicException(MessageEnum.DUPLICATE_USERNAME.getMessage());
            }
            oldUser.setUsername(user.getUsername());
        }
        if(user.getEmail() != null) {
            oldUser.setEmail(user.getEmail());
        }
        if(user.getStatus() != null) {
            oldUser.setStatus(user.getStatus());
        }
        oldUser.setUpdateTime(new Date());

        userRepository.save(oldUser);
    }

}
