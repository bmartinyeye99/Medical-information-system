package com.medis.controllers;

import com.medis.Main;
import com.medis.models.Patient;
import com.medis.models.User;
import com.medis.models.JavaPostgreSql;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.time.LocalDate;

public class NewPrescription {

    private Patient selectedPatient;
    private User loggedInUser;
    private final JavaPostgreSql javaPostgreSql = JavaPostgreSql.getInstance();

    @FXML private Label patientNamePrescriptionTitle;
    @FXML private TextField titleData;
    @FXML private TextArea descriptionData;
    @FXML private TextField drugData;
    @FXML private DatePicker expDateYmdData;
    @FXML private TextArea notesData;
    @FXML private Label missingValuesMsg;

    // Create prescription button
    @FXML
    private void createPrescription(ActionEvent event) throws IOException {


        String titleText = titleData.getText();
        String descriptionText = descriptionData.getText();
        String drugText = drugData.getText();
        java.time.LocalDate expDateText = expDateYmdData.getValue();

        if (!titleText.isEmpty() && expDateText!=null && !descriptionText.isEmpty() && !drugText.isEmpty()){
            LocalDate today = LocalDate.now();
            if (today.isBefore(expDateText)){
                javaPostgreSql.createPrescription(titleData.getText(), descriptionData.getText(), drugData.getText(), expDateYmdData.getValue().toString(), selectedPatient.getId(), loggedInUser.getId(), notesData.getText());
                switchToPrescriptions(event);
                Alert a = new Alert(Alert.AlertType.INFORMATION);
                a.setContentText("Prescription was created successfully!");
                a.show();
            }else {
                missingValuesMsg.setText("Expiration data should be later than today!");
            }
        } else {
            missingValuesMsg.setText("Please fill in missing compulsory data!");
        }


    }

    // Cancel button
    @FXML
    private void switchToPrescriptions(ActionEvent event) throws IOException {
        Main.switchToPrescriptions(loggedInUser, selectedPatient, event);
    }

    public void initData(Patient patient, User user) {
        selectedPatient = patient;
        loggedInUser = user;
        patientNamePrescriptionTitle.setText(selectedPatient.getFirstName() + " " + selectedPatient.getLastName() + " - " + "New prescription");
    }
}
