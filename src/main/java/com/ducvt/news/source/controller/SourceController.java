package com.ducvt.news.source.controller;

import com.ducvt.news.fw.utils.ResponseFactory;
import com.ducvt.news.source.models.dto.SourceDto;
import com.ducvt.news.source.models.dto.SourcePageDto;
import com.ducvt.news.source.service.SourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/source")
public class SourceController {
    @Autowired
    SourceService sourceService;
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity findAllDto(@RequestParam int page, @RequestParam int offset) {
        SourcePageDto sourcePageDto = sourceService.findAllSource(page, offset);
        return ResponseFactory.success(sourcePageDto);
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity searchByName(@RequestParam String name, @RequestParam int page, @RequestParam int offset) {
        SourcePageDto sourcePageDto = sourceService.searchByName(name, page, offset);
        return ResponseFactory.success(sourcePageDto);
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity update(@RequestBody SourceDto sourceDto) {
        sourceService.update(sourceDto);
        return ResponseFactory.success();
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity create(@RequestBody SourceDto sourceDto) {
        sourceService.create(sourceDto);
        return ResponseFactory.success();
    }

    @PutMapping("/stop/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity stop(@PathVariable Integer id) {
        sourceService.stop(id);
        return ResponseFactory.success();
    }

    @PutMapping("/start/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity start(@PathVariable Integer id) {
        sourceService.start(id);
        return ResponseFactory.success();
    }


}
