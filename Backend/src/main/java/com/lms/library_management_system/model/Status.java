package com.lms.library_management_system.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "status")
public class Status {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private LocalDate issueDate;
    private LocalDate returnDate;
    private BigDecimal fine;
    private String review;

    @ManyToOne
    @JoinColumn(name = "prn_no")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    // --- Getters and Setters ---

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }
    public BigDecimal getFine() { return fine; }
    public void setFine(BigDecimal fine) { this.fine = fine; }
    public String getReview() { return review; }
    public void setReview(String review) { this.review = review; }
    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }
    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }
}