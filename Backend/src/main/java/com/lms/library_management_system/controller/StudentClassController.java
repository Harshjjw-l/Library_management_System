package com.lms.library_management_system.controller;

import com.lms.library_management_system.model.StudentClass;
import com.lms.library_management_system.service.StudentClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/classes")
public class StudentClassController {

    @Autowired
    private StudentClassService studentClassService;

    @PostMapping
    public ResponseEntity<StudentClass> addStudentClass(@RequestBody StudentClass studentClass) {
        return ResponseEntity.ok(studentClassService.addStudentClass(studentClass));
    }

    @GetMapping
    public ResponseEntity<List<StudentClass>> getAllStudentClasses() {
        return ResponseEntity.ok(studentClassService.findAllStudentClasses());
    }

    // Add getById, update, delete endpoints here...
}