package com.lms.library_management_system.service;

import com.lms.library_management_system.model.Book;
import com.lms.library_management_system.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    public Book addBook(Book book) {
        return bookRepository.save(book);
    }

    public List<Book> findAllBooks() {
        return bookRepository.findAll();
    }

    public Book findBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));
    }

    public Book updateBook(Long id, Book bookDetails) {
        Book existingBook = findBookById(id);
        existingBook.setName(bookDetails.getName());
        existingBook.setAuthName(bookDetails.getAuthName());
        existingBook.setNoCopies(bookDetails.getNoCopies());
        existingBook.setAvailability(bookDetails.getAvailability());
        return bookRepository.save(existingBook);
    }

    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new RuntimeException("Book not found with id: " + id);
        }
        bookRepository.deleteById(id);
    }
}