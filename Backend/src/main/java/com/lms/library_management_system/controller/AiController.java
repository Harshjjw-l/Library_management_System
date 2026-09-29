package com.lms.library_management_system.controller;

import com.lms.library_management_system.dto.*;
import com.lms.library_management_system.service.AiRecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST endpoints that proxy AI requests to the Python FastAPI service.
 * The caller sends a courseId (and optionally selectedBookIds / topN).
 * This controller fetches data from MySQL and forwards it as JSON to Python.
 */
@RestController
@RequestMapping("/api/ai")
public class AiController {

    @Autowired
    private AiRecommendationService aiService;

    /**
     * POST /api/ai/recommend
     * Body: { "courseId": "CS101", "topN": 5 }
     */
    @PostMapping("/recommend")
    public ResponseEntity<?> recommend(@RequestBody AiRequest request) {
        try {
            validateCourseId(request.getCourseId());
            RecommendationResponseDto response = aiService.getRecommendations(
                    request.getCourseId(), request.getTopN());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * POST /api/ai/coverage
     * Body: { "courseId": "CS101" }
     */
    @PostMapping("/coverage")
    public ResponseEntity<?> coverage(@RequestBody AiRequest request) {
        try {
            validateCourseId(request.getCourseId());
            CoverageResponseDto response = aiService.getCoverage(request.getCourseId());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * POST /api/ai/supplementary
     * Body: { "courseId": "CS101", "selectedBookIds": [1, 3], "topN": 5 }
     */
    @PostMapping("/supplementary")
    public ResponseEntity<?> supplementary(@RequestBody AiRequest request) {
        try {
            validateCourseId(request.getCourseId());
            SupplementaryResponseDto response = aiService.getSupplementary(
                    request.getCourseId(), request.getSelectedBookIds(), request.getTopN());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    private void validateCourseId(String courseId) {
        if (courseId == null || courseId.isBlank()) {
            throw new IllegalArgumentException("courseId is required.");
        }
    }
}
