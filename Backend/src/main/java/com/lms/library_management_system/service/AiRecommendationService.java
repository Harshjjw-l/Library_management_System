package com.lms.library_management_system.service;

import com.lms.library_management_system.config.AiServiceConfig;
import com.lms.library_management_system.dto.*;
import com.lms.library_management_system.model.Book;
import com.lms.library_management_system.model.Course;
import com.lms.library_management_system.model.Topic;
import com.lms.library_management_system.repository.BookRepository;
import com.lms.library_management_system.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Fetches books and course syllabus topics from the database, maps them into
 * the JSON format expected by the Python AI service, calls the service, and
 * returns the response DTOs.
 *
 * The Python service remains fully independent of MySQL; all data is sent
 * in the HTTP request body.
 */
@Service
public class AiRecommendationService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private org.springframework.web.client.RestOperations aiRestTemplate;

    @Autowired
    private AiServiceConfig aiServiceConfig;

    // --- Public methods called by AiController ---

    /**
     * Rank all library books by TF-IDF cosine similarity to a course's syllabus topics.
     */
    public RecommendationResponseDto getRecommendations(String courseId, Integer topN) {
        Course course = findCourseOrThrow(courseId);
        List<String> syllabusTopics = extractTopicNames(course);
        validateTopicsNotEmpty(syllabusTopics, courseId);

        List<BookInputDto> bookDtos = buildBookInputList();

        RecommendationRequestDto request = new RecommendationRequestDto();
        request.setSyllabusTopics(syllabusTopics);
        request.setBooks(bookDtos);
        request.setTopN(topN != null && topN >= 1 ? topN : 5);

        return callAiService("/recommend", request, RecommendationResponseDto.class);
    }

    /**
     * Calculate exact-match syllabus topic coverage for all library books.
     */
    public CoverageResponseDto getCoverage(String courseId) {
        Course course = findCourseOrThrow(courseId);
        List<String> syllabusTopics = extractTopicNames(course);
        validateTopicsNotEmpty(syllabusTopics, courseId);

        List<BookInputDto> bookDtos = buildBookInputList();

        CoverageRequestDto request = new CoverageRequestDto();
        request.setSyllabusTopics(syllabusTopics);
        request.setBooks(bookDtos);

        return callAiService("/coverage", request, CoverageResponseDto.class);
    }

    /**
     * Recommend supplementary books that cover still-uncovered syllabus topics.
     */
    public SupplementaryResponseDto getSupplementary(String courseId, List<Long> selectedBookIds, Integer topN) {
        Course course = findCourseOrThrow(courseId);
        List<String> syllabusTopics = extractTopicNames(course);
        validateTopicsNotEmpty(syllabusTopics, courseId);

        List<BookInputDto> bookDtos = buildBookInputList();

        SupplementaryRequestDto request = new SupplementaryRequestDto();
        request.setSyllabusTopics(syllabusTopics);
        request.setBooks(bookDtos);
        request.setSelectedBookIds(selectedBookIds);
        request.setTopN(topN != null && topN >= 1 ? topN : 5);

        return callAiService("/supplementary", request, SupplementaryResponseDto.class);
    }

    // --- Internal helpers ---

    private Course findCourseOrThrow(String courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found with id: " + courseId));
    }

    private List<String> extractTopicNames(Course course) {
        Set<Topic> topics = course.getSyllabusTopics();
        if (topics == null) {
            return Collections.emptyList();
        }
        return topics.stream()
                .map(Topic::getName)
                .collect(Collectors.toList());
    }

    private void validateTopicsNotEmpty(List<String> topics, String courseId) {
        if (topics.isEmpty()) {
            throw new IllegalArgumentException(
                    "Course '" + courseId + "' has no syllabus topics configured.");
        }
    }

    /**
     * Build the full book catalog with topics, matching the Python BookInput schema.
     * Books without topics are still included (the AI service handles empty topic lists).
     */
    private List<BookInputDto> buildBookInputList() {
        List<Book> allBooks = bookRepository.findAll();
        List<BookInputDto> dtos = new ArrayList<>(allBooks.size());

        for (Book book : allBooks) {
            List<String> topicNames;
            if (book.getTopics() != null) {
                topicNames = book.getTopics().stream()
                        .map(Topic::getName)
                        .collect(Collectors.toList());
            } else {
                topicNames = Collections.emptyList();
            }
            dtos.add(new BookInputDto(book.getId(), book.getName(), topicNames));
        }
        return dtos;
    }

    /**
     * POST a request to the Python AI service and deserialize the response.
     * Wraps RestClientException into a RuntimeException with a clear message.
     */
    private <T> T callAiService(String path, Object request, Class<T> responseType) {
        String url = aiServiceConfig.getAiServiceBaseUrl() + path;
        try {
            return aiRestTemplate.postForObject(url, request, responseType);
        } catch (RestClientException e) {
            throw new RuntimeException(
                    "AI service unavailable or returned an error at " + url + ": " + e.getMessage(), e);
        }
    }
}
