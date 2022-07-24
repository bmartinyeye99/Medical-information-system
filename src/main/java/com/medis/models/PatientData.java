package com.medis.models;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.ArrayList;
@JacksonXmlRootElement(localName = "patient")
public class PatientData {
    @JacksonXmlProperty(localName = "data")
    private Patient patientData;

    @JacksonXmlProperty(localName = "appointments")
    @JacksonXmlElementWrapper(useWrapping = false)
    private ArrayList<Appointment> appointment;
    @JacksonXmlProperty(localName = "prescriptions")
    @JacksonXmlElementWrapper(useWrapping = false)
    private ArrayList<Prescription> prescription;
    @JacksonXmlProperty(localName = "records")
    @JacksonXmlElementWrapper(useWrapping = false)
    private  ArrayList<Record> record;

    public PatientData(Patient patientData, ArrayList<Appointment> appointments, ArrayList<Prescription> prescriptions, ArrayList<Record> records) {
        this.patientData = patientData;
        this.appointment = appointments;
        this.prescription = prescriptions;
        this.record = records;
    }

    public PatientData(Patient patientData) {
        this.patientData = patientData;
    }

    public void setPatientData(Patient patientData) {
        this.patientData = patientData;
    }

    public void setAppointment(ArrayList<Appointment> appointment) {
        this.appointment = appointment;
    }

    public void setPrescription(ArrayList<Prescription> prescription) {
        this.prescription = prescription;
    }

    public void setRecords(ArrayList<Record> records) {
        this.record = records;
    }

    public Patient getPatientData() {
        return patientData;
    }

    public ArrayList<Appointment> getAppointment() {
        return appointment;
    }

    public ArrayList<Prescription> getPrescription() {
        return prescription;
    }

    public ArrayList<Record> getRecords() {
        return record;
    }
}
