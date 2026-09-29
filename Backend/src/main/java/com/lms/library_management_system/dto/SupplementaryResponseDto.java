package com.lms.library_management_system.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Matches Python SupplementaryResponse schema.
 * Returned from POST /supplementary on the AI service.
 */
public class SupplementaryResponseDto {

    @JsonProperty("syllabus_topics")
    private List<String> syllabusTopics;

    @JsonProperty("already_covered_topics")
    private List<String> alreadyCoveredTopics;

    private List<SupplementaryBookDto> recommendations;

    @JsonProperty("remaining_uncovered_topics")
    private List<String> remainingUncoveredTopics;

    @JsonProperty("coverage_percentage_before")
    private double coveragePercentageBefore;

    @JsonProperty("coverage_percentage_after")
    private double coveragePercentageAfter;

    private String explanation;

    public List<String> getSyllabusTopics() { return syllabusTopics; }
    public void setSyllabusTopics(List<String> syllabusTopics) { this.syllabusTopics = syllabusTopics; }
    public List<String> getAlreadyCoveredTopics() { return alreadyCoveredTopics; }
    public void setAlreadyCoveredTopics(List<String> alreadyCoveredTopics) { this.alreadyCoveredTopics = alreadyCoveredTopics; }
    public List<SupplementaryBookDto> getRecommendations() { return recommendations; }
    public void setRecommendations(List<SupplementaryBookDto> recommendations) { this.recommendations = recommendations; }
    public List<String> getRemainingUncoveredTopics() { return remainingUncoveredTopics; }
    public void setRemainingUncoveredTopics(List<String> remainingUncoveredTopics) { this.remainingUncoveredTopics = remainingUncoveredTopics; }
    public double getCoveragePercentageBefore() { return coveragePercentageBefore; }
    public void setCoveragePercentageBefore(double coveragePercentageBefore) { this.coveragePercentageBefore = coveragePercentageBefore; }
    public double getCoveragePercentageAfter() { return coveragePercentageAfter; }
    public void setCoveragePercentageAfter(double coveragePercentageAfter) { this.coveragePercentageAfter = coveragePercentageAfter; }
    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
}
