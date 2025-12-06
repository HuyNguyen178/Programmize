package model;

import java.util.Date;

public class Chapter {
    private Integer chapterId;
    private Integer courseId;
    private String chapterName;
    private String description;
    private Integer orderIndex;
    private String status;
    private Date createdAt;
    private Date updatedAt;


    public Chapter() {
    }

    // Parameterized Constructor
    public Chapter(Integer chapterId, Integer courseId, String chapterName, String description,
                   Integer orderIndex, String status, Date createdAt, Date updatedAt) {
        this.chapterId = chapterId;
        this.courseId = courseId;
        this.chapterName = chapterName;
        this.description = description;
        this.orderIndex = orderIndex;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Constructor without ID (for creating new chapter)
    public Chapter(Integer courseId, String chapterName, String description,
                   Integer orderIndex, String status, Date createdAt, Date updatedAt) {
        this.courseId = courseId;
        this.chapterName = chapterName;
        this.description = description;
        this.orderIndex = orderIndex;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public Integer getChapterId() {
        return chapterId;
    }
    public void setChapterId(Integer chapterId) {
        this.chapterId = chapterId;
    }

    public Integer getCourseId() {
        return courseId;
    }
    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public String getChapterName() {
        return chapterName;
    }
    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }
    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    // toString method for debugging
    @Override
    public String toString() {
        return "Chapter{" +
                "chapterId=" + chapterId +
                ", courseId=" + courseId +
                ", chapterName='" + chapterName + '\'' +
                ", description='" + description + '\'' +
                ", orderIndex=" + orderIndex +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}