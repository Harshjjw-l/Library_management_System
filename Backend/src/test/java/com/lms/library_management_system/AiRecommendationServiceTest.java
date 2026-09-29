package com.lms.library_management_system;

import com.lms.library_management_system.config.AiServiceConfig;
import com.lms.library_management_system.dto.*;
import com.lms.library_management_system.model.Book;
import com.lms.library_management_system.model.Course;
import com.lms.library_management_system.model.Topic;
import com.lms.library_management_system.repository.BookRepository;
import com.lms.library_management_system.repository.CourseRepository;
import com.lms.library_management_system.service.AiRecommendationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AiRecommendationServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private org.springframework.web.client.RestOperations aiRestTemplate;

    private AiServiceConfig aiServiceConfig = new AiServiceConfig();
    private AiRecommendationService aiRecommendationService;

    private Course sampleCourse;
    private Book sampleBook;

    @BeforeEach
    void setUp() {
        org.springframework.test.util.ReflectionTestUtils.setField(aiServiceConfig, "aiServiceBaseUrl", "http://localhost:8000");

        aiRecommendationService = new AiRecommendationService();
        org.springframework.test.util.ReflectionTestUtils.setField(aiRecommendationService, "bookRepository", bookRepository);
        org.springframework.test.util.ReflectionTestUtils.setField(aiRecommendationService, "courseRepository", courseRepository);
        org.springframework.test.util.ReflectionTestUtils.setField(aiRecommendationService, "aiRestTemplate", aiRestTemplate);
        org.springframework.test.util.ReflectionTestUtils.setField(aiRecommendationService, "aiServiceConfig", aiServiceConfig);

        Topic topic1 = new Topic("Algorithms");
        topic1.setId(1L);
        topic1.setId(1L);
        Topic topic2 = new Topic("Data Structures");
        topic2.setId(2L);

        sampleCourse = new Course();
        sampleCourse.setCourseId("CS101");
        sampleCourse.setName("Intro to CS");
        sampleCourse.setSyllabusTopics(Set.of(topic1, topic2));

        sampleBook = new Book();
        sampleBook.setId(10L);
        sampleBook.setName("Introduction to Algorithms");
        sampleBook.setAuthName("Cormen");
        sampleBook.setTopics(Set.of(topic1));
    }

    @Test
    void testGetRecommendations_Success() {
        when(courseRepository.findById("CS101")).thenReturn(Optional.of(sampleCourse));
        when(bookRepository.findAll()).thenReturn(List.of(sampleBook));

        RecommendationResponseDto expectedResponse = new RecommendationResponseDto();
        RecommendedBookDto recBook = new RecommendedBookDto();
        recBook.setId(10L);
        recBook.setTitle("Introduction to Algorithms");
        recBook.setSimilarityScore(0.95);
        recBook.setMatchedTopics(List.of("Algorithms"));
        expectedResponse.setRecommendations(List.of(recBook));

        when(aiRestTemplate.postForObject(eq("http://localhost:8000/recommend"), any(), eq(RecommendationResponseDto.class)))
                .thenReturn(expectedResponse);

        RecommendationResponseDto result = aiRecommendationService.getRecommendations("CS101", 5);

        assertNotNull(result);
        assertEquals(1, result.getRecommendations().size());
        assertEquals("Introduction to Algorithms", result.getRecommendations().get(0).getTitle());
        assertEquals(0.95, result.getRecommendations().get(0).getSimilarityScore());
    }

    @Test
    void testGetCoverage_Success() {
        when(courseRepository.findById("CS101")).thenReturn(Optional.of(sampleCourse));
        when(bookRepository.findAll()).thenReturn(List.of(sampleBook));

        CoverageResponseDto expectedResponse = new CoverageResponseDto();
        expectedResponse.setCoveredCount(1);
        expectedResponse.setTotalCount(2);
        expectedResponse.setCoveragePercentage(50.0);
        expectedResponse.setCoveredTopics(List.of("Algorithms"));
        expectedResponse.setMissingTopics(List.of("Data Structures"));

        when(aiRestTemplate.postForObject(eq("http://localhost:8000/coverage"), any(), eq(CoverageResponseDto.class)))
                .thenReturn(expectedResponse);

        CoverageResponseDto result = aiRecommendationService.getCoverage("CS101");

        assertNotNull(result);
        assertEquals(50.0, result.getCoveragePercentage());
        assertEquals(1, result.getCoveredCount());
    }

    @Test
    void testGetSupplementary_Success() {
        when(courseRepository.findById("CS101")).thenReturn(Optional.of(sampleCourse));
        when(bookRepository.findAll()).thenReturn(List.of(sampleBook));

        SupplementaryResponseDto expectedResponse = new SupplementaryResponseDto();
        expectedResponse.setCoveragePercentageBefore(50.0);
        expectedResponse.setCoveragePercentageAfter(100.0);
        
        SupplementaryBookDto supBook = new SupplementaryBookDto();
        supBook.setId(11L);
        supBook.setTitle("Data Structures in Java");
        supBook.setNewTopicsCovered(List.of("Data Structures"));
        expectedResponse.setRecommendations(List.of(supBook));

        when(aiRestTemplate.postForObject(eq("http://localhost:8000/supplementary"), any(), eq(SupplementaryResponseDto.class)))
                .thenReturn(expectedResponse);

        SupplementaryResponseDto result = aiRecommendationService.getSupplementary("CS101", List.of(10L), 5);

        assertNotNull(result);
        assertEquals(50.0, result.getCoveragePercentageBefore());
        assertEquals(100.0, result.getCoveragePercentageAfter());
        assertEquals(1, result.getRecommendations().size());
    }

    @Test
    void testCourseNotFound_ThrowsIllegalArgumentException() {
        when(courseRepository.findById("INVALID")).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                aiRecommendationService.getRecommendations("INVALID", 5));
        assertTrue(ex.getMessage().contains("Course not found"));
    }

    @Test
    void testEmptySyllabusTopics_ThrowsIllegalArgumentException() {
        Course emptyCourse = new Course();
        emptyCourse.setCourseId("EMPTY");
        emptyCourse.setSyllabusTopics(Collections.emptySet());

        when(courseRepository.findById("EMPTY")).thenReturn(Optional.of(emptyCourse));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                aiRecommendationService.getCoverage("EMPTY"));
        assertTrue(ex.getMessage().contains("has no syllabus topics"));
    }

    @Test
    void testAiServiceUnavailable_ThrowsRuntimeException() {
        when(courseRepository.findById("CS101")).thenReturn(Optional.of(sampleCourse));
        when(bookRepository.findAll()).thenReturn(List.of(sampleBook));

        when(aiRestTemplate.postForObject(anyString(), any(), any()))
                .thenThrow(new RestClientException("Connection refused"));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                aiRecommendationService.getRecommendations("CS101", 5));
        assertTrue(ex.getMessage().contains("AI service unavailable"));
    }
}
