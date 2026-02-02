package com.lms.library_management_system.service;

import com.lms.library_management_system.model.StudentClass;
import com.lms.library_management_system.repository.StudentClassRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class StudentClassService {

    @Autowired
    private StudentClassRepository studentClassRepository;

    public StudentClass addStudentClass(StudentClass studentClass) {
        return studentClassRepository.save(studentClass);
    }

    public List<StudentClass> findAllStudentClasses() {
        return studentClassRepository.findAll();
    }

    public StudentClass findStudentClassById(String roomNo) {
        return studentClassRepository.findById(roomNo)
                .orElseThrow(() -> new RuntimeException("Class not found with id: " + roomNo));
    }
    
    // You can add update and delete methods here following the same pattern...
}