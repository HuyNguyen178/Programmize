package model;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Objects;

public class Course {
    private Integer courseId;
    private String courseName;
    private String[] courseCategories;
    private String courseInstructor;
    private BigDecimal listedPrice;
    private BigDecimal salePrice;
    private String thumbnailUrl;
    private String description;
    private boolean status;
    private Integer duration;
    private Integer instructorId;

    public Course() {}


    // Parameterized constructor
    public Course(String courseName,String[] courseCategories, String courseInstructor, String thumbnailUrl, String description,
                  BigDecimal listedPrice, BigDecimal salePrice, boolean status, Integer duration,  Integer instructorId) {
        this.courseName = courseName;
        this.courseCategories = courseCategories;
        this.courseInstructor = courseInstructor;
        this.thumbnailUrl = thumbnailUrl;
        this.description = description;
        this.listedPrice = listedPrice;
        this.salePrice = salePrice;
        this.status = status;
        this.duration = duration;
        this.instructorId = instructorId;
    }

    public int getCourseId() {
        return courseId;
    }
    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public Integer getId() {
        return courseId;
    }
    public void setId(Integer courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }
    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String[] getCourseCategories() {
        return courseCategories;
    }
    public void setCourseCategories(String[] courseCategories) {
        this.courseCategories = courseCategories;
    }

    public String getCourseInstructor() {
        return courseInstructor;
    }
    public void setCourseInstructor(String courseInstructor) {
        this.courseInstructor = courseInstructor;
    }

    public BigDecimal getListedPrice() {
        return listedPrice;
    }
    public void setListedPrice(BigDecimal listedPrice) {
        this.listedPrice = listedPrice;
    }

    public BigDecimal getSalePrice() {
        return salePrice;
    }
    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }
    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public boolean getStatus() {
        return status;
    }
    public void setStatus(boolean status) {
        this.status = status;
    }

    public Integer getDuration() {return duration;}
    public void setDuration(Integer duration) {this.duration = duration;}

    public Integer getInstructorId() {return instructorId;}
    public void setInstructorId(Integer instructorId) {
        this.instructorId = instructorId;
    }

    // Optional: Override toString() method for debugging purposes
    @Override
    public String toString() {
        return "Course{" +
                "courseId=" + courseId +
                ", courseName='" + courseName + '\'' +
                ", courseCategories='" + Arrays.toString(courseCategories) + '\'' +
                ", courseInstructor='" + courseInstructor + '\'' +
                ", listedPrice=" + listedPrice +
                ", salePrice=" + salePrice +
                ", thumbnailUrl='" + thumbnailUrl + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    // Optional: Override equals() and hashCode() methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Course course = (Course) o;

        if (!Objects.equals(courseId, course.courseId)) return false;
        if (!Objects.equals(courseName, course.courseName)) return false;
        if (!Arrays.equals(courseCategories, course.courseCategories)) return false;
        if (!Objects.equals(courseInstructor, course.courseInstructor)) return false;
        if (!Objects.equals(listedPrice, course.listedPrice)) return false;
        if (!Objects.equals(salePrice, course.salePrice)) return false;
        if (!Objects.equals(thumbnailUrl, course.thumbnailUrl)) return false;
        if (!Objects.equals(description, course.description)) return false;
        return Objects.equals(status, course.status);
    }

}