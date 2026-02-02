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

@Service
public class CirculationService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StatusRepository statusRepository;

    @Transactional // Ensures this entire method is one single database transaction
    public Status issueBook(Long bookId, String studentPrn) {
        // 1. Find the book and student, or throw an error if they don't exist.
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + bookId));
        
        Student student = studentRepository.findById(studentPrn)
                .orElseThrow(() -> new RuntimeException("Student not found with PRN: " + studentPrn));

        // 2. Check the business rule: Is the book available?
        if (!book.getAvailability()) {
            throw new RuntimeException("Book is currently issued and not available.");
        }

        // 3. Update the book's status and save it.
        book.setAvailability(false);
        bookRepository.save(book);

        // 4. Create a new Status record to track the loan.
        Status newStatus = new Status();
        newStatus.setBook(book);
        newStatus.setStudent(student);
        newStatus.setIssueDate(LocalDate.now());
        // We can leave returnDate and fine as null/default for now.
        
        // 5. Save the new status record and return it.
        return statusRepository.save(newStatus);
    }
    
    // We will add the returnBook method later.
    // Add this method to your existing CirculationService class

@Transactional
public Status returnBook(Integer statusId) {
    // 1. Find the specific loan record (Status) by its ID.
    Status status = statusRepository.findById(statusId)
            .orElseThrow(() -> new RuntimeException("Loan record not found with id: " + statusId));

    // 2. Check if the book has already been returned.
    if (status.getReturnDate() != null) {
        throw new RuntimeException("This book has already been returned.");
    }

    // 3. Get the associated book from the status record.
    Book book = status.getBook();

    // 4. Update the book's availability and the status record's return date.
    book.setAvailability(true);
    status.setReturnDate(LocalDate.now());

    // 5. Save the changes to the database.
    bookRepository.save(book);
    return statusRepository.save(status);
}
}