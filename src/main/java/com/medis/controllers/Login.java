package com.medis.controllers;
import com.medis.Main;
import com.medis.models.User;
import com.medis.models.JavaPostgreSql;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class Login implements Initializable {

    private User loggedInUser;
    private final JavaPostgreSql javaPostgreSql = JavaPostgreSql.getInstance();

    @FXML private Label loginMsg;
    @FXML private TextField loginEmail;
    @FXML private PasswordField loginPassword;
    @FXML private ComboBox<String> localeComboBox;
    @FXML private Label passwordLabel;
    @FXML private Button loginButton;
    // Login button
    @FXML
    private void userLogIn(ActionEvent event) throws IOException {
        checkLogin(event);
    }

    private void checkLogin(ActionEvent event) throws IOException {

        // Locale locale = new Locale(localeComboBox.getValue());


        if (!loginEmail.getText().equals("") && !loginPassword.getText().equals("")) {
            if (javaPostgreSql.checkUser(loginEmail.getText(), loginPassword.getText())) {
                loggedInUser = javaPostgreSql.getUserByEmailOrUsername(loginEmail.getText());
                if (loggedInUser.getPosition().equals("admin")) {
                    Main.switchToUsers(loggedInUser, event);
                }
                else {
                    Main.switchToAllAppointments(loggedInUser, event);
                }
            }


            else
                loginMsg.setText("Username or password are not valid!");
        }

        else if(loginEmail.getText().isEmpty() && loginPassword.getText().isEmpty()) {
            loginMsg.setText("Please enter your email and password.");
        }
        else {
            loginMsg.setText("Username or password are not valid!");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        localeComboBox.getItems().addAll("EN", "SK");
        localeComboBox.setValue("EN");

        localeComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            Main.setLocale(newValue);
            System.out.println(newValue);
            loginButton.setText(ResourceBundle.getBundle("medis", Main.getLocale()).getString("login.button"));
            passwordLabel.setText(ResourceBundle.getBundle("medis", Main.getLocale()).getString("login.password.text"));
        });
    }

    public void initData(User user) {
        loggedInUser = user;
    }
}