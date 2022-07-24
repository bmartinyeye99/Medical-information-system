package com.medis;

import com.medis.controllers.*;
import com.medis.models.*;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

public class Main extends Application {

    private static Locale locale;

    public Main() {
        Main.setLocale("EN");
    }

    public static Locale getLocale() {
        return locale;
    }

    public static void setLocale(Locale locale) {
        Main.locale = locale;
    }

    public static void setLocale(String locale) {
        if (Objects.equals(locale, "SK")) {
            Main.locale = new Locale("sk", "SK");

        }
        if (Objects.equals(locale, "EN")) {
            Main.locale = new Locale("en", "EN");
        }
    }

    public static void switchToUserCreation(User loggedInUser, MouseEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(Main.class.getResource("fxml/new_user.fxml"));
        loader.setResources(ResourceBundle.getBundle("medis", Main.locale));

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Medis");
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);

        NewUser controller = loader.getController();
        controller.initData(loggedInUser);

        stage.show();
    }

    public static void switchToUsers(User loggedInUser, ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(Main.class.getResource("fxml/users.fxml"));
        loader.setResources(ResourceBundle.getBundle("medis", Main.locale));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Medis");
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);

        Users controller = loader.getController();
        controller.initData(loggedInUser);

        stage.show();
    }

    public static void switchToUserInfo(User loggedInUser, User selectedUser, ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(Main.class.getResource("fxml/user_info.fxml"));
        loader.setResources(ResourceBundle.getBundle("medis", Main.locale));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Medis");
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);

        UserInfo controller = loader.getController();
        controller.initData(loggedInUser, selectedUser);

        stage.show();
    }

    public static void switchToUserInfoEdit(User loggedInUser, User selectedUser, ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(Main.class.getResource("fxml/edit_user_info.fxml"));
        loader.setResources(ResourceBundle.getBundle("medis", Main.locale));

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Medis");
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);

        EditUserInfo controller = loader.getController();
        controller.initData(loggedInUser, selectedUser);

        stage.show();
    }

    // Doctor, Nurse, Receptionist

    public static void switchToPatients(User loggedInUser, ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(Main.class.getResource("fxml/patients.fxml"));
        loader.setResources(ResourceBundle.getBundle("medis", Main.locale));

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Medis");
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);

        Patients controller = loader.getController();
        controller.initData(loggedInUser);

        stage.show();
    }

    public static void switchToAllAppointments(User loggedInUser, ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(Main.class.getResource("fxml/all_appointments.fxml"));
        loader.setResources(ResourceBundle.getBundle("medis", Main.locale));

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Medis");
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);

        AllAppointments controller = loader.getController();
        controller.initData(loggedInUser);

        stage.show();
    }

    public static void switchToLogout(User loggedInUser, ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(Main.class.getResource("fxml/login.fxml"));
        loader.setResources(ResourceBundle.getBundle("medis", Main.locale));

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Medis");
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);

        Login controller = loader.getController();
        controller.initData(loggedInUser);

        stage.show();
    }

    // Patient info

    public static void switchToPatientInfo(User loggedInUser, Patient patient, ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(Main.class.getResource("fxml/patient_info.fxml"));
        loader.setResources(ResourceBundle.getBundle("medis", Main.locale));

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Medis");
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);

        PatientInfo controller = loader.getController();
        controller.initData(patient, loggedInUser);

        stage.show();
    }

    public static void switchToPatientInfoEdit(User loggedInUser, Patient patient, ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(Main.class.getResource("fxml/edit_patient_info.fxml"));
        loader.setResources(ResourceBundle.getBundle("medis", Main.locale));

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Edit patient info");
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);

        EditPatientInfo controller = loader.getController();
        controller.initData(patient, loggedInUser);

        stage.show();
    }

    public static void switchToPatientCreation(User loggedInUser, MouseEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(Main.class.getResource("fxml/new_patient.fxml"));
        loader.setResources(ResourceBundle.getBundle("medis", Main.locale));

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("New patient");
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);

        NewPatient controller = loader.getController();
        controller.initData(loggedInUser);

        stage.show();
    }

    // Records

    public static void switchToRecords(User loggedInUser, Patient patient, ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("fxml/records.fxml"));
        loader.setResources(ResourceBundle.getBundle("medis", Main.locale));

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Medis");
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);

        Records controller = loader.getController();
        controller.initData(patient, loggedInUser);

        stage.show();
    }

    public static void switchToRecordDetailed(User loggedInUser, Patient patient, Record record, ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(Main.class.getResource("fxml/detailed_record.fxml"));
        loader.setResources(ResourceBundle.getBundle("medis", Main.locale));

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Record details");
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);

        DetailedRecord controller = loader.getController();
        controller.initData(patient, record, loggedInUser);

        stage.show();
    }

    public static void switchToRecordEdit(User loggedInUser, Patient patient, Record record, ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(Main.class.getResource("fxml/edit_record.fxml"));
        loader.setResources(ResourceBundle.getBundle("medis", Main.locale));

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Edit record");
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);

        EditRecord controller = loader.getController();
        controller.initData(patient, record, loggedInUser);

        stage.show();
    }

    public static void switchToRecordCreation(User loggedInUser, Patient patient, MouseEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(Main.class.getResource("fxml/new_record.fxml"));
        loader.setResources(ResourceBundle.getBundle("medis", Main.locale));

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("New record");
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);

        NewRecord controller = loader.getController();
        controller.initData(patient, loggedInUser);

        stage.show();
    }

    // Appointments

    public static void switchToAppointments(User loggedInUser, Patient patient, ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(Main.class.getResource("fxml/appointments.fxml"));
        loader.setResources(ResourceBundle.getBundle("medis", Main.locale));

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Medis");
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);

        Appointments controller = loader.getController();
        controller.initData(patient, loggedInUser);

        stage.show();
    }

    public static void switchToAppointmentEdit(User loggedInUser, Patient patient, Appointment appointment, String previousScene, ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(Main.class.getResource("fxml/edit_appointment.fxml"));
        loader.setResources(ResourceBundle.getBundle("medis", Main.locale));

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Edit appointment");
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);

        EditAppointment controller = loader.getController();
        controller.initData(patient, appointment, loggedInUser, previousScene);

        stage.show();
    }

    public static void switchToAppointmentCreation(User loggedInUser, Patient patient, MouseEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(Main.class.getResource("fxml/new_appointment.fxml"));
        loader.setResources(ResourceBundle.getBundle("medis", Main.locale));

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("New appointment");
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);

        NewAppointment controller = loader.getController();
        controller.initData(patient, loggedInUser);

        stage.show();
    }

    // Prescriptions

    public static void switchToPrescriptions(User loggedInUser, Patient patient, ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(Main.class.getResource("fxml/prescriptions.fxml"));
        loader.setResources(ResourceBundle.getBundle("medis", Main.locale));

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Medis");
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);

        Prescriptions controller = loader.getController();
        controller.initData(patient, loggedInUser);

        stage.show();
    }

    public static void switchToPrescriptionEdit(User loggedInUser, Patient patient, Prescription prescription, ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(Main.class.getResource("fxml/edit_prescription.fxml"));
        loader.setResources(ResourceBundle.getBundle("medis", Main.locale));

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Edit prescription");
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);

        EditPrescription controller = loader.getController();
        controller.initData(patient, prescription, loggedInUser);

        stage.show();
    }

    public static void switchToPrescriptionCreation(User loggedInUser, Patient patient, MouseEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(Main.class.getResource("fxml/new_prescription.fxml"));
        loader.setResources(ResourceBundle.getBundle("medis", Main.locale));

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("New prescription");
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);

        NewPrescription controller = loader.getController();
        controller.initData(patient, loggedInUser);

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setResizable(false);
        Locale locale = new Locale("EN");
        FXMLLoader loader = new FXMLLoader();
        ResourceBundle bundle = ResourceBundle.getBundle("medis", locale);
        loader.setLocation(Objects.requireNonNull(getClass().getResource("fxml/login.fxml")));
        loader.setResources(bundle);
        Parent root = loader.load();
        primaryStage.setTitle("Medis");
        primaryStage.getIcons().add(new Image(String.valueOf(getClass().getResource("img/logo.png"))));
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}