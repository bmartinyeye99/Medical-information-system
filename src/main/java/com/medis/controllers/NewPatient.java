package com.medis.controllers;

import com.medis.Main;
import com.medis.models.JavaPostgreSql;
import com.medis.models.Patient;
import com.medis.models.User;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class NewPatient implements Initializable {

    private User loggedInUser;
    private final JavaPostgreSql javaPostgreSql = JavaPostgreSql.getInstance();

    @FXML private TextField firstNameData;
    @FXML private TextField lastNameData;
    @FXML private ComboBox<String> insuranceCoData;
    @FXML private TextField birthIdData;
    @FXML private ComboBox<String> sexData;
    @FXML private ComboBox<String> bloodGroupData;
    @FXML private TextField addressData;
    @FXML private TextField phoneData;
    @FXML private TextField emailData;
    @FXML private Label missingValuesMsg;

    // Create patient button
    @FXML
    private void createPatient(ActionEvent event) throws IOException {

        String birthDateFromId;
        String firstNameText = firstNameData.getText();
        String lastNameText = lastNameData.getText();
        String insuranceCompanyText = insuranceCoData.getValue();
        String birthIdText = birthIdData.getText();
        String sexValue = sexData.getValue();
        String bloodGroupValue = bloodGroupData.getValue();
        String addressText = addressData.getText();
        String phoneText = phoneData.getText();
        String emailText = emailData.getText();


        if(!firstNameText.isEmpty() && !lastNameText.isEmpty() && insuranceCompanyText!=null && !birthIdText.isEmpty() && sexValue!=null && bloodGroupValue!=null && !addressText.isEmpty() && !phoneText.isEmpty() && !emailText.isEmpty()){
            if (validationBirthID(birthIdText)){
                birthDateFromId = Patient.getYear(birthIdText) + "-" + Patient.getMonth(birthIdText) + "-" + Patient.getDay(birthIdText);
                if (validationPhone(phoneText)){
                    if (validationEmail(emailText)){
                        missingValuesMsg.setText(javaPostgreSql.createPatient(firstNameText, lastNameText, insuranceCompanyText, birthDateFromId, Patient.getGender(birthIdText), bloodGroupValue, addressText, phoneText, emailText, birthIdText));
                        switchToPatients(event);
                        Alert a = new Alert(Alert.AlertType.INFORMATION);
                        a.setContentText("Patient "+ firstNameText + " " + lastNameText + " was created successfully!");
                        a.show();
                    }
                    else{
                        missingValuesMsg.setText("Please enter valid email!");
                    }
                }
                else{
                    missingValuesMsg.setText("Please enter valid phone number!");
                }
            }else{

                missingValuesMsg.setText("Please enter valid birth id!");
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
    private void switchToPatients(ActionEvent event) throws IOException {
        Main.switchToPatients(loggedInUser, event);
    }

    @FXML
    private void handleInputData() {

        ResourceBundle bundle = ResourceBundle.getBundle("medis", Main.getLocale());
        Stage stage = new Stage();
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile == null) {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setContentText(bundle.getString("patientInfo.import.fail"));
            a.show();
            return;
        }

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(selectedFile);
            doc.getDocumentElement().normalize();
            //System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
            Element element = doc.getDocumentElement();

            element = (Element) element.getElementsByTagName("data").item(0);

            try {
                element.getNodeName();

            } catch (NullPointerException e) {
                element = doc.getDocumentElement();
            }

            if (!element.getNodeName().equals("data")) {
                element = doc.getDocumentElement();
            }

            try {
                System.out.println("What?");
            } catch (NullPointerException e) {
                element = doc.getDocumentElement();
                System.out.println(element.getNodeName());
            }

            try {
                firstNameData.setText(element
                        .getElementsByTagName("firstName")
                        .item(0)
                        .getTextContent());
            } catch (NullPointerException e) {
                System.out.println("firstName not in XML");
            }

            try {
                lastNameData.setText(element
                        .getElementsByTagName("lastName")
                        .item(0)
                        .getTextContent());
            } catch (NullPointerException e) {
                System.out.println("lastName not in XML");
            }
            try {
                lastNameData.setText(element
                        .getElementsByTagName("lastName")
                        .item(0)
                        .getTextContent());
            } catch (NullPointerException e) {
                System.out.println("firstName not in XML");
            }

            try {
                String insurance = element
                        .getElementsByTagName("insuranceCompany")
                        .item(0)
                        .getTextContent();
                if (insurance.matches("Union|Dôvera|VŠZP")) {
                    insuranceCoData.setValue(insurance);
                }
            } catch (NullPointerException e) {
                System.out.println("insurance not in XML");
            }

            try {
                birthIdData.setText(element
                        .getElementsByTagName("birthId")
                        .item(0)
                        .getTextContent());
            } catch (NullPointerException e) {
                System.out.println("birthDate not in XML");
            }

            try {
                String sex = element
                        .getElementsByTagName("sex")
                        .item(0)
                        .getTextContent();
                if (sex.matches("male|female")) {
                    sexData.setValue(sex);
                }
            } catch (NullPointerException e) {
                System.out.println("sex not in XML");
            }

            try {
                String bg = element
                        .getElementsByTagName("bloodGroup")
                        .item(0)
                        .getTextContent();
                if (bg.matches("A|A+|A-|B|B+|B-|AB|AB+|AB-|O|O+|O-")) {
                    bloodGroupData.setValue(bg);
                }
            } catch (NullPointerException e) {
                System.out.println("Blood group not in XML");
            }

            try {
                addressData.setText(element
                        .getElementsByTagName("address")
                        .item(0)
                        .getTextContent());
            } catch (NullPointerException e) {
                System.out.println("address not in XML");
            }

            try {
                phoneData.setText(element
                        .getElementsByTagName("phone")
                        .item(0)
                        .getTextContent());
            } catch (NullPointerException e) {
                System.out.println("phone not in XML");
            }

            try {
                emailData.setText(element
                        .getElementsByTagName("email")
                        .item(0)
                        .getTextContent());
            } catch (NullPointerException e) {
                System.out.println("email not in XML");
            }

        } catch (Exception e) {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setContentText(bundle.getString("patientInfo.import.fail"));
            a.show();
            e.printStackTrace();
        }
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setContentText(bundle.getString("patientInfo.import.success"));
        a.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        insuranceCoData.setItems(FXCollections.observableArrayList("Union", "Dôvera", "VŠZP"));
        sexData.setItems(FXCollections.observableArrayList("male","female"));
        bloodGroupData.setItems(FXCollections.observableArrayList("A","A+","A-","B","B+","B-","AB","AB+","AB-", "O","O+","O-"));

    }
    public void initData(User user) {
        loggedInUser = user;
    }
}
