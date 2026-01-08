package model;

import java.sql.Time;
import java.util.List;

public class Syllabus {
    private Integer syllabusId;
    private Class clazz;
    private int totalHours;
    private Time startTime;
    private Time endTime;
    private List<DaysOfWeek> daysOfWeek;
    private int attendance;
    private int assignments;
    private int finalExam;
    private String objectives;
    private List<SyllabusLesson> lessons;

    public List<SyllabusLesson> getLessons() {
        return lessons;
    }

    public void setLessons(List<SyllabusLesson> lessons) {
        this.lessons = lessons;
    }

    public Integer getSyllabusId() {
        return syllabusId;
    }

    public void setSyllabusId(Integer syllabusId) {
        this.syllabusId = syllabusId;
    }

    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    public int getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(int totalHours) {
        this.totalHours = totalHours;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    public List<DaysOfWeek> getDaysOfWeek() {
        return daysOfWeek;
    }

    public void setDaysOfWeek(List<DaysOfWeek> daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }

    public int getAttendance() {
        return attendance;
    }

    public void setAttendance(int attendance) {
        this.attendance = attendance;
    }

    public int getAssignments() {
        return assignments;
    }

    public void setAssignments(int assignments) {
        this.assignments = assignments;
    }

    public int getFinalExam() {
        return finalExam;
    }

    public void setFinalExam(int finalExam) {
        this.finalExam = finalExam;
    }

    public String getObjectives() {
        return objectives;
    }

    public void setObjectives(String objectives) {
        this.objectives = objectives;
    }
}
