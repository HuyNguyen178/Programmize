package model;

import java.sql.Date;

public class Lesson {
    private Integer lessonId;
    private Integer chapterId;
    private String lessonName;
    private String lessonType;
    private String content;
    private String videoUrl;
    private Integer duration;
    private Integer orderIndex;
    private boolean isPreview;
    private boolean status;
    private Date createdAt;
    private Date updatedAt;

    public Lesson() {
    }

    public Lesson(Integer lessonId, Integer chapterId, String lessonName, String lessonType,
                  String content, String videoUrl, Integer duration, Integer orderIndex,
                  boolean isPreview, boolean status, Date createdAt, Date updatedAt) {
        this.lessonId = lessonId;
        this.chapterId = chapterId;
        this.lessonName = lessonName;
        this.lessonType = lessonType;
        this.content = content;
        this.videoUrl = videoUrl;
        this.duration = duration;
        this.orderIndex = orderIndex;
        this.isPreview = isPreview;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    //Constructor without ID to insert new record/
    public Lesson(Integer chapterId, String lessonName, String lessonType, String content,
                  String videoUrl, Integer duration, Integer orderIndex, boolean isPreview, boolean status) {
        this.chapterId = chapterId;
        this.lessonName = lessonName;
        this.lessonType = lessonType;
        this.content = content;
        this.videoUrl = videoUrl;
        this.duration = duration;
        this.orderIndex = orderIndex;
        this.isPreview = isPreview;
        this.status = status;
    }

    public Integer getLessonId() {
        return lessonId;
    }
    public void setLessonId(Integer lessonId) {
        this.lessonId = lessonId;
    }

    public Integer getChapterId() {
        return chapterId;
    }
    public void setChapterId(Integer chapterId) {
        this.chapterId = chapterId;
    }

    public String getLessonName() {
        return lessonName;
    }
    public void setLessonName(String lessonName) {
        this.lessonName = lessonName;
    }

    public String getLessonType() {
        return lessonType;
    }
    public void setLessonType(String lessonType) {
        this.lessonType = lessonType;
    }

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public String getVideoUrl() {
        return videoUrl;
    }
    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public Integer getDuration() {
        return duration;
    }
    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }
    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

    public boolean isPreview() {
        return isPreview;
    }
    public void setPreview(boolean preview) {
        isPreview = preview;
    }

    public boolean isStatus() {
        return status;
    }
    public void setStatus(boolean status) {
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

    @Override
    public String toString() {
        return "Lesson{" +
                "lessonId=" + lessonId +
                ", chapterId=" + chapterId +
                ", lessonName='" + lessonName + '\'' +
                ", lessonType='" + lessonType + '\'' +
                ", duration=" + duration +
                ", orderIndex=" + orderIndex +
                ", isPreview=" + isPreview +
                ", status=" + status +
                '}';
    }
}