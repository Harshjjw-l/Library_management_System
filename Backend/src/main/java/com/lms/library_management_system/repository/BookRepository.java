package com.lms.library_management_system.repository;

import com.lms.library_management_system.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}