package model;

import java.util.Date;

public class Lesson {
    private Integer lessonId;
    private Integer chapterId;
    private String lessonName;
    private LessonType lessonType;
    private String content;
    private String videoUrl;
    private Integer duration;
    private Integer orderIndex;
    private boolean isPreview;
    private Boolean status;
    private Date createdAt;
    private Date updatedAt;

    public enum LessonType {VIDEO("video"), TEXT("text"), QUIZ("quiz"), ASSIGNMENT("assignment");

        private final String value;

        LessonType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static LessonType fromString(String text) {
            for (LessonType type : LessonType.values()) {
                if (type.value.equalsIgnoreCase(text)) {
                    return type;
                }
            }
            return VIDEO;
        }
    }

    public Lesson() {
    }

    public Lesson(Integer chapterId, String lessonName, LessonType lessonType, Integer orderIndex) {
        this.chapterId = chapterId;
        this.lessonName = lessonName;
        this.lessonType = lessonType;
        this.orderIndex = orderIndex;
    }

    public Lesson(Integer lessonId, Integer chapterId, String lessonName, LessonType lessonType,
                  String content, String videoUrl, Integer duration, Integer orderIndex,
                  boolean isPreview, Boolean status, Date createdAt, Date updatedAt) {
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

    public LessonType getLessonType() {
        return lessonType;
    }
    public void setLessonType(LessonType lessonType) {
        this.lessonType = lessonType;
    }

    public void setLessonTypeFromString(String lessonType) {
        this.lessonType = LessonType.fromString(lessonType);
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

//  extra methods
    public String getDurationFormatted() {
        if (duration <= 0) {
            return "0 min";
        }

        int hours = duration / 3600;
        int minutes = (duration % 3600) / 60;
        int seconds = duration % 60;

        if (hours > 0) {
            if (minutes > 0) {
                return hours + " hr " + minutes + " min";
            }
            return hours + " hr";
        } else if (minutes > 0) {
            return minutes + " min";
        } else {
            return seconds + " sec";
        }
    }

    public String getTypeIcon() {
        if (lessonType == null) {
            return "fas fa-file";
        }

        switch (lessonType) {
            case VIDEO:
                return "fas fa-video";
            case TEXT:
                return "fas fa-file-alt";
            case QUIZ:
                return "fas fa-question-circle";
            case ASSIGNMENT:
                return "fas fa-laptop-code";
            default:
                return "fas fa-file";
        }
    }

    public String getTypeDisplayName() {
        if (lessonType == null) {
            return "Lesson";
        }

        switch (lessonType) {
            case VIDEO:
                return "Video";
            case TEXT:
                return "Reading";
            case QUIZ:
                return "Quiz";
            case ASSIGNMENT:
                return "Assignment";
            default:
                return "Lesson";
        }
    }

    @Override
    public String toString() {
        return "Lesson{" +
                "lessonId=" + lessonId +
                ", chapterId=" + chapterId +
                ", lessonName='" + lessonName + '\'' +
                ", lessonType=" + lessonType +
                ", duration=" + duration +
                ", orderIndex=" + orderIndex +
                ", isPreview=" + isPreview +
                ", status=" + status +
                '}';
    }
}