package com.lms.library_management_system.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Matches Python SupplementaryBook schema.
 * One supplementary book inside the SupplementaryResponse.
 */
public class SupplementaryBookDto {

    private Long id;
    private String title;

    @JsonProperty("new_topics_covered")
    private List<String> newTopicsCovered;

    @JsonProperty("new_topics_count")
    private int newTopicsCount;

    private String explanation;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public List<String> getNewTopicsCovered() { return newTopicsCovered; }
    public void setNewTopicsCovered(List<String> newTopicsCovered) { this.newTopicsCovered = newTopicsCovered; }
    public int getNewTopicsCount() { return newTopicsCount; }
    public void setNewTopicsCount(int newTopicsCount) { this.newTopicsCount = newTopicsCount; }
    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
}
