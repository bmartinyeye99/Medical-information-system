package com.medis.controllers;

import com.medis.Main;
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
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditUserInfo implements Initializable {

    private User loggedInUser;
    private User selectedUser;
    JavaPostgreSql javaPostgreSql = new JavaPostgreSql();

    @FXML private TextField firstNameData;
    @FXML private TextField lastNameData;
    @FXML private TextField passwordData;
    @FXML private TextField usernameData;
    @FXML private TextField phoneData;
    @FXML private TextField emailData;
    @FXML private ComboBox<String> positionData;
    @FXML private DatePicker birthDateData;
    @FXML private Label missingValuesMsg;

    // Save button
    @FXML
    private void updateUserInfo(ActionEvent event) throws IOException {

        String firstNameText = firstNameData.getText();
        String lastNameText = lastNameData.getText();
        String positionText = positionData.getValue();
        String phoneText = phoneData.getText();
        String emailText = emailData.getText();
        String passwordText = passwordData.getText();
        LocalDate birthDateText = birthDateData.getValue();
        String usernameText = usernameData.getText();

        if(!firstNameText.isEmpty() && !lastNameText.isEmpty() && positionText!=null && !passwordText.isEmpty() &&  !phoneText.isEmpty() && !emailText.isEmpty() && birthDateText != null && !usernameText.isEmpty()){
            if (validationPhone(phoneText)){
                if (validationEmail(emailText)){
                    javaPostgreSql.updateUser(selectedUser.getId(), usernameData.getText(), firstNameData.getText(), lastNameData.getText(), passwordData.getText(), emailData.getText(), phoneData.getText(), String.valueOf(positionData.getSelectionModel().getSelectedItem()), birthDateData.getValue().toString());
                    switchToUserInfo(event);
                    Alert a = new Alert(Alert.AlertType.INFORMATION);
                    a.setContentText("User info was updated successfully!");
                    a.show();
                }
                else{
                    missingValuesMsg.setText("Please enter valid email!");
                }
            }
            else{
                missingValuesMsg.setText("Please enter valid phone number!");
            }

        }
        else {
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

    // Cancel button
    @FXML
    private void switchToUserInfo(ActionEvent event) throws IOException {
        Main.switchToUserInfo(loggedInUser, selectedUser, event);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        positionData.setItems(FXCollections.observableArrayList("doctor", "nurse", "receptionist", "admin"));
    }

    public void initData(User user, User user2)  {
        loggedInUser = user;
        selectedUser = javaPostgreSql.getUserById(user2.getId());
        firstNameData.setText(selectedUser.getFirstName());
        lastNameData.setText(selectedUser.getLastName());
        usernameData.setText(selectedUser.getUsername());
        phoneData.setText(selectedUser.getPhone());
        emailData.setText(selectedUser.getEmail());
        positionData.getSelectionModel().select(String.valueOf(selectedUser.getPosition()));
        birthDateData.setValue(LocalDate.parse(selectedUser.getBirthDateFormatted()));

    }
}
