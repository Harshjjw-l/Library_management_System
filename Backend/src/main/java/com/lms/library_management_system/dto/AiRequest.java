package com.lms.library_management_system.dto;

import java.util.List;

/**
 * Request body accepted by the Java AI controller endpoints.
 * The caller provides a courseId to look up syllabus topics from the database,
 * and optionally a list of already-selected book IDs and a top_n limit.
 */
public class AiRequest {

    private String courseId;
    private List<Long> selectedBookIds;
    private Integer topN;

    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }
    public List<Long> getSelectedBookIds() { return selectedBookIds; }
    public void setSelectedBookIds(List<Long> selectedBookIds) { this.selectedBookIds = selectedBookIds; }
    public Integer getTopN() { return topN; }
    public void setTopN(Integer topN) { this.topN = topN; }
}
