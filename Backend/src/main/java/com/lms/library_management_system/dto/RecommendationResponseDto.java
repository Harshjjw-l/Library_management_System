package com.lms.library_management_system.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Matches Python RecommendationResponse schema.
 * Returned from POST /recommend on the AI service.
 */
public class RecommendationResponseDto {

    private List<RecommendedBookDto> recommendations;

    @JsonProperty("score_note")
    private String scoreNote;

    public List<RecommendedBookDto> getRecommendations() { return recommendations; }
    public void setRecommendations(List<RecommendedBookDto> recommendations) { this.recommendations = recommendations; }
    public String getScoreNote() { return scoreNote; }
    public void setScoreNote(String scoreNote) { this.scoreNote = scoreNote; }
}
