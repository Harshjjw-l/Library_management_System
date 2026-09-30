package com.lms.library_management_system.repository;

import com.lms.library_management_system.model.Status;
import com.lms.library_management_system.model.Student;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatusRepository extends JpaRepository<Status, Integer> {

    // All circulation records for a student
    List<Status> findByStudent(Student student);

    // Currently issued books
    List<Status> findByStudentAndReturnDateIsNull(Student student);
}