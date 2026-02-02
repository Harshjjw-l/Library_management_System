package com.lms.library_management_system.controller;

import com.lms.library_management_system.model.Status;
import com.lms.library_management_system.service.StatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/status")
public class StatusController {

    @Autowired
    private StatusService statusService;

    @PostMapping
    public ResponseEntity<Status> addStatus(@RequestBody Status status) {
        return ResponseEntity.ok(statusService.addStatus(status));
    }

    @GetMapping
    public ResponseEntity<List<Status>> getAllStatuses() {
        return ResponseEntity.ok(statusService.findAllStatuses());
    }
    
    // Add getById, update, delete endpoints here...
}