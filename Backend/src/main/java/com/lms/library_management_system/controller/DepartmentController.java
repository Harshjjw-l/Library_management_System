package com.lms.library_management_system.controller;

import com.lms.library_management_system.model.Department;
import com.lms.library_management_system.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @PostMapping
    public ResponseEntity<Department> addDepartment(@RequestBody Department department) {
        return ResponseEntity.ok(departmentService.addDepartment(department));
    }

    @GetMapping
    public ResponseEntity<List<Department>> getAllDepartments() {
        return ResponseEntity.ok(departmentService.findAllDepartments());
    }

    @GetMapping("/{deptNo}")
    public ResponseEntity<Department> getDepartmentById(@PathVariable String deptNo) {
        return ResponseEntity.ok(departmentService.findDepartmentById(deptNo));
    }

    @PutMapping("/{deptNo}")
    public ResponseEntity<Department> updateDepartment(@PathVariable String deptNo, @RequestBody Department departmentDetails) {
        return ResponseEntity.ok(departmentService.updateDepartment(deptNo, departmentDetails));
    }

    @DeleteMapping("/{deptNo}")
    public ResponseEntity<Void> deleteDepartment(@PathVariable String deptNo) {
        departmentService.deleteDepartment(deptNo);
        return ResponseEntity.noContent().build();
    }
}