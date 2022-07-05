package com.ducvt.news.source.controller;

import com.ducvt.news.fw.utils.ResponseFactory;
import com.ducvt.news.source.models.dto.SourcePageDto;
import com.ducvt.news.source.service.SourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/source")
public class SourceController {
    @Autowired
    SourceService sourceService;
    @GetMapping
    public ResponseEntity findAllDto(@RequestParam int page, @RequestParam int offset) {
        SourcePageDto sourcePageDto = sourceService.findAllSource(page, offset);
        return ResponseFactory.success(sourcePageDto);
    }

    @GetMapping("/search")
    public ResponseEntity searchByName(@RequestParam String name, @RequestParam int page, @RequestParam int offset) {
        SourcePageDto sourcePageDto = sourceService.searchByName(name, page, offset);
        return ResponseFactory.success(sourcePageDto);
    }
}
