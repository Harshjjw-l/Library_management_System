package com.lms.library_management_system.controller;

import com.lms.library_management_system.model.Publisher;
import com.lms.library_management_system.service.PublisherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/publishers")
public class PublisherController {

    @Autowired
    private PublisherService publisherService;

    @PostMapping
    public ResponseEntity<Publisher> addPublisher(@RequestBody Publisher publisher) {
        return ResponseEntity.ok(publisherService.addPublisher(publisher));
    }

    @GetMapping
    public ResponseEntity<List<Publisher>> getAllPublishers() {
        return ResponseEntity.ok(publisherService.findAllPublishers());
    }

    // Add getById, update, delete endpoints here...
}