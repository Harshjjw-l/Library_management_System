package com.lms.library_management_system.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Matches Python RecommendedBook schema.
 * One ranked book inside the RecommendationResponse.
 */
public class RecommendedBookDto {

    private Long id;
    private String title;

    @JsonProperty("similarity_score")
    private double similarityScore;

    @JsonProperty("matched_topics")
    private List<String> matchedTopics;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public double getSimilarityScore() { return similarityScore; }
    public void setSimilarityScore(double similarityScore) { this.similarityScore = similarityScore; }
    public List<String> getMatchedTopics() { return matchedTopics; }
    public void setMatchedTopics(List<String> matchedTopics) { this.matchedTopics = matchedTopics; }
}
