package model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;

public class Class {
    private int id;
    private String name;
    private String thumbnailUrl;
    private int numberOfStudents;
    private String[] categories;
    private BigDecimal listedPrice;
    private BigDecimal salePrice;
    private boolean status;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private User instructor;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public int getNumberOfStudents() {
        return numberOfStudents;
    }

    public void setNumberOfStudents(int numberOfStudents) {
        this.numberOfStudents = numberOfStudents;
    }

    public String[] getCategories() {
        return categories;
    }

    public void setCategories(String[] categories) {
        this.categories = categories;
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

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public User getInstructor() {
        return instructor;
    }

    public void setInstructor(User instructor) {
        this.instructor = instructor;
    }

    @Override
    public String toString() {
        return "Class{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", thumbnailUrl='" + thumbnailUrl + '\'' +
                ", numberOfStudents=" + numberOfStudents +
                ", categories=" + Arrays.toString(categories) +
                ", listedPrice=" + listedPrice +
                ", salePrice=" + salePrice +
                ", status=" + status +
                ", description='" + description + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", instructor=" + instructor.getFullname() +
                '}';
    }
}
