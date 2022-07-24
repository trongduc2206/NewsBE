package com.ducvt.news.news.controllers;

import com.ducvt.news.fw.utils.ResponseFactory;
import com.ducvt.news.news.payload.request.InteractNewsRequest;
import com.ducvt.news.news.service.InteractNewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/interact")
public class InteractNewsController {
    @Autowired
    private InteractNewsService interactNewsService;

    @GetMapping("/{id}")
    public ResponseEntity getById(@PathVariable Long id){
        return ResponseFactory.success(interactNewsService.getById(id));
    }

    @PostMapping(value = "/news")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity insertInteractNews(@RequestBody InteractNewsRequest interactNewsRequest) {
        interactNewsService.insert(interactNewsRequest);
        return ResponseFactory.success();
    }

    @GetMapping(value = "/check-like")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity checkLike(@RequestParam Long userId, @RequestParam Long newsId) {
        return ResponseFactory.success(interactNewsService.checkLike(userId, newsId));
    }
}
