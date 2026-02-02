package com.lms.library_management_system.controller;

import com.lms.library_management_system.model.Staff;
import com.lms.library_management_system.service.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/staff")
public class StaffController {

    @Autowired
    private StaffService staffService;

    @PostMapping
    public ResponseEntity<Staff> addStaff(@RequestBody Staff staff) {
        return ResponseEntity.ok(staffService.addStaff(staff));
    }

    @GetMapping
    public ResponseEntity<List<Staff>> getAllStaff() {
        return ResponseEntity.ok(staffService.findAllStaff());
    }
    
    // Add getById, update, delete endpoints here...
}