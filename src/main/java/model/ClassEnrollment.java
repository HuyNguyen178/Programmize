package model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class ClassEnrollment {
    private int enrollmentId;
    private int userId;
    private int classId;
    private BigDecimal pricePaid;
    private String paymentMethod;
    private Timestamp enrolledAt;
    private boolean status;

    public ClassEnrollment(int enrollmentId, int userId, int classId, BigDecimal pricePaid, String paymentMethod, Timestamp enrolledAt, boolean status) {
        this.enrollmentId = enrollmentId;
        this.userId = userId;
        this.classId = classId;
        this.pricePaid = pricePaid;
        this.paymentMethod = paymentMethod;
        this.enrolledAt = enrolledAt;
        this.status = status;
    }

    public int getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(int enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public BigDecimal getPricePaid() {
        return pricePaid;
    }

    public void setPricePaid(BigDecimal pricePaid) {
        this.pricePaid = pricePaid;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Timestamp getEnrolledAt() {
        return enrolledAt;
    }

    public void setEnrolledAt(Timestamp enrolledAt) {
        this.enrolledAt = enrolledAt;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
