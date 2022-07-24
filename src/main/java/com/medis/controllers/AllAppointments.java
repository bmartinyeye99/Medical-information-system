package com.medis.controllers;

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
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class AllAppointments implements Initializable {

    private User loggedInUser;
    private Patient selectedPatient;
    private Appointment selectedAppointment;
    private final JavaPostgreSql javaPostgreSql = JavaPostgreSql.getInstance();

    @FXML private TableView<Appointment> allAppointmentsTable;
    @FXML private TableColumn<Appointment, String> title;
    @FXML private TableColumn<Appointment, String> doctorOrPatientName;
    @FXML private TableColumn<Appointment, LocalDateTime> startTime;
    @FXML private TableColumn<Appointment, LocalDateTime> endTime;

    // Log out
    @FXML
    private void userLogOut(ActionEvent event) throws IOException {
        Main.switchToLogout(null, event);
    }

    @FXML
    private void switchToPatients(ActionEvent event) throws IOException {
        Main.switchToPatients(loggedInUser, event);
    }

    @FXML
    private void switchToAppointmentEdit(ActionEvent event) throws IOException {
        Main.switchToAppointmentEdit(loggedInUser, selectedPatient, selectedAppointment, "AllAppointments", event);
    }


    private void addButtonToTable() {
        TableColumn<Appointment, Void> details  = new TableColumn<>();

        Callback<TableColumn<Appointment, Void>, TableCell<Appointment, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Appointment, Void> call(final TableColumn<Appointment, Void> param) {
                return new TableCell<>() {

                    private final Button openButton = new Button("Open");
                    {
                        openButton.setOnAction((ActionEvent event) -> {

                            selectedAppointment = javaPostgreSql.getAppointmentById(allAppointmentsTable.getItems().get(getIndex()).getId());
                            selectedPatient = javaPostgreSql.getPatientById(selectedAppointment.getPatientId());
                            System.out.println("selected app: " + selectedAppointment.getId());
                            System.out.println("selected pat: " + selectedPatient.getId());
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

        allAppointmentsTable.getColumns().add(details);


    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        title.setCellValueFactory(new PropertyValueFactory<>("title"));
        doctorOrPatientName.setCellValueFactory(new PropertyValueFactory<>("patientName"));
        startTime.setCellValueFactory(new PropertyValueFactory<>("startTimeFormatted"));
        endTime.setCellValueFactory(new PropertyValueFactory<>("endTimeFormatted"));
        addButtonToTable();
    }

    public void initData(User user) {
        loggedInUser = user;
        if (!loggedInUser.getPosition().equals("doctor"))
            doctorOrPatientName.setCellValueFactory(new PropertyValueFactory<>("doctorName"));

        if (loggedInUser.getPosition().equals("doctor")) {
            doctorOrPatientName.setText("Patient");
            allAppointmentsTable.setItems(javaPostgreSql.getFutureAppointmentsByDoctorId(loggedInUser.getId()));
        }
        else {
            allAppointmentsTable.setItems(javaPostgreSql.getFutureAppointments());
        }

    }
}
