package com.medis.controllers;

import com.medis.Main;
import com.medis.models.Patient;
import com.medis.models.Prescription;
import com.medis.models.User;
import com.medis.models.JavaPostgreSql;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.time.LocalDate;

public class EditPrescription  {

    private Patient selectedPatient;
    private Prescription selectedPrescription;
    private User loggedInUser;
    private final JavaPostgreSql javaPostgreSql = JavaPostgreSql.getInstance();

    @FXML private Label patientNameAppointmentTitle;
    @FXML private TextField titleData;
    @FXML private TextArea descriptionData;
    @FXML private TextField drugData;
    @FXML private DatePicker expDateYmdData;
    @FXML private TextArea notesData;
    @FXML private Label missingValuesMsg;

    // Save button
    @FXML
    private void updatePrescription(ActionEvent event) throws IOException {

        String titleText = titleData.getText();
        String descriptionText = descriptionData.getText();
        String drugText = drugData.getText();
        java.time.LocalDate expDateText = expDateYmdData.getValue();

        if (!titleText.isEmpty() && expDateText!=null && !descriptionText.isEmpty() && !drugText.isEmpty()){
            LocalDate today = LocalDate.now();
            if (today.isBefore(expDateText)){
                javaPostgreSql.updatePrescription(selectedPrescription.getId(), titleData.getText(), descriptionData.getText(), drugData.getText(), expDateYmdData.getValue().toString(), selectedPatient.getId(), 1, notesData.getText());
                switchToPrescriptions(event);
                Alert a = new Alert(Alert.AlertType.INFORMATION);
                a.setContentText("Prescription was updated successfully!");
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

    // Delete prescription button
    @FXML
    private void deletePrescription(ActionEvent event) {

        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setContentText("Are you sure you want to delete prescription " + selectedPrescription.getTitle() + "?");
        a.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                javaPostgreSql.deletePrescription(selectedPrescription.getId());
                try {
                    switchToPrescriptions(event);
                    Alert b = new Alert(Alert.AlertType.INFORMATION);
                    b.setContentText("Prescription " + selectedPrescription.getTitle() + " was deleted successfully!");
                    b.show();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

    }

    public void initData(Patient patient, Prescription prescription, User user) {
        selectedPatient = patient;
        loggedInUser = user;
        selectedPrescription = prescription;
        patientNameAppointmentTitle.setText(selectedPatient.getFirstName() + " " + selectedPatient.getLastName() + " - " + selectedPrescription.getTitle());
        titleData.setText(selectedPrescription.getTitle());
        descriptionData.setText(selectedPrescription.getDescription());
        drugData.setText(selectedPrescription.getDrug());
        expDateYmdData.setValue(selectedPrescription.getExpirationDate().toLocalDate());
        notesData.setText(selectedPrescription.getNotes());
    }


}
