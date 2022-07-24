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

public class NewUser implements Initializable {

    private User loggedInUser;
    private final JavaPostgreSql javaPostgreSql = JavaPostgreSql.getInstance();

    @FXML private TextField firstNameData;
    @FXML private TextField lastNameData;
    @FXML private ComboBox<String> positionData;
    @FXML private TextField phoneData;
    @FXML private TextField emailData;
    @FXML private TextField passwordData;
    @FXML private DatePicker birthDateData;
    @FXML private TextField usernameData;
    @FXML private Label missingValuesMsg;

    // Create user button
    @FXML
    private void createUser(ActionEvent event) throws IOException {

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
                    missingValuesMsg.setText(javaPostgreSql.createUser(firstNameText, lastNameText, usernameText, passwordText, emailText, phoneText, positionText, birthDateText.toString()));
                    switchToUsers(event);
                    Alert a = new Alert(Alert.AlertType.INFORMATION);
                    a.setContentText("User "+ firstNameText + " " + lastNameText + " was created successfully!");
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

    //Cancel button
    @FXML
    private void switchToUsers(ActionEvent event) throws IOException {
        Main.switchToUsers(loggedInUser, event);
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        positionData.setItems(FXCollections.observableArrayList("doctor", "nurse", "receptionist", "admin"));
    }

    public void initData(User user) {
        loggedInUser = user;

    }
}
