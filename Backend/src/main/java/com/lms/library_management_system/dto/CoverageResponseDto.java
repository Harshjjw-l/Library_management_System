package com.lms.library_management_system.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Matches Python CoverageResponse schema.
 * Returned from POST /coverage on the AI service.
 */
public class CoverageResponseDto {

    @JsonProperty("syllabus_topics")
    private List<String> syllabusTopics;

    @JsonProperty("covered_topics")
    private List<String> coveredTopics;

    @JsonProperty("missing_topics")
    private List<String> missingTopics;

    @JsonProperty("covered_count")
    private int coveredCount;

    @JsonProperty("total_count")
    private int totalCount;

    @JsonProperty("coverage_percentage")
    private double coveragePercentage;

    private String explanation;

    public List<String> getSyllabusTopics() { return syllabusTopics; }
    public void setSyllabusTopics(List<String> syllabusTopics) { this.syllabusTopics = syllabusTopics; }
    public List<String> getCoveredTopics() { return coveredTopics; }
    public void setCoveredTopics(List<String> coveredTopics) { this.coveredTopics = coveredTopics; }
    public List<String> getMissingTopics() { return missingTopics; }
    public void setMissingTopics(List<String> missingTopics) { this.missingTopics = missingTopics; }
    public int getCoveredCount() { return coveredCount; }
    public void setCoveredCount(int coveredCount) { this.coveredCount = coveredCount; }
    public int getTotalCount() { return totalCount; }
    public void setTotalCount(int totalCount) { this.totalCount = totalCount; }
    public double getCoveragePercentage() { return coveragePercentage; }
    public void setCoveragePercentage(double coveragePercentage) { this.coveragePercentage = coveragePercentage; }
    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
}
