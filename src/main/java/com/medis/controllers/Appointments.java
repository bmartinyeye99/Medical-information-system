package com.medis.controllers;

import com.medis.GeneralLogger;
import com.medis.Main;
import com.medis.models.Appointment;
import com.medis.models.JavaPostgreSql;
import com.medis.models.Patient;
import com.medis.models.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import java.util.logging.Level;

public class Appointments implements Initializable {

    private Patient selectedPatient;
    private User loggedInUser;
    private Appointment selectedAppointment;
    private final JavaPostgreSql javaPostgreSql = JavaPostgreSql.getInstance();


    @FXML private TableView<Appointment> appointmentsTable;
    @FXML private Label patientNameAppointments;
    @FXML private TableColumn<Appointment, String> title;
    @FXML private TableColumn<Appointment, String> doctor;
    @FXML private TableColumn<Appointment, LocalDateTime> startTime;
    @FXML private TableColumn<Appointment, LocalDateTime> endTime;

    // Patient info
    @FXML
    private void switchToPatientInfo(ActionEvent event) throws IOException {
        Main.switchToPatientInfo(loggedInUser, selectedPatient, event);
    }

    // Records
    @FXML
    private void switchToRecords(ActionEvent event) throws IOException {
        if (!loggedInUser.getPosition().equals("receptionist")) {
            Main.switchToRecords(loggedInUser, selectedPatient, event);
        }
        else{
            GeneralLogger.log(Level.WARNING, "ACCESS | DENIED | RECORDS: Denied for " + loggedInUser.getId() );
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setContentText("You don't have permissions to show Records!");
            a.show();
        }
    }

    // Edit buttons
    @FXML
    private void switchToAppointmentEdit(ActionEvent event) throws IOException {
        Main.switchToAppointmentEdit(loggedInUser, selectedPatient, selectedAppointment, "Appointments", event);
    }


    // Plus button
    @FXML
    private void switchToAppointmentCreation(MouseEvent event) throws IOException {
        Main.switchToAppointmentCreation(loggedInUser, selectedPatient, event);
    }


    // Prescriptions
    @FXML
    private void switchToPrescriptions(ActionEvent event) throws IOException {
        if (loggedInUser.getPosition().equals("doctor")) {
            Main.switchToPrescriptions(loggedInUser, selectedPatient, event);
        }
        else{
            GeneralLogger.log(Level.WARNING, "ACCESS | DENIED | PRESCRIPTIONS: Denied for " + loggedInUser.getId() );
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setContentText("You don't have permissions to show Prescriptions!");
            a.show();
        }
    }


    // Close window button
    @FXML
    private void switchToPatients(ActionEvent event) throws IOException {
        Main.switchToPatients(loggedInUser, event);
    }


    private void addButtonToTable() {
        TableColumn<Appointment, Void> details  = new TableColumn<>();

        Callback<TableColumn<Appointment, Void>, TableCell<Appointment, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Appointment, Void> call(final TableColumn<Appointment, Void> param) {
                return new TableCell<>() {

                    private final Button openButton = new Button("Edit");
                    {
                        openButton.setOnAction((ActionEvent event) -> {

                            selectedAppointment = javaPostgreSql.getAppointmentById(appointmentsTable.getItems().get(getIndex()).getId());
                            try {
                                switchToAppointmentEdit(event);
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

        appointmentsTable.getColumns().add(details);

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        title.setCellValueFactory(new PropertyValueFactory<>("title"));
        doctor.setCellValueFactory(new PropertyValueFactory<>("doctorName"));
        startTime.setCellValueFactory(new PropertyValueFactory<>("startTimeFormatted"));
        endTime.setCellValueFactory(new PropertyValueFactory<>("endTimeFormatted"));
        addButtonToTable();
    }


    public void initData(Patient patient, User user) {
        loggedInUser = user;
        selectedPatient = patient;
        patientNameAppointments.setText(selectedPatient.getFirstName() + " " + selectedPatient.getLastName() + "'s appointments");
        appointmentsTable.setItems(javaPostgreSql.getAllNotDeletedAppointmentsByPatientId(selectedPatient.getId()));

    }

}
