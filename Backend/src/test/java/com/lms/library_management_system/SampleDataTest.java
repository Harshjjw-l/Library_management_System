package com.lms.library_management_system;

import com.lms.library_management_system.model.Book;
import com.lms.library_management_system.model.Course;
import com.lms.library_management_system.model.Topic;
import com.lms.library_management_system.repository.BookRepository;
import com.lms.library_management_system.repository.CourseRepository;
import com.lms.library_management_system.repository.TopicRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Local-only integration test that populates sample topic, book, and course syllabus data
 * inside an isolated H2 in-memory database to verify entity relationships and JPA queries.
 */
@DataJpaTest
class SampleDataTest {

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Test
    @DisplayName("Verify Topic entity persistence and many-to-many relationships with Book and Course")
    void testSampleTopicAndSyllabusData() {
        // 1. Create Sample Topics
        Topic topicAlgo = topicRepository.save(new Topic("Algorithms"));
        Topic topicDs = topicRepository.save(new Topic("Data Structures"));
        Topic topicAi = topicRepository.save(new Topic("Artificial Intelligence"));

        assertNotNull(topicAlgo.getId());
        assertNotNull(topicDs.getId());

        // 2. Create Sample Book with Topics
        Book book = new Book();
        book.setName("Introduction to Algorithms");
        book.setAuthName("Cormen et al.");
        book.setAvailability(true);
        book.setNoCopies(5);
        book.setTopics(Set.of(topicAlgo, topicDs));

        Book savedBook = bookRepository.save(book);
        assertNotNull(savedBook.getId());
        assertEquals(2, savedBook.getTopics().size());

        // 3. Create Sample Course with Syllabus Topics
        Course course = new Course();
        course.setCourseId("CS102");
        course.setName("Data Structures & Algorithms");
        course.setCredits(4);
        course.setSyllabusTopics(Set.of(topicAlgo, topicDs, topicAi));

        Course savedCourse = courseRepository.save(course);
        assertEquals("CS102", savedCourse.getCourseId());
        assertEquals(3, savedCourse.getSyllabusTopics().size());

        // 4. Verify Database Queries
        Optional<Course> fetchedCourse = courseRepository.findById("CS102");
        assertTrue(fetchedCourse.isPresent());
        assertTrue(fetchedCourse.get().getSyllabusTopics().stream().anyMatch(t -> t.getName().equals("Algorithms")));

        Optional<Book> fetchedBook = bookRepository.findById(savedBook.getId());
        assertTrue(fetchedBook.isPresent());
        assertEquals("Introduction to Algorithms", fetchedBook.get().getName());
    }
}
