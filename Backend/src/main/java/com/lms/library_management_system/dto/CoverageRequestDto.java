package com.lms.library_management_system.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Matches Python CoverageRequest schema.
 * Sent as the JSON body to POST /coverage on the AI service.
 */
public class CoverageRequestDto {

    @JsonProperty("syllabus_topics")
    private List<String> syllabusTopics;

    private List<BookInputDto> books;

    public List<String> getSyllabusTopics() { return syllabusTopics; }
    public void setSyllabusTopics(List<String> syllabusTopics) { this.syllabusTopics = syllabusTopics; }
    public List<BookInputDto> getBooks() { return books; }
    public void setBooks(List<BookInputDto> books) { this.books = books; }
}
