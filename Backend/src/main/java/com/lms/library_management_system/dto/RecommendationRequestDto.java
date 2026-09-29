package com.lms.library_management_system.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Matches Python RecommendationRequest schema.
 * Sent as the JSON body to POST /recommend on the AI service.
 */
public class RecommendationRequestDto {

    @JsonProperty("syllabus_topics")
    private List<String> syllabusTopics;

    private List<BookInputDto> books;

    @JsonProperty("top_n")
    private int topN = 5;

    public List<String> getSyllabusTopics() { return syllabusTopics; }
    public void setSyllabusTopics(List<String> syllabusTopics) { this.syllabusTopics = syllabusTopics; }
    public List<BookInputDto> getBooks() { return books; }
    public void setBooks(List<BookInputDto> books) { this.books = books; }
    public int getTopN() { return topN; }
    public void setTopN(int topN) { this.topN = topN; }
}
