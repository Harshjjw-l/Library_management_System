package com.lms.library_management_system.service;

import com.lms.library_management_system.model.Student;
import com.lms.library_management_system.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    public Student addStudent(Student student) {
        return studentRepository.save(student);
    }

    public List<Student> findAllStudents() {
        return studentRepository.findAll();
    }

    public Student findStudentById(String prnNo) {
        return studentRepository.findById(prnNo)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + prnNo));
    }
    
    // You can add update and delete methods here...
}