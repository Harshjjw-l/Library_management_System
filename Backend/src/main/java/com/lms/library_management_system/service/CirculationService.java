package com.lms.library_management_system.service;

import com.lms.library_management_system.model.Book;
import com.lms.library_management_system.model.Student;
import com.lms.library_management_system.model.Status;
import com.lms.library_management_system.repository.BookRepository;
import com.lms.library_management_system.repository.StudentRepository;
import com.lms.library_management_system.repository.StatusRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class CirculationService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StatusRepository statusRepository;

    // =========================
    // ISSUE BOOK
    // =========================
    @Transactional
    public Status issueBook(Long bookId, String studentPrn) {

        // 1. Find the book and student
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() ->
                        new RuntimeException("Book not found with id: " + bookId));

        Student student = studentRepository.findById(studentPrn)
                .orElseThrow(() ->
                        new RuntimeException("Student not found with PRN: " + studentPrn));

        // 2. Check availability
        if (!book.getAvailability()) {
            throw new RuntimeException(
                    "Book is currently issued and not available.");
        }

        // 3. Mark book as unavailable
        book.setAvailability(false);
        bookRepository.save(book);

        // 4. Create circulation record
        Status newStatus = new Status();
        newStatus.setBook(book);
        newStatus.setStudent(student);
        newStatus.setIssueDate(LocalDate.now());

        // 5. Save circulation record
        return statusRepository.save(newStatus);
    }

    // =========================
    // RETURN BOOK
    // =========================
    @Transactional
    public Status returnBook(Integer statusId) {

        // 1. Find circulation record
        Status status = statusRepository.findById(statusId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Loan record not found with id: " + statusId));

        // 2. Check if already returned
        if (status.getReturnDate() != null) {
            throw new RuntimeException(
                    "This book has already been returned.");
        }

        // 3. Get associated book
        Book book = status.getBook();

        // 4. Update book availability
        book.setAvailability(true);

        // 5. Set return date
        status.setReturnDate(LocalDate.now());

        // 6. Save changes
        bookRepository.save(book);

        return statusRepository.save(status);
    }

    // =========================
    // GET ALL CIRCULATION RECORDS
    // OF A STUDENT
    // =========================
    public List<Status> getStudentCirculation(Student student) {
        return statusRepository.findByStudent(student);
    }

    // =========================
    // GET CURRENTLY ISSUED BOOKS
    // OF A STUDENT
    // =========================
    public List<Status> getIssuedBooks(Student student) {
        return statusRepository.findByStudentAndReturnDateIsNull(student);
    }
}
