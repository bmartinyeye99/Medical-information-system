package com.medis.controllers;

import com.medis.Main;
import com.medis.models.Patient;
import com.medis.models.User;
import com.medis.models.JavaPostgreSql;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditPatientInfo implements Initializable{


    private Patient selectedPatient;
    private User loggedInUser;
    private final JavaPostgreSql javaPostgreSql = JavaPostgreSql.getInstance();

    @FXML private Label firstNameAndLastName;
    @FXML private TextField firstNameData;
    @FXML private TextField lastNameData;
    @FXML private ComboBox<String> insuranceCoData;
    @FXML private TextField birthIdData;
    @FXML private ComboBox<String> sexData;
    @FXML private ComboBox<String> bloodGroupData;
    @FXML private TextField addressData;
    @FXML private TextField phoneData;
    @FXML private TextField emailData;
    public Label missingValuesMsg;

    // Save button
    @FXML
    private void updatePatientInfo(ActionEvent event) throws IOException {


        String firstNameText = firstNameData.getText();
        String lastNameText = lastNameData.getText();
        String insuranceCompanyText = insuranceCoData.getValue();
        String sexValue = sexData.getValue();
        String birthIdText = birthIdData.getText();
        String bloodGroupValue = bloodGroupData.getValue();
        String addressText = addressData.getText();
        String phoneText = phoneData.getText();
        String emailText = emailData.getText();

        if (!firstNameText.isEmpty() && !lastNameText.isEmpty() && insuranceCompanyText!=null && !birthIdText.isEmpty() && sexValue!=null && bloodGroupValue!=null && !addressText.isEmpty() && !phoneText.isEmpty() && !emailText.isEmpty()){
            if (validationBirthID(birthIdText)){
                if (validationPhone(phoneText)){
                    if (validationEmail(emailText)){
                        String birthDateFromId = Patient.getYear(birthIdText) + "-" + Patient.getMonth(birthIdText) + "-" + Patient.getDay(birthIdText);
                        javaPostgreSql.updatePatient(selectedPatient.getId(), firstNameData.getText(), lastNameData.getText(), insuranceCoData.getSelectionModel().getSelectedItem(), birthDateFromId, sexData.getSelectionModel().getSelectedItem(), bloodGroupData.getSelectionModel().getSelectedItem(), addressData.getText(), phoneData.getText(), emailData.getText(), birthIdData.getText());
                        switchToPatientInfo(event);
                        Alert a = new Alert(Alert.AlertType.INFORMATION);
                        a.setContentText("Patient info was updated successfully!");
                        a.show();
                    } else {
                        missingValuesMsg.setText("Please enter valid email!");
                    }
                } else {
                    missingValuesMsg.setText("Please enter valid phone number!");
                }
            } else {
                missingValuesMsg.setText("Please enter valid birth id!");
            }
        }else {
            missingValuesMsg.setText("Please fill in missing compulsory data!");
        }


    }

    public boolean validationEmail(String emailText){
        String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z\\d-]+\\.)+[a-zA-Z]{2,6}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(emailText);
        return matcher.matches();
    }

    public boolean validationPhone(String phoneText){
        String regex = "^\\d*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(phoneText);
        return matcher.matches();
    }

    public boolean validationBirthID(String birthId){
        String regex = "^\\d*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(birthId);
        if (matcher.matches()){
            return Patient.hasValidID(birthId);
        }
        else {
            return false;
        }
    }

    // Cancel button
    @FXML
    private void switchToPatientInfo(ActionEvent event) throws IOException {
        Main.switchToPatientInfo(loggedInUser, selectedPatient, event);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        insuranceCoData.setItems(FXCollections.observableArrayList("Union", "Dôvera", "VŠZP"));
        sexData.setItems(FXCollections.observableArrayList("male","female"));
        bloodGroupData.setItems(FXCollections.observableArrayList("A","A+","A-","B","B+","B-","AB","AB+","AB-", "O","O+","O-"));
    }

    public void initData(Patient patient, User user) {
        loggedInUser = user;
        selectedPatient = javaPostgreSql.getPatientById(patient.getId());
        firstNameAndLastName.setText(selectedPatient.getFirstName() + " " + selectedPatient.getLastName());
        firstNameData.setText(selectedPatient.getFirstName());
        lastNameData.setText(selectedPatient.getLastName());
        insuranceCoData.getSelectionModel().select(String.valueOf(selectedPatient.getInsuranceCompany()));
        birthIdData.setText(String.valueOf(selectedPatient.getBirthId()));
        sexData.getSelectionModel().select(String.valueOf(selectedPatient.getSex()));
        bloodGroupData.getSelectionModel().select(String.valueOf(selectedPatient.getBloodGroup()));
        addressData.setText(selectedPatient.getAddress());
        phoneData.setText(selectedPatient.getPhone());
        emailData.setText(selectedPatient.getEmail());
    }
}
