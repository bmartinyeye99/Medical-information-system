package com.medis.controllers;

import com.medis.Main;
import com.medis.models.Patient;
import com.medis.models.Record;
import com.medis.models.User;
import com.medis.models.JavaPostgreSql;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DetailedRecord implements Initializable {

    private Patient selectedPatient;
    private Record selectedRecord;
    private User loggedInUser;
    private final JavaPostgreSql javaPostgreSql = JavaPostgreSql.getInstance();

    @FXML private Label patientNameRecordTitle;
    @FXML private Label titleData;
    @FXML private Label dateData;
    @FXML private Label descriptionData;
    @FXML private Label notesData;

    @FXML
    private void switchToRecords(ActionEvent event) throws IOException {
        Main.switchToRecords(loggedInUser, selectedPatient, event);
    }

    // Edit record button
    @FXML
    private void switchToRecordEdit(ActionEvent event) throws IOException {
        Main.switchToRecordEdit(loggedInUser, selectedPatient, selectedRecord,event);
    }

    // Delete record button
    @FXML
    private void deleteRecord(ActionEvent event) {

        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setContentText("Are you sure you want to delete record " + selectedRecord.getTitle() + "?");
        a.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                javaPostgreSql.deleteRecord(selectedRecord.getId());
                try {
                    switchToRecords(event);
                    Alert b = new Alert(Alert.AlertType.INFORMATION);
                    b.setContentText("Record " + selectedRecord.getTitle() + " was deleted successfully!");
                    b.show();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    public void initData(Patient patient, Record record, User user) {
        selectedPatient = patient;
        selectedRecord = javaPostgreSql.getRecordById(record.getId());
        loggedInUser = user;
        patientNameRecordTitle.setText(selectedPatient.getFirstName() + " " + selectedPatient.getLastName() + " - " + selectedRecord.getTitle());
        titleData.setText(selectedRecord.getTitle());
        dateData.setText(selectedRecord.getDateExecuted().toString());
        descriptionData.setText(selectedRecord.getDescription());
        notesData.setText(selectedRecord.getNotes());

    }
}
