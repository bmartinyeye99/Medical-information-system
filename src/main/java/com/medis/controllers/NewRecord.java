package com.medis.controllers;

import com.medis.Main;
import com.medis.models.Patient;
import com.medis.models.User;
import com.medis.models.JavaPostgreSql;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;

public class NewRecord {

    private Patient selectedPatient;
    private User loggedInUser;
    private final JavaPostgreSql javaPostgreSql = JavaPostgreSql.getInstance();

    @FXML private Label patientNameNewRecord;
    @FXML private TextField titleData;
    @FXML private DatePicker dateData;
    @FXML private TextArea descriptionData;
    @FXML private TextArea notesData;
    @FXML private Label missingValuesMsg;

    // Create record button
    @FXML
    private void createRecord(ActionEvent event) throws IOException {

        String titleText = titleData.getText();
        java.time.LocalDate dateText = dateData.getValue();
        String descriptionText = descriptionData.getText();

        if (!titleText.isEmpty() && dateText!=null && !descriptionText.isEmpty()){
            javaPostgreSql.createRecord(titleData.getText(), descriptionData.getText(), dateData.getValue().toString(), notesData.getText(), selectedPatient.getId(), loggedInUser.getId());
            switchToRecords(event);
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setContentText("Record was created successfully!");
            a.show();
        } else {
            missingValuesMsg.setText("Please fill in missing compulsory data!");
        }

    }

    // Cancel button
    @FXML
    private void switchToRecords(ActionEvent event) throws IOException {
        Main.switchToRecords(loggedInUser, selectedPatient, event);
    }

    public void initData(Patient patient, User user) {
        selectedPatient = patient;
        loggedInUser = user;
        patientNameNewRecord.setText(selectedPatient.getFirstName() + " " + selectedPatient.getLastName() + " - " + "New record");
    }
}
