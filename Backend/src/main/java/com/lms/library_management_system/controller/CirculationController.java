package com.lms.library_management_system.controller;

import com.lms.library_management_system.model.Status;
import com.lms.library_management_system.service.CirculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/circulation")
public class CirculationController {

    @Autowired
    private CirculationService circulationService;

    @PostMapping("/issue")
    public ResponseEntity<?> issueBook(@RequestParam Long bookId, @RequestParam String studentPrn) {
        try {
            Status newStatus = circulationService.issueBook(bookId, studentPrn);
            return ResponseEntity.ok(newStatus);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/return")
    public ResponseEntity<?> returnBook(@RequestParam Integer statusId) {
        try {
            Status updatedStatus = circulationService.returnBook(statusId);
            return ResponseEntity.ok(updatedStatus);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}