package com.ducvt.news.account.controllers;

import com.ducvt.news.account.models.User;
import com.ducvt.news.account.security.jwt.JwtUtils;
import com.ducvt.news.account.service.UserService;
import com.ducvt.news.fw.utils.ResponseFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity getAll(@RequestParam int page, @RequestParam int offset) {
        Page<User> userPage = userService.getAll(page, offset);
        return ResponseFactory.success(userPage);
    }

    @GetMapping(value = "/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity searchByUserName(@RequestParam int page, @RequestParam int offset, @RequestParam String username) {
        Page<User> userPage = userService.searchByUsername(username, page, offset);
        return ResponseFactory.success(userPage);
    }

    @PutMapping(value = "/update")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity update(@RequestBody User user) {
        userService.update(user);
        return ResponseFactory.success();
    }


}
