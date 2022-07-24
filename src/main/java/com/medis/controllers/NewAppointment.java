package com.medis.controllers;

import com.medis.Main;
import com.medis.models.Patient;
import com.medis.models.User;
import com.medis.models.JavaPostgreSql;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class NewAppointment implements Initializable {

    private Patient selectedPatient;
    private User loggedInUser;
    private final JavaPostgreSql javaPostgreSql = JavaPostgreSql.getInstance();

    @FXML private Label patientNameNewAppointment;
    @FXML private TextField titleData;
    @FXML private DatePicker startYmdData;
    @FXML private ComboBox<String> startHData;
    @FXML private ComboBox<String> startMinData;
    @FXML private DatePicker endYmdData;
    @FXML private ComboBox<String> endHData;
    @FXML private ComboBox<String> endMinData;
    @FXML private TextField descriptionData;
    @FXML private ComboBox <String> doctorData;
    @FXML private Label missingValuesMsg;

    // Create appointment button
    @FXML
    private void createAppointment(ActionEvent event) throws IOException {

        String titleText = titleData.getText();
        LocalDate startYmdText =  startYmdData.getValue();
        String startHText =  startHData.getValue();
        String startMinText =  startMinData.getValue();
        LocalDate endYmdText =  endYmdData.getValue();
        String endHText =  endHData.getValue();
        String endMinText =  endMinData.getValue();
        String descriptionText =  descriptionData.getText();
        String doctorText = doctorData.getValue();

        if (!titleText.isEmpty() && startYmdText!=null && startHText!=null && startMinText!=null && endYmdText!=null && endHText!=null && endMinText!=null && !descriptionText.isEmpty() && doctorText!=null){
            LocalDateTime today = LocalDateTime.now();
            String startstr = startYmdText + " " + startHText + ":" + startMinText;
            String endstr = endYmdText + " " + endHText + ":" + endMinText;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime startdateTime = LocalDateTime.parse(startstr, formatter);
            LocalDateTime enddateTime = LocalDateTime.parse(endstr, formatter);
            if (today.isBefore(startdateTime)){
                if (today.isBefore(enddateTime)){
                    if (startdateTime.isBefore(enddateTime)){
                        String[] doctorName = doctorData.getValue().split(" ");
                        javaPostgreSql.creteAppointment(titleData.getText(), descriptionData.getText(), startYmdData.getValue().toString()+" " + startHData.getValue() + ":" + startMinData.getValue(), endYmdData.getValue().toString() + " "  + endHData.getValue()+":" + endMinData.getValue(), selectedPatient.getId(), javaPostgreSql.getUserByFirstAndLastName(doctorName[0],doctorName[1]).getId(), loggedInUser.getId());
                        switchToAppointments(event);
                        Alert a = new Alert(Alert.AlertType.INFORMATION);
                        a.setContentText("Appointment was created successfully!");
                        a.show();
                    } else {
                        missingValuesMsg.setText("Starting date have to be before ending date!");
                    }
                }
                else {
                    missingValuesMsg.setText("Please fill appropriate ending date!");
                }

            }
            else {
                missingValuesMsg.setText("Please fill appropriate starting date!");
            }
        }else {

            missingValuesMsg.setText("Please fill in missing compulsory data!");

        }

    }

    // Cancel button
    @FXML
    private void switchToAppointments(ActionEvent event) throws IOException {
        Main.switchToAppointments(loggedInUser, selectedPatient, event);
    }

    private void fillAppointmentOptions(ComboBox<String> doctorData, ComboBox<String> startHData, ComboBox<String> startMinData, ComboBox<String> endHData, ComboBox<String> endMinData) {
        doctorData.setItems(FXCollections.observableArrayList(javaPostgreSql.getUsersByPosition("doctor")));
        startHData.setItems(FXCollections.observableArrayList("00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17","18","19","20","21","22","23"));
        startMinData.setItems(FXCollections.observableArrayList("00", "15", "30", "45"));
        endHData.setItems(FXCollections.observableArrayList("00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17","18","19","20","21","22","23"));
        endMinData.setItems(FXCollections.observableArrayList("00", "15", "30", "45"));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        fillAppointmentOptions(doctorData, startHData, startMinData, endHData, endMinData);
    }

    public void initData(Patient patient, User user) {
        selectedPatient = patient;
        loggedInUser = user;
        patientNameNewAppointment.setText(selectedPatient.getFirstName() + " " + selectedPatient.getLastName() + " - " + "New appointment");
    }
}
