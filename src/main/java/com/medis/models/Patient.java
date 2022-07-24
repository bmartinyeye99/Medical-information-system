package com.medis.models;

import java.sql.Date;
import java.time.LocalDateTime;

public class Patient {

    private final JavaPostgreSql javaPostgreSql = JavaPostgreSql.getInstance();

    private long id;
    private String firstName, lastName;
    private String insuranceCompany;
    private String phone;
    private Date birthDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean deleted;
    private long birthId;
    private String sex;
    private String bloodGroup;
    private String address;
    private String email;
    private String nextVisit;
    private String formattedNextVisit;

    public void setNextVisit(String nextVisit) {
        this.nextVisit = nextVisit;
    }


    public String getFormattedNextVisit() {
        if (nextVisit.equals("0000-00-00 00:00")) {
            return "Not specified";
        }
        return nextVisit.replace("T", " at ");
    }

    public String getInsuranceCompany() {
        return insuranceCompany;
    }

    public void setInsuranceCompany(String insuranceCompany) {
        this.insuranceCompany = insuranceCompany;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public void setBirthId(long birthId) {
        this.birthId = birthId;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
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

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public long getBirthId() {
        return birthId;
    }

    public void setBirthID(long birthId) {
        this.birthId = birthId;
    }

    public static boolean hasValidID(String identificationNumber) {
        long identificationNumberLong = Long.parseLong(identificationNumber);
        long date = identificationNumberLong / 10000;
        int day = (int) (date % 100);
        int month = (int) (date / 100 % 100);

        if (month >= 51 && month <= 62)
            month -= 50;

        int[] mdays = {0, 31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

        return identificationNumberLong % 11 == 0 && month > 0 && (month <= 12 || month >= 51) &&
                month <= 62 && day > 0 && day <= mdays[month];
    }

    public static String getGender(String identificationNumber) {

        if (!(identificationNumber.equals(""))) {
            long identificationNumberLong = Long.parseLong(identificationNumber);
            long date = identificationNumberLong / 10000;
            int month = (int) (date / 100 % 100);
            if (month >= 51 && month <= 62)
                return "female";
            else
                return "male";
        }
        return null;
    }

    public static int getYear(String identificationNumber) {
        long identificationNumberLong = Long.parseLong(identificationNumber);
        long date = identificationNumberLong / 10000;
        int year = (int) (date / 10000 % 100);

        if (year >= 20)
            year += 1900;

        else
            year += 2000;


        return year;
    }

    public static int getMonth(String identificationNumber) {
        long identificationNumberLong = Long.parseLong(identificationNumber);
        long date = identificationNumberLong / 10000;
        int month = (int) (date / 100 % 100);

        if (month >= 51 && month <= 62)
            month -= 50;

        return month;
    }

    public static int getDay(String identificationNumber) {
        long identificationNumberLong = Long.parseLong(identificationNumber);
        long date = identificationNumberLong / 10000;

        return (int) (date % 100);
    }

    public void setFormattedNextVisit(String formattedNextVisit) {
        this.formattedNextVisit = formattedNextVisit;
    }
}
