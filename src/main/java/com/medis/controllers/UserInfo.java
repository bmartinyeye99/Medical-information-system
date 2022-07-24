package com.medis.controllers;

import com.medis.Main;
import com.medis.models.User;
import com.medis.models.JavaPostgreSql;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;

import java.io.IOException;

public class UserInfo {

    private User loggedInUser;
    private User selectedUser;
    JavaPostgreSql javaPostgreSql = new JavaPostgreSql();

    @FXML private Label userNameData;
    @FXML private Label lastNameData;
    @FXML private Label usernameData;
    @FXML private Label emailData;
    @FXML private Label phoneData;
    @FXML private Label positionData;
    @FXML private Label birthDateData;
    @FXML private Label firstNameData;

    // Edit user info button
    @FXML
    public void switchToUserInfoEdit(ActionEvent event) throws IOException {
        Main.switchToUserInfoEdit(loggedInUser, selectedUser, event);

    }

    //Delete user button
    @FXML
    private void deleteUser(ActionEvent event) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setContentText("Are you sure you want to delete user " + selectedUser.getUsername() + "?");
        a.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                javaPostgreSql.deleteUser(selectedUser.getId());
                try {
                    switchToUsers(event);
                    Alert b = new Alert(Alert.AlertType.INFORMATION);
                    b.setContentText("User " + selectedUser.getUsername() + " was deleted successfully!");
                    b.show();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        });
    }

    @FXML
    public void switchToUsers(ActionEvent event) throws IOException {
        Main.switchToUsers(loggedInUser, event);
    }

    public void initData(User user, User user2) {
        loggedInUser = user;
        selectedUser = javaPostgreSql.getUserById(user2.getId());
        userNameData.setText(selectedUser.getUsername());
        firstNameData.setText(selectedUser.getFirstName());
        lastNameData.setText(selectedUser.getLastName());
        usernameData.setText(selectedUser.getUsername());
        emailData.setText(selectedUser.getEmail());
        phoneData.setText(selectedUser.getPhone());
        positionData.setText(selectedUser.getPosition());
        birthDateData.setText(selectedUser.getBirthDateFormatted());
    }
}
