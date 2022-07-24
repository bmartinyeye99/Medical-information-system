package com.medis.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.GregorianCalendar;

public class Appointment {
    private long id;
    private String title;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String startTimeFormatted;
    private String endTimeFormatted;
    private LocalDateTime createdAt, updatedAt;
    private int startHour, startMin, startYear, startMonth, startDay, endHour, endMin, endYear, endMonth, endDay;
    private long patientId;
    private String patientName;
    private long doctorId;
    private String doctorName;
    private boolean deleted;
    private long createdBy;

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String firstName, String lastName) {
        this.patientName = firstName + " " + lastName;
    }

    public String getStartTimeFormatted() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return startTime.format(formatter);
    }

    public String getEndTimeFormatted() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return endTime.format(formatter);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
        this.startYear = startTime.getYear();
        this.startMonth = startTime.getMonthValue();
        this.startDay = startTime.getDayOfMonth();
        this.startHour = startTime.getHour();
        this.startMin = startTime.getMinute();
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
        this.endYear = endTime.getYear();
        this.endMonth = endTime.getMonthValue();
        this.endDay = endTime.getDayOfMonth();
        this.endHour = endTime.getHour();
        this.endMin = endTime.getMinute();
    }

    public long getPatientId() {
        return patientId;
    }

    public void setPatientId(long patientId) {
        this.patientId = patientId;
    }

    public long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(long doctorId) {
        this.doctorId = doctorId;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(long createdBy) {
        this.createdBy = createdBy;
    }

    public String getStartHour() {
        if (startHour > 9) {
            return String.valueOf(startHour);
        }else {
            return "0"+ startHour;
        }
    }

    public String getStartMin() {
        if (startMin > 9) {
            return String.valueOf(startMin);
        }else {
            return "0"+ startMin;
        }
    }

    public int getStartYear() {
        return startYear;
    }

    public int getStartMonth() {
        return startMonth;
    }

    public int getStartDay() {
        return startDay;
    }

    public String getEndHour() {
        if (endHour > 9) {
            return String.valueOf(endHour);
        }else {
            return "0"+ endHour;
        }
    }

    public String getEndMin() {
        if (endMin > 9) {
            return String.valueOf(endMin);
        }else {
            return "0"+ endMin;
        }
    }

    public int getEndYear() {
        return endYear;
    }

    public int getEndMonth() {
        return endMonth;
    }

    public int getEndDay() {
        return endDay;
    }

    public Date getStartDate(){
        return new GregorianCalendar(startYear, startMonth, startDay).getTime();
    }

    public Date getEndDate(){
        return new GregorianCalendar(endYear, endMonth, endDay).getTime();
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public void setStartMin(int startMin) {
        this.startMin = startMin;
    }

    public void setStartYear(int startYear) {
        this.startYear = startYear;
    }

    public void setStartMonth(int startMonth) {
        this.startMonth = startMonth;
    }

    public void setStartDay(int startDay) {
        this.startDay = startDay;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    public void setEndMin(int endMin) {
        this.endMin = endMin;
    }

    public void setEndYear(int endYear) {
        this.endYear = endYear;
    }

    public void setEndMonth(int endMonth) {
        this.endMonth = endMonth;
    }

    public void setEndDay(int endDay) {
        this.endDay = endDay;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }
}
