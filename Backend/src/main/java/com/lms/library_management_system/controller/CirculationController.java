package com.lms.library_management_system.controller;

import com.lms.library_management_system.model.Status;
import com.lms.library_management_system.model.Student;
import com.lms.library_management_system.repository.StudentRepository;
import com.lms.library_management_system.service.CirculationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/circulation")
public class CirculationController {

    @Autowired
    private CirculationService circulationService;

    @Autowired
    private StudentRepository studentRepository;

    // =========================
    // ISSUE BOOK
    // =========================
    @PostMapping("/issue")
    public ResponseEntity<?> issueBook(
            @RequestParam Long bookId,
            @RequestParam String studentPrn) {

        try {
            Status newStatus = circulationService.issueBook(bookId, studentPrn);
            return ResponseEntity.ok(newStatus);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // =========================
    // RETURN BOOK
    // =========================
    @PostMapping("/return")
    public ResponseEntity<?> returnBook(
            @RequestParam Integer statusId) {

        try {
            Status updatedStatus = circulationService.returnBook(statusId);
            return ResponseEntity.ok(updatedStatus);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // =========================
    // GET ALL CIRCULATION RECORDS OF A STUDENT
    // =========================
    @GetMapping("/student/{prn}")
    public ResponseEntity<?> getStudentCirculation(
            @PathVariable String prn) {

        try {
            Student student = studentRepository.findById(prn)
                    .orElseThrow(() ->
                            new RuntimeException("Student not found with PRN: " + prn));

            List<Status> records =
                    circulationService.getStudentCirculation(student);

            return ResponseEntity.ok(records);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // =========================
    // GET CURRENTLY ISSUED BOOKS OF A STUDENT
    // =========================
    @GetMapping("/student/{prn}/issued")
    public ResponseEntity<?> getIssuedBooks(
            @PathVariable String prn) {

        try {
            Student student = studentRepository.findById(prn)
                    .orElseThrow(() ->
                            new RuntimeException("Student not found with PRN: " + prn));

            List<Status> records =
                    circulationService.getIssuedBooks(student);

            return ResponseEntity.ok(records);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}