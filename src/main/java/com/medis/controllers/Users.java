package com.medis.controllers;

import com.medis.Main;
import com.medis.models.JavaPostgreSql;
import com.medis.models.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Users implements Initializable {

    private User loggedInUser;
    private User selectedUser;
    private final JavaPostgreSql javaPostgreSql = JavaPostgreSql.getInstance();

    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, String> firstName;
    @FXML private TableColumn<User, String> lastName;
    @FXML private TableColumn<User, String> position;
    @FXML private TableColumn<User, Boolean> deleted;
    @FXML private TableColumn<User, Long> id;
    @FXML private TextField searchUserfield;
    @FXML private Label searchLabel;

    public void addUser(MouseEvent event) throws IOException {
        Main.switchToUserCreation(loggedInUser, event);
    }

    // Log out
    @FXML
    private void userLogOut(ActionEvent event) throws IOException {
        Main.switchToLogout(null, event);
    }

    @FXML
    private void switchToUserInfo(ActionEvent event) throws IOException {
        Main.switchToUserInfo(loggedInUser, selectedUser, event);
    }

    private void matchSearch(String newValue, Pattern textCols, Pattern numCols) {
        Matcher textMatcher = textCols.matcher(newValue);
        Matcher numberMatcher = numCols.matcher(newValue);
        if (newValue.equals("")) {
            searchLabel.setText("");
            if (usersTable.getItems().size() == 0) {
                usersTable.setItems(javaPostgreSql.getAllUsers());
            }
        } else {
            if (textMatcher.find()) {
                System.out.println(textMatcher.group(1) + " " + textMatcher.group(2));
                String col = textMatcher.group(1).toLowerCase().replace(" ", "_");
                usersTable.setItems(javaPostgreSql.filterUsers(col, textMatcher.group(2).toLowerCase()));
                searchLabel.setTextFill(Color.color(0, 0.6, 0));
                searchLabel.setText("Searching over column: " + col + "string: " + textMatcher.group(2));
                return;
            }
            if (numberMatcher.find()) {
                System.out.println(numberMatcher.group(1) + " " + numberMatcher.group(2));
                System.out.println();
                usersTable.setItems( javaPostgreSql.filterUsersById(Long.parseLong(numberMatcher.group(2))));
                searchLabel.setTextFill(Color.color(0, 0.6, 0));
                searchLabel.setText("Searching over column: id, number: " + numberMatcher.group(2));
                return;
            }

            searchLabel.setTextFill(Color.color(0.8, 0, 0));
            searchLabel.setText("Search pattern not matched try:\n<first name/last name/id/position>:<number/text>");


        }
    }

    private void addButtonToTable() {

        TableColumn<User, Void> details  = new TableColumn<>();

        Callback<TableColumn<User, Void>, TableCell<User, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<User, Void> call(final TableColumn<User, Void> param) {
                return new TableCell<>() {

                    private final Button openButton = new Button("Open");
                    {
                        openButton.setOnAction((ActionEvent event) -> {

                            selectedUser = javaPostgreSql.getUserById(usersTable.getItems().get(getIndex()).getId());

                            try {
                                switchToUserInfo(event);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(openButton);
                        }
                    }
                };
            }
        };

        details.setCellFactory(cellFactory);

        usersTable.getColumns().add(details);


    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        firstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        position.setCellValueFactory(new PropertyValueFactory<>("position"));
        deleted.setCellValueFactory(new PropertyValueFactory<>("deleted"));
        addButtonToTable();
        usersTable.setItems(javaPostgreSql.getAllUsers());
        //
        Pattern textCols = Pattern.compile("^((?i)\\bfirst name\\b|(?i)\\blast name\\b):([A-Za-z\\d ]+)$");
        Pattern numCols = Pattern.compile("^((?i)\\bid\\b):([1-9]\\d{0,18})$");

        searchUserfield.textProperty().addListener((observable, oldValue, newValue) -> matchSearch(newValue, textCols, numCols));

        searchUserfield.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (!newPropertyValue && Objects.equals(searchUserfield.getText(), "")) {
                usersTable.setItems(javaPostgreSql.getAllUsers());
            }
        }
        );
    }

    public void initData(User user) {
        loggedInUser = user;
    }
}
