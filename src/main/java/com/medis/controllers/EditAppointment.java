package com.medis.controllers;

import com.medis.Main;
import com.medis.models.Appointment;
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

public class EditAppointment implements Initializable {

    private Patient selectedPatient;
    private User loggedInUser;
    private Appointment selectedAppointment;
    private final JavaPostgreSql javaPostgreSql = JavaPostgreSql.getInstance();
    private String previousScene;

    @FXML private Label patientNameAppointmentTitle;
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

    // Save button
    @FXML
    private void updateAppointment(ActionEvent event) throws IOException {

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
            System.out.println(startYmdData.getValue().toString()+" " + startHData.getValue() + ":" + startMinData.getValue());
            System.out.println(endYmdData.getValue().toString() + " "  + endHData.getValue()+":" + endMinData.getValue());
            if (today.isBefore(startdateTime)){
                if (today.isBefore(enddateTime)){
                    if (startdateTime.isBefore(enddateTime)){
                        String[] doctorName = doctorData.getValue().split(" ");
                        System.out.println(javaPostgreSql.getUserByFirstAndLastName(doctorName[0],doctorName[1]).getId()+ " " + doctorName[0] + " " + doctorName[1]);
                        javaPostgreSql.updateAppointment(
                                selectedAppointment.getId(),
                                titleData.getText(),
                                descriptionData.getText(),
                                startYmdData.getValue().toString()+" " + startHData.getValue() + ":" + startMinData.getValue(),
                                endYmdData.getValue().toString() + " "  + endHData.getValue()+":" + endMinData.getValue(),
                                selectedAppointment.getPatientId(), javaPostgreSql.getUserByFirstAndLastName(doctorName[0],doctorName[1]).getId());

                        switchToAppointments(event);
                        Alert a = new Alert(Alert.AlertType.INFORMATION);
                        a.setContentText("Appointment was updated successfully!");
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
        if (previousScene.equals("AllAppointments"))
            Main.switchToAllAppointments(loggedInUser, event);
        else
            Main.switchToAppointments(loggedInUser, selectedPatient, event);
    }

    // Delete button
    @FXML
    private void deleteAppointment(ActionEvent event) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setContentText("Are you sure you want to delete appointment " + selectedAppointment.getTitle() +"?");
        a.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                javaPostgreSql.deleteAppointment(selectedAppointment.getId());
                try {
                    Main.switchToAppointments(loggedInUser, selectedPatient, event);
                    Alert b = new Alert(Alert.AlertType.INFORMATION);
                    b.setContentText("Appointment " + selectedAppointment.getTitle() + " was deleted successfully!");
                    b.show();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        });
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

    public void initData(Patient patient, Appointment appointment, User user, String scene) {
        previousScene = scene;
        selectedPatient = patient;
        loggedInUser = user;
        selectedAppointment = appointment;
        patientNameAppointmentTitle.setText(selectedPatient.getFirstName() + " " + selectedPatient.getLastName() + " - " + selectedAppointment.getTitle());
        titleData.setText(selectedAppointment.getTitle());
        descriptionData.setText(selectedAppointment.getDescription());
        doctorData.getSelectionModel().select(javaPostgreSql.getUserById(selectedAppointment.getDoctorId()).getFirstName() + " " + javaPostgreSql.getUserById(selectedAppointment.getDoctorId()).getLastName());
        startYmdData.setValue(selectedAppointment.getStartTime().toLocalDate());
        endYmdData.setValue(selectedAppointment.getEndTime().toLocalDate());
        startHData.getSelectionModel().select(String.valueOf(selectedAppointment.getStartHour()));
        endHData.getSelectionModel().select(String.valueOf(selectedAppointment.getEndHour()));
        startMinData.getSelectionModel().select(String.valueOf(selectedAppointment.getStartMin()));
        endMinData.getSelectionModel().select(String.valueOf(selectedAppointment.getEndMin()));

    }
}
