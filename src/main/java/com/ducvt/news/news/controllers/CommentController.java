package com.ducvt.news.news.controllers;

import com.ducvt.news.fw.utils.ResponseFactory;
import com.ducvt.news.news.payload.request.CommentRequest;
import com.ducvt.news.news.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @PostMapping
    public ResponseEntity insert(@RequestBody CommentRequest commentRequest) {
        commentService.insert(commentRequest);
        return ResponseFactory.success();
    }
}
