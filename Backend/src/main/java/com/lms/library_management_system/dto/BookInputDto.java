package com.lms.library_management_system.dto;

import java.util.List;

/**
 * Matches Python BookInput schema: id, title, topics.
 * Used as the book element inside all AI service request payloads.
 */
public class BookInputDto {

    private Long id;
    private String title;
    private List<String> topics;

    public BookInputDto() {}

    public BookInputDto(Long id, String title, List<String> topics) {
        this.id = id;
        this.title = title;
        this.topics = topics;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public List<String> getTopics() { return topics; }
    public void setTopics(List<String> topics) { this.topics = topics; }
}
