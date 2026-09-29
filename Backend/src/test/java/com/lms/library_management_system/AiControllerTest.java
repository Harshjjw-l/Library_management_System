package com.lms.library_management_system;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lms.library_management_system.config.AiServiceConfig;
import com.lms.library_management_system.controller.AiController;
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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AiControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private org.springframework.web.client.RestOperations aiRestTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Course sampleCourse;
    private Book sampleBook;

    @BeforeEach
    void setUp() {
        AiServiceConfig config = new AiServiceConfig();
        ReflectionTestUtils.setField(config, "aiServiceBaseUrl", "http://localhost:8000");

        AiRecommendationService aiService = new AiRecommendationService();
        ReflectionTestUtils.setField(aiService, "bookRepository", bookRepository);
        ReflectionTestUtils.setField(aiService, "courseRepository", courseRepository);
        ReflectionTestUtils.setField(aiService, "aiRestTemplate", aiRestTemplate);
        ReflectionTestUtils.setField(aiService, "aiServiceConfig", config);

        AiController aiController = new AiController();
        ReflectionTestUtils.setField(aiController, "aiService", aiService);
        this.mockMvc = MockMvcBuilders.standaloneSetup(aiController).build();

        Topic topic1 = new Topic("Software Engineering");
        topic1.setId(1L);

        sampleCourse = new Course();
        sampleCourse.setCourseId("CS101");
        sampleCourse.setName("Intro to SE");
        sampleCourse.setSyllabusTopics(Set.of(topic1));

        sampleBook = new Book();
        sampleBook.setId(1L);
        sampleBook.setName("Clean Code");
        sampleBook.setAuthName("Robert C. Martin");
        sampleBook.setTopics(Set.of(topic1));
    }

    @Test
    void testRecommendEndpoint_Success() throws Exception {
        when(courseRepository.findById("CS101")).thenReturn(Optional.of(sampleCourse));
        when(bookRepository.findAll()).thenReturn(List.of(sampleBook));

        RecommendationResponseDto mockResponse = new RecommendationResponseDto();
        RecommendedBookDto book = new RecommendedBookDto();
        book.setId(1L);
        book.setTitle("Clean Code");
        book.setSimilarityScore(0.88);
        book.setMatchedTopics(List.of("Software Engineering"));
        mockResponse.setRecommendations(List.of(book));

        when(aiRestTemplate.postForObject(eq("http://localhost:8000/recommend"), any(), eq(RecommendationResponseDto.class)))
                .thenReturn(mockResponse);

        AiRequest request = new AiRequest();
        request.setCourseId("CS101");
        request.setTopN(5);

        mockMvc.perform(post("/api/ai/recommend")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recommendations[0].title").value("Clean Code"))
                .andExpect(jsonPath("$.recommendations[0].similarity_score").value(0.88));
    }

    @Test
    void testCoverageEndpoint_Success() throws Exception {
        when(courseRepository.findById("CS101")).thenReturn(Optional.of(sampleCourse));
        when(bookRepository.findAll()).thenReturn(List.of(sampleBook));

        CoverageResponseDto mockResponse = new CoverageResponseDto();
        mockResponse.setCoveredCount(1);
        mockResponse.setTotalCount(1);
        mockResponse.setCoveragePercentage(100.0);
        mockResponse.setCoveredTopics(List.of("Software Engineering"));

        when(aiRestTemplate.postForObject(eq("http://localhost:8000/coverage"), any(), eq(CoverageResponseDto.class)))
                .thenReturn(mockResponse);

        AiRequest request = new AiRequest();
        request.setCourseId("CS101");

        mockMvc.perform(post("/api/ai/coverage")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.coverage_percentage").value(100.0))
                .andExpect(jsonPath("$.covered_count").value(1));
    }

    @Test
    void testSupplementaryEndpoint_Success() throws Exception {
        when(courseRepository.findById("CS101")).thenReturn(Optional.of(sampleCourse));
        when(bookRepository.findAll()).thenReturn(List.of(sampleBook));

        SupplementaryResponseDto mockResponse = new SupplementaryResponseDto();
        mockResponse.setCoveragePercentageBefore(50.0);
        mockResponse.setCoveragePercentageAfter(100.0);
        
        SupplementaryBookDto sup = new SupplementaryBookDto();
        sup.setId(2L);
        sup.setTitle("Design Patterns");
        sup.setNewTopicsCovered(List.of("Design Patterns"));
        mockResponse.setRecommendations(List.of(sup));

        when(aiRestTemplate.postForObject(eq("http://localhost:8000/supplementary"), any(), eq(SupplementaryResponseDto.class)))
                .thenReturn(mockResponse);

        AiRequest request = new AiRequest();
        request.setCourseId("CS101");

        mockMvc.perform(post("/api/ai/supplementary")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.coverage_percentage_after").value(100.0))
                .andExpect(jsonPath("$.recommendations[0].title").value("Design Patterns"));
    }

    @Test
    void testMissingCourseId_ReturnsBadRequest() throws Exception {
        AiRequest request = new AiRequest(); // courseId null

        mockMvc.perform(post("/api/ai/recommend")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("courseId is required."));
    }
}
