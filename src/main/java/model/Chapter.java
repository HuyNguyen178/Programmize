package model;

import java.util.Date;

public class Chapter {
    private Integer chapterId;
    private Integer courseId;
    private String chapterName;
    private String description;
    private Integer orderIndex;
    private Boolean status;
    private Date createdAt;
    private Date updatedAt;

    private int lessonCount;


    public Chapter() {
    }

    public Chapter(Integer chapterId, Integer courseId, String chapterName, String description,
                   Integer orderIndex, Boolean status, Date createdAt, Date updatedAt) {
        this.chapterId = chapterId;
        this.courseId = courseId;
        this.chapterName = chapterName;
        this.description = description;
        this.orderIndex = orderIndex;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // constructor w/o ID -> for creating new chapter
    public Chapter(Integer courseId, String chapterName, String description,
                   Integer orderIndex, Boolean status, Date createdAt, Date updatedAt) {
        this.courseId = courseId;
        this.chapterName = chapterName;
        this.description = description;
        this.orderIndex = orderIndex;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

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

    public Boolean getStatus() {
        return status;
    }
    public void setStatus(Boolean status) {
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

    public int getLessonCount() {return lessonCount; }
    public void setLessonCount(int lessonCount) {this.lessonCount = lessonCount; }


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