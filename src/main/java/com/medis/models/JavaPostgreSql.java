package com.medis.models;

import com.medis.GeneralLogger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;


public class JavaPostgreSql {

    private final String url = "jdbc:postgresql://postgresql.r1.websupport.sk:5432/medis";
    private final String user = "medis";
    private final String pswd = "Uu39FC4W#Z";
    private final char[] hexArray = "0123456789ABCDEF".toCharArray();
    private static JavaPostgreSql instance = new JavaPostgreSql();

    public static JavaPostgreSql getInstance(){
        return instance;
    }

    // Buffer methods

    /**
     * Metoda zahashuje heslo cez algoritmus SHA3-256
     * @param password
     * @return
     * @throws NoSuchAlgorithmException
     */
    private String hashPass(String password) throws NoSuchAlgorithmException {
        final MessageDigest digest = MessageDigest.getInstance("SHA3-256");
        final byte[] hashbytes = digest.digest(
                password.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(hashbytes);
    }

    /**
     * Metoda prevedie byte na hex pre ulozenie do databazy
     * @param hashbytes
     * @return
     */
    private String bytesToHex(byte[] hashbytes) {
        char[] hexChars = new char[hashbytes.length * 2];
        for (int j = 0; j < hashbytes.length; j++) {
            int v = hashbytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * Metoda vrati parsovani datum pre ulozenie do databazy
     * @param birthdate
     * @return
     * @throws ParseException
     */
    private java.sql.Date getDate(String birthdate) throws ParseException {
        Date t = new SimpleDateFormat("yyyy-MM-dd").parse(birthdate);
        return new java.sql.Date(t.getTime());
    }

    /**
     * Metoda skontroluje ci entita ma status vymazany
     * @param id
     * @param query
     * @return
     */
    private Boolean getaBoolean(long id, String query) {
        try {
            Connection connection = DriverManager.getConnection(url, user, pswd);
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setLong(1, id);
            System.out.println(preparedStatement);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next() || resultSet.getBoolean("deleted")) {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Metoda vypise vystup z databazy. Tato metoda je len pomocna metoda.
     * @param resultSet
     * @throws SQLException
     */
    private void printResultSet(ResultSet resultSet) throws SQLException {
        ResultSetMetaData rsmd = resultSet.getMetaData();
        int columnsNumber = rsmd.getColumnCount();
        while (resultSet.next()) {
            for (int i = 1; i <= columnsNumber; i++) {
                if (i > 1) System.out.print(",  ");
                String columnValue = resultSet.getString(i);
                System.out.print(columnValue + " " + rsmd.getColumnName(i));
            }
            System.out.println("");
        }
    }


    // Patient related methods

    /**
     * Metoda vrati vytvoreneho pacienta podla odpovedi databazy
     * @param resultSet
     * @return
     * @throws SQLException
     */
    private Patient createPatientFromResultSet(ResultSet resultSet) throws SQLException {
        Patient patient = new Patient();
        patient.setId(resultSet.getLong("id"));
        patient.setFirstName(resultSet.getString("first_name"));
        patient.setLastName(resultSet.getString("last_name"));
        patient.setBloodGroup(resultSet.getString("blood_group"));
        patient.setSex(resultSet.getString("sex"));
        patient.setAddress(resultSet.getString("address"));
        patient.setEmail(resultSet.getString("email"));
        patient.setInsuranceCompany(resultSet.getString("insurance_company"));
        patient.setPhone(resultSet.getString("phone"));
        patient.setBirthDate(resultSet.getDate("birthdate"));
        patient.setCreatedAt(resultSet.getObject("created_at", LocalDateTime.class));
        patient.setUpdatedAt(resultSet.getObject("updated_at", LocalDateTime.class));
        try {
            patient.setNextVisit(resultSet.getObject("start_time", LocalDateTime.class).toString());
        } catch (Exception e) {
            patient.setNextVisit("0000-00-00 00:00");
        }
        patient.setDeleted(resultSet.getBoolean("deleted"));
        patient.setBirthID(resultSet.getLong("identification_number"));
        return patient;
    }

    /**
     * Metoda vytvory pacienta podla zadanych parametrov
     * @param firstName
     * @param lastName
     * @param insuranceCo
     * @param birthdate
     * @param sex
     * @param bloodGroup
     * @param address
     * @param phone
     * @param email
     * @param birthId
     * @return
     */
    public String createPatient(String firstName, String lastName, String insuranceCo, String birthdate, String sex, String bloodGroup, String address, String phone, String email, String birthId) {
        String query = "INSERT INTO patients VALUES(default, ?, ?, cast(? as insurance_enum), ?, " +
                "cast(? as sex_enum), cast(? as blood_enum), ?, ?, now(), now(), false, ?, ?);";
        try {
            Connection connection = DriverManager.getConnection(url, user, pswd);
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setString(3, insuranceCo);
            preparedStatement.setDate(4, getDate(birthdate));
            preparedStatement.setString(5, sex);
            preparedStatement.setString(6, bloodGroup);
            preparedStatement.setString(7, address);
            preparedStatement.setString(8, phone);
            preparedStatement.setString(9, email);
            preparedStatement.setString(10, birthId);
            System.out.println(preparedStatement);
            preparedStatement.executeUpdate();
            GeneralLogger.log(Level.INFO, "PATIENT | CREATE: Patient " + email + " created");
            return "Succesfully created patient!";
        } catch (SQLException e) {
            e.printStackTrace();
            return "SQLException: " + e;
        } catch (ParseException e) {
            e.printStackTrace();
            return "ParseException: " + e;
        }
    }

    /**
     * Metoda aktualizuje udaje pacienta podla ID pacienta
     *
     * @param id
     * @param firstName
     * @param lastName
     * @param insuranceCo
     * @param birthdate
     * @param sex
     * @param bloodGroup
     * @param address
     * @param phone
     * @param email
     * @param identificationNumber
     */
    public void updatePatient(long id, String firstName, String lastName, String insuranceCo, String birthdate, String sex, String bloodGroup, String address, String phone, String email, String identificationNumber) {
        if (!isPatientExist(id)) {
            GeneralLogger.log(Level.WARNING, "PATIENT | UPDATE | FAILED: Patient " + email + " not found");
            System.out.println("Patient with this id not exists!");
        }
        else {
            String query1 = "UPDATE patients SET first_name=?, last_name=?, insurance_company=cast(? as insurance_enum), birthdate=?, " +
                    "sex=cast(? as sex_enum), blood_group=cast(? as blood_enum), phone=?, address=?, email=?, updated_at=now(), identification_number=?  WHERE id=?;";
            try {
                Connection connection = DriverManager.getConnection(url, user, pswd);
                PreparedStatement preparedStatement = connection.prepareStatement(query1);
                preparedStatement.setString(1, firstName);
                preparedStatement.setString(2, lastName);
                preparedStatement.setString(3, insuranceCo);
                preparedStatement.setDate(4, getDate(birthdate));
                preparedStatement.setString(5, sex);
                preparedStatement.setString(6, bloodGroup);
                preparedStatement.setString(7, phone);
                preparedStatement.setString(8, address);
                preparedStatement.setString(9, email);
                preparedStatement.setString(10, identificationNumber);
                preparedStatement.setLong(11, id);
                System.out.println(preparedStatement);
                int res = preparedStatement.executeUpdate();
                System.out.println("Succesfully updated " + res + " row!");
                GeneralLogger.log(Level.INFO, "PATIENT | UPDATE: Patient " + email + " updated");
                System.out.println("Succesfully updated patient with id=" + id + " !");
            }
            catch (SQLException | ParseException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Metoda zmeny status pacienta na vymazany
     * @param id
     * @return
     */
    public String deletePatient(long id) {
        if (!isPatientExist(id)) {
            return "Patient with this id not exists!";
        } else {
            String query1 = "UPDATE patients SET deleted=true, updated_at=now() WHERE id=?";
            try {
                Connection connection = DriverManager.getConnection(url, user, pswd);
                PreparedStatement preparedStatement = connection.prepareStatement(query1);
                preparedStatement.setLong(1, id);
                System.out.println(preparedStatement);
                int res = preparedStatement.executeUpdate();
                System.out.println("Succesfully updated " + res + " row!");
                String email = getEmailByPatientId(id);
                GeneralLogger.log(Level.INFO, "PATIENT | DELETE: Patient " + email + " deleted");
            } catch (SQLException e) {
                e.printStackTrace();
                return "SQLException: " + e;
            }
            return "Patient deleted!";
        }
    }

    /**
     * Metoda skontroluje ci pacient existuje
     * @param id
     * @return
     */
    private Boolean isPatientExist(long id) {
        String query = "SELECT * FROM patients WHERE id=?;";
        return getaBoolean(id, query);
    }

    /**
     *  Metoda vrati pacienta podla ID pacienta
     * @param patientId
     * @return
     */
    public Patient getPatientById(Long patientId) {
        Patient result = new Patient();
        try {
            String query = "SELECT * FROM patients WHERE id=?";
            Connection connection = DriverManager.getConnection(url, user, pswd);
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setLong(1, patientId);
            ResultSet resultSet = preparedStatement.executeQuery();
            System.out.println(preparedStatement);
            resultSet.next();
            result = createPatientFromResultSet(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     *  Metoda vrati zoznam nevymazanych pacientov
     * @return
     */
    public ObservableList<Patient> getAllNotDeletedPatients() {
        String query = "select patients.*, appointments.start_time from patients left join appointments on appointments.patient_id = patients.id and appointments.deleted=false and appointments.start_time > now() where patients.deleted=false;";
        ObservableList<Patient> result = FXCollections.observableArrayList();
        try {
            Connection connection = DriverManager.getConnection(url, user, pswd);
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                result.add(createPatientFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Metoda vrati zoznam pacientov podla zadanych filtrov
     * @param column
     * @param filterWord
     * @return
     */
    public ObservableList<Patient> getAllNotDeletedPatientsFiltered(String column, String filterWord) {
        try {
            if (!Objects.equals(column, "first_name") && !Objects.equals(column, "last_name") && !Objects.equals(column, "position")) {
                throw new Exception("Invalid column provided");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String query = "select patients.*, appointments.start_time from patients left join appointments on appointments.patient_id = patients.id and appointments.deleted=false and appointments.start_time > now() where patients.deleted=false AND LOWER(patients." + column + ") LIKE ?;";
        ObservableList<Patient> result = FXCollections.observableArrayList();
        try {
            Connection connection = DriverManager.getConnection(url, user, pswd);
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, "%" + filterWord + "%");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                result.add(createPatientFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Metoda vrati zoznam pacientov podla ID
     * @param id
     * @return
     */
    public ObservableList<Patient> getAllNotDeletedPatientsFiltered(String id) {
        String query = "select patients.*, appointments.start_time from patients left join appointments on appointments.patient_id = patients.id and appointments.deleted=false and appointments.start_time > now() where patients.deleted=false AND patients.identification_number LIKE ?;";
        ObservableList<Patient> result = FXCollections.observableArrayList();
        try {
            Connection connection = DriverManager.getConnection(url, user, pswd);
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                result.add(createPatientFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Metoda vrati email pacienta podla ID pacienta
     * @param patientId
     * @return
     */
    private String getEmailByPatientId(long patientId) {
        String result = "";
        try {
            String query = "SELECT * FROM patients WHERE id=?;";
            Connection connection = DriverManager.getConnection(url, user, pswd);
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setLong(1, patientId);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            result = resultSet.getString("email");
            System.out.println(preparedStatement);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }



    // User related methods

    /**
     * Metoda vrati vytvoreneho pouzivatela podla odpovedi databazy
     * @param resultSet
     * @return
     * @throws SQLException
     */
    private User createUserFromResultSet(ResultSet resultSet) throws SQLException {
        User obj = new User();
        obj.setId(resultSet.getLong("id"));
        obj.setFirstName(resultSet.getString("first_name"));
        obj.setLastName(resultSet.getString("last_name"));
        obj.setUsername(resultSet.getString("username"));
        obj.setEmail(resultSet.getString("email"));
        obj.setPhone(resultSet.getString("phone"));
        obj.setPosition(resultSet.getString("position"));
        obj.setBirthDate(LocalDateTime.from(resultSet.getTimestamp("birthdate").toLocalDateTime()));
        obj.setCreatedAt(LocalDateTime.from(resultSet.getTimestamp("created_at").toLocalDateTime()));
        obj.setUpdatedAt(LocalDateTime.from(resultSet.getTimestamp("updated_at").toLocalDateTime()));
        obj.setDeleted(resultSet.getBoolean("deleted"));
        return obj;
    }

    /**
     * Metoda vytvory pouzivatela podla zadanych parametrov
     * @param firstName
     * @param lastName
     * @param username
     * @param password
     * @param email
     * @param phone
     * @param position
     * @param birthdate
     * @return
     */
    public String createUser(String firstName, String lastName, String username, String password, String email, String phone, String position, String birthdate) {
        String query = "INSERT INTO users (id, first_name,last_name, username, password, email, phone, position,birthdate, created_at, updated_at,deleted) VALUES(default, ?, ?, ?, ?, ?, ?, cast(? as position_enum), ?, now(), now(), false);";
        try {
            Connection connection = DriverManager.getConnection(url, user, pswd);
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            //adding values
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setString(3, username);
            preparedStatement.setString(4, hashPass(password));
            preparedStatement.setString(5, email);
            preparedStatement.setString(6, phone);
            preparedStatement.setString(7, position);
            preparedStatement.setDate(8, getDate(birthdate));
            System.out.println(preparedStatement);
            preparedStatement.executeUpdate();
            System.out.println("Succesfully created user!");
            GeneralLogger.log(Level.INFO, "REGISTER: User " + email + " created");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            System.out.println("Wrong date format");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return query;
    }

    /**
     * Metoda aktualizuje udaje pouzivatela podla ID pouzivatela
     *
     * @param id
     * @param username
     * @param firstName
     * @param lastName
     * @param password
     * @param email
     * @param phone
     * @param position
     * @param birthdate
     */
    // update application users
    public void updateUser(long id, String username, String firstName, String lastName, String password, String email, String phone, String position, String birthdate) {
        if (!isUserExist(id)) {
            GeneralLogger.log(Level.WARNING, "USER | UPDATE | FAILED: User " + email + " not found");
            System.out.println("User with this id not exists!");
        } else {
            String query1 = "UPDATE users SET first_name=?, last_name=?, username=?, password=?, email=?, phone=?, birthdate=?, position=cast(? AS position_enum), updated_at=now()  WHERE id=?;";
            try {
                Connection connection = DriverManager.getConnection(url, user, pswd);
                PreparedStatement preparedStatement = connection.prepareStatement(query1);
                preparedStatement.setString(1, firstName);
                preparedStatement.setString(2, lastName);
                preparedStatement.setString(3, username);
                preparedStatement.setString(4, hashPass(password));
                preparedStatement.setString(5, email);
                preparedStatement.setString(6, phone);
                preparedStatement.setDate(7, getDate(birthdate));
                preparedStatement.setString(8, position);
                preparedStatement.setLong(9, id);

                System.out.println(preparedStatement);
                int res = preparedStatement.executeUpdate();
                System.out.println("Succesfully updated " + res + " row!");
                GeneralLogger.log(Level.INFO, "USER | UPDATE: User " + email + " updated");
                System.out.println("Succesfully updated user!");
            }
            catch (SQLException | ParseException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Metoda zmeny status pouzivatela na vymazany
     *
     * @param id
     */
    public void deleteUser(long id) {
        if (!isUserExist(id)) {
            GeneralLogger.log(Level.WARNING, "USER | UPDATE | FAILED: User with id " + id + " not found");
            System.out.println("User with this id not exists!");
        }
        else {
            String query1 = "UPDATE users SET deleted=true, updated_at=now() WHERE id=?";
            try {
                Connection connection = DriverManager.getConnection(url, user, pswd);
                PreparedStatement preparedStatement = connection.prepareStatement(query1);
                preparedStatement.setLong(1, id);
                System.out.println(preparedStatement);
                int res = preparedStatement.executeUpdate();
                String email = getEmailByUserId(id);
                GeneralLogger.log(Level.INFO, "USER | DELETE: User " + email + " deleted");
                System.out.println("Succesfully updated " + res + " row!");
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
            System.out.println("User deleted!");
        }
    }

    /**
     * Metoda skontroluje ci pouzivatel existuje
     * @param id
     * @return
     */
    private Boolean isUserExist(long id) {
        String query = "SELECT * FROM users WHERE id=?;";
        return getaBoolean(id, query);
    }

    /**
     * Metoda vrati pouzivatela podla ID pouzivatela
     * @param userId
     * @return
     */
    public User getUserById(Long userId) {
        User result = new User();
        try {
            String query = "SELECT * FROM users WHERE id=?;";
            Connection connection = DriverManager.getConnection(url, user, pswd);
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setLong(1, userId);
            System.out.println(preparedStatement);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            result = createUserFromResultSet(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Metoda vrati zoznam pouzivatelov podla zadanych filtrov
     * @param column
     * @param filterWord
     * @return
     */
    public ObservableList<User> filterUsers(String column, String filterWord) {
        try {
            if (!Objects.equals(column, "first_name") && !Objects.equals(column, "last_name") && !Objects.equals(column, "position")) {
                throw new Exception("Invalid column provided");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ObservableList<User> result = FXCollections.observableArrayList();
        String query = "SELECT * FROM users WHERE LOWER(" + column + ") LIKE ?;";
        try {
            Connection connection = DriverManager.getConnection(url, user, pswd);
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, "%" + filterWord + "%");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                result.add(createUserFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Metoda vrati zoznam pouzivatelov podla ID pouzivatela
     * @param id
     * @return
     */
    public ObservableList<User> filterUsersById(Long id) {
        ObservableList<User> result = FXCollections.observableArrayList();
        String query = "SELECT * FROM users WHERE id = ?;";
        try {
            Connection connection = DriverManager.getConnection(url, user, pswd);
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                result.add(createUserFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     *  Metoda vrati zoznam pouzivatelov
     * @return
     */
    public ObservableList<User> getAllUsers() {
        String query = "SELECT * from users;";
        ObservableList<User> result = FXCollections.observableArrayList();
        try {
            Connection connection = DriverManager.getConnection(url, user, pswd);
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                result.add(createUserFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Metoda vrati zoznam nevymazanych pouzivatelov podla pozicie
     * @param position
     * @return
     */
    public ObservableList<String> getUsersByPosition(String position) {
        String query = "SELECT * from users WHERE position=cast(? as position_enum) and deleted=false;";
        ObservableList<String> result = FXCollections.observableArrayList();
        try {
            Connection connection = DriverManager.getConnection(url, user, pswd);
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, position);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                User a = createUserFromResultSet(resultSet);
                result.add(a.getFirstName() + " " + a.getLastName());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Metoda vrati pouzivatela podla emailu alebo pouzivatelskeho mena
     * @param identification
     * @return
     */
    public User getUserByEmailOrUsername(String identification) {
        User result = new User();
        try {
            String query = "SELECT * FROM users WHERE email=? or username=?;";
            Connection connection = DriverManager.getConnection(url, user, pswd);
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, identification);
            preparedStatement.setString(2, identification);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            result = createUserFromResultSet(resultSet);
            System.out.println(preparedStatement);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     *  Metoda vrati pouzivatela podla mena
     * @param firstName
     * @param lastName
     * @return
     */
    public User getUserByFirstAndLastName(String firstName, String lastName) {
        User result = new User();
        try {
            String query = "SELECT * FROM users WHERE first_name=? and last_name=?;";
            Connection connection = DriverManager.getConnection(url, user, pswd);
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            result = createUserFromResultSet(resultSet);
            System.out.println(preparedStatement);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Metoda vrati email pouzivatela podla ID pouzivatela
     * @param userId
     * @return
     */
    private String getEmailByUserId(long userId) {
        String result = "";
        try {
            String query = "SELECT * FROM users WHERE id=?;";
            Connection connection = DriverManager.getConnection(url, user, pswd);
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setLong(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            result = resultSet.getString("email");
            System.out.println(preparedStatement);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Metoda skontroluje ci dany pouzivatel existuje v databaze
     * @param identification
     * @param password
     * @return
     */
    // login checker
    public boolean checkUser(String identification, String password) {
        String query = "SELECT id, first_name, last_name, username, deleted, position FROM users WHERE (email=? and password=?) or (username=? and password=?) and deleted=false;";
        try {
            Connection connection = DriverManager.getConnection(url, user, pswd);
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, identification);
            preparedStatement.setString(2, hashPass(password));
            preparedStatement.setString(3, identification);
            preparedStatement.setString(4, hashPass(password));
            System.out.println(preparedStatement);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {
                System.out.println("User not found in DB!");
                GeneralLogger.log(Level.WARNING, "LOGIN | USER | FAILED: Unsuccessful login | " + identification + " | " + password);
                return false;
            } else {
                List<User> result = new ArrayList<>();
                while (resultSet.next()) {
                    result.add(createUserFromResultSet(resultSet));
                }
                GeneralLogger.log(Level.INFO, "LOGIN | USER: User " + identification + " logged in");
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return false;
    }


    // Appointment related methods

    /**
     * Metoda vrati vytvorenu schodzu podla odpovedi databazy
     * @param resultSet
     * @return
     * @throws SQLException
     */
    private Appointment createAppointmentFromResultSet(ResultSet resultSet) throws SQLException {
        Appointment appointment = new Appointment();
        appointment.setId(resultSet.getLong("id"));
        appointment.setTitle(resultSet.getString("title"));
        appointment.setDescription(resultSet.getString("description"));
        appointment.setStartTime(resultSet.getObject("start_time", LocalDateTime.class));
        appointment.setEndTime(resultSet.getObject("end_time", LocalDateTime.class));
        appointment.setPatientId(resultSet.getLong("patient_id"));
        appointment.setDoctorId(resultSet.getLong("doctor_id"));
        try {
            appointment.setPatientName(resultSet.getString("first_name"), resultSet.getString("last_name"));
        } catch (Exception e) {
            appointment.setPatientName("","");
        }

        appointment.setDoctorName(this.getUserById(resultSet.getLong("doctor_id")).getFullName());
        appointment.setCreatedAt(resultSet.getObject("created_at", LocalDateTime.class));
        appointment.setUpdatedAt(resultSet.getObject("updated_at", LocalDateTime.class));
        appointment.setDeleted(resultSet.getBoolean("deleted"));
        appointment.setCreatedBy(resultSet.getLong("created_by"));
        return appointment;
    }

    /**
     * Metoda vytvory schodzu podla zadanych parametrov
     *
     * @param title
     * @param description
     * @param startTime
     * @param endTime
     * @param patientId
     * @param doctorId
     * @param createdBy
     */
    public void creteAppointment(String title, String description, String startTime, String endTime, long patientId, long doctorId, long createdBy) {
        String query = "INSERT INTO appointments VALUES(default, ?, ?, ?, ?, ?, ?, now(), now(),  false, ?);";
        try {
            Connection connection = DriverManager.getConnection(url, user, pswd);
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, title);
            preparedStatement.setString(2, description);
            preparedStatement.setTimestamp(3, Timestamp.valueOf(LocalDateTime.parse(startTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))));
            preparedStatement.setTimestamp(4, Timestamp.valueOf(LocalDateTime.parse(endTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))));
            preparedStatement.setLong(5, patientId);
            preparedStatement.setLong(6, doctorId);
            preparedStatement.setLong(7, createdBy);
            System.out.println(preparedStatement);
            preparedStatement.executeUpdate();
            GeneralLogger.log(Level.INFO, "APPOINTMENT | CREATE: Appointment " + title + " created");
            System.out.println("Succesfully created appointment!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metoda aktualizuje udaje schodzy podla ID schodzy
     *
     * @param id
     * @param title
     * @param description
     * @param startTime
     * @param endTime
     * @param patientId
     * @param doctorId
     */
    public void updateAppointment(long id, String title, String description, String startTime, String endTime, long patientId, long doctorId) {
        if (!isAppointmentExist(id)) {
            GeneralLogger.log(Level.WARNING, "APPOINTMENT | UPDATE | FAILED: Appointment " + title + " not found");
        } else {
            String query = "UPDATE appointments SET title=?, description=?, start_time=?, end_time=?, patient_id=?, doctor_id=?, updated_at=now() WHERE id=?";
            try {
                Connection connection = DriverManager.getConnection(url, user, pswd);
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, title);
                preparedStatement.setString(2, description);
                preparedStatement.setTimestamp(3, Timestamp.valueOf(LocalDateTime.parse(startTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))));
                preparedStatement.setTimestamp(4, Timestamp.valueOf(LocalDateTime.parse(endTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))));
                preparedStatement.setLong(5, patientId);
                preparedStatement.setLong(6, doctorId);
                preparedStatement.setLong(7, id);
                System.out.println(preparedStatement);
                preparedStatement.executeUpdate();
                GeneralLogger.log(Level.INFO, "APPOINTMENT | UPDATE: Appointment " + title + " updated");
                System.out.println("Succesfully updated appointment!");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Metoda zmeny status schodzy na vymazany
     *
     * @param id
     */
    public void deleteAppointment(long id) {
        if (!isAppointmentExist(id)) {
        } else {
            String query = "UPDATE appointments SET deleted=true WHERE id=?";
            try {
                Connection connection = DriverManager.getConnection(url, user, pswd);
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setLong(1, id);
                System.out.println(preparedStatement);
                int res = preparedStatement.executeUpdate();
                System.out.println("Succesfully updated " + res + " row!");
                String title = getTitleByAppointmentId(id);
                GeneralLogger.log(Level.INFO, "APPOINTMENT | DELETE: Appointment " + title + " deleted");
                System.out.println("Succesfully deleted appointment!");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Metoda skontroluje ci schodza existuje
     * @param id
     * @return
     */
    private Boolean isAppointmentExist(long id) {
        String query = "SELECT * FROM appointments WHERE id=?;";
        return getaBoolean(id, query);
    }

    /**
     * Metoda vrati schodzu podla ID schodzy
     * @param appointmentId
     * @return
     */
    public Appointment getAppointmentById(Long appointmentId) {
        Appointment result = new Appointment();
        try {
            String query = "SELECT appointments.*, patients.first_name, patients.last_name  FROM appointments LEFT JOIN patients ON appointments.patient_id = patients.id WHERE appointments.id=?;";
            Connection connection = DriverManager.getConnection(url, user, pswd);
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setLong(1, appointmentId);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            result = createAppointmentFromResultSet(resultSet);
            System.out.println(preparedStatement);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Metoda vrati vsetky nasledujuce schodzy podla ID doktora
     * @param doctorId
     * @return
     */
    public ObservableList<Appointment> getFutureAppointmentsByDoctorId(long doctorId){
        String query = "SELECT appointments.*, patients.first_name, patients.last_name  FROM appointments left join patients on appointments.patient_id = patients.id WHERE appointments.start_time>=now() AND doctor_id=? AND appointments.deleted=false AND patients.deleted=false ORDER BY start_time ASC;";
        ObservableList<Appointment> result = FXCollections.observableArrayList();
        try {
            Connection connection = DriverManager.getConnection(url, user, pswd);
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setLong(1, doctorId);
            System.out.println(preparedStatement);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                result.add(createAppointmentFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Metoda vrati vsetky nasledujuce schodzy
     * @return
     */
    public ObservableList<Appointment> getFutureAppointments(){
        String query = "SELECT appointments.*, patients.first_name, patients.last_name  FROM appointments LEFT JOIN patients ON appointments.patient_id = patients.id WHERE appointments.start_time>=now() AND appointments.deleted=false AND patients.deleted=false ORDER BY start_time ASC;";
        ObservableList<Appointment> result = FXCollections.observableArrayList();
        try {
            Connection connection = DriverManager.getConnection(url, user, pswd);
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            System.out.println(preparedStatement);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                result.add(createAppointmentFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Metoda vrati nazov schodzy podla ID schodzy
     * @param appointmentId
     * @return
     */
    private String getTitleByAppointmentId(long appointmentId) {
        String result = "";
        try {
            String query = "SELECT * FROM appointments WHERE id=?;";
            Connection connection = DriverManager.getConnection(url, user, pswd);
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setLong(1, appointmentId);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            result = resultSet.getString("title");
            System.out.println(preparedStatement);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Metoda vrati zoznam nevymazanych schodz podla ID pacienta
     * @param patientId
     * @return
     */
    public ObservableList<Appointment> getAllNotDeletedAppointmentsByPatientId(long patientId) {
        String query = "SELECT * FROM appointments WHERE start_time>=now() AND patient_id=? AND deleted=false ORDER BY start_time ASC;";
        ObservableList<Appointment> result = FXCollections.observableArrayList();
        try {
            Connection connection = DriverManager.getConnection(url, user, pswd);
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setLong(1, patientId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                result.add(createAppointmentFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }


    // Prescription related methods

    /**
     *  Metoda vrati vytvoreny recept podla odpovedi databazy
     * @param resultSet
     * @return
     * @throws SQLException
     */
    private Prescription createPrescriptionFromResultSet(ResultSet resultSet) throws SQLException {
        Prescription obj = new Prescription();
        obj.setId(resultSet.getLong("id"));
        obj.setTitle(resultSet.getString("title"));
        obj.setDescription(resultSet.getString("description"));
        obj.setDrug(resultSet.getString("drug"));
        obj.setNotes(resultSet.getString("notes"));
        obj.setPatientId(resultSet.getLong("patient_id"));
        obj.setDoctorId(resultSet.getLong("doctor_id"));
        obj.setDoctorName(this.getUserById(resultSet.getLong("doctor_id")).getFullName());
        obj.setExpirationDate(resultSet.getDate("expiration_date"));
        obj.setCreatedAt(LocalDateTime.from(resultSet.getTimestamp("created_at").toLocalDateTime()));
        obj.setUpdatedAt(LocalDateTime.from(resultSet.getTimestamp("updated_at").toLocalDateTime()));
        obj.setDeleted(resultSet.getBoolean("deleted"));
        return obj;
    }

    /**
     * Metoda vytvory predpis podla zadanych parametrov
     *
     * @param title
     * @param description
     * @param drug
     * @param expirationDate
     * @param patientId
     * @param doctorId
     * @param notes
     */
    public void createPrescription(String title, String description, String drug, String expirationDate, long patientId, long doctorId, String notes) {
        String query = "INSERT INTO prescriptions VALUES(default, ?, ?, ?, ?, ?, ?, ?, now(), now(),  false);";
        try {
            Connection connection = DriverManager.getConnection(url, user, pswd);
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, title);
            preparedStatement.setString(2, description);
            preparedStatement.setString(3, drug);
            preparedStatement.setDate(4, getDate(expirationDate));
            preparedStatement.setLong(5, patientId);
            preparedStatement.setLong(6, doctorId);
            preparedStatement.setString(7, notes);
            System.out.println(preparedStatement);
            preparedStatement.executeUpdate();
            GeneralLogger.log(Level.INFO, "PRESCRIPTION | CREATE: Prescription " + title + " created");
            System.out.println("Succesfully created prescription!");
        }
        catch (SQLException | ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metoda aktualizuje udaje predpisu podla ID predpisu
     *
     * @param id
     * @param title
     * @param description
     * @param drug
     * @param expirationDate
     * @param patientId
     * @param doctorId
     * @param notes
     */
    public void updatePrescription(long id, String title, String description, String drug, String expirationDate, long patientId, long doctorId, String notes) {
        if (!isPrescriptionExist(id)) {
            GeneralLogger.log(Level.WARNING, "PRESCRIPTION | UPDATE | FAILED: Prescription " + title + " not found");
        }
        else {
            String query = "UPDATE prescriptions SET title=?, description=?, drug=?, expiration_date=?, notes=?, patient_id=?, doctor_id=?, updated_at=now() WHERE id=?";
            try {
                Connection connection = DriverManager.getConnection(url, user, pswd);
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, title);
                preparedStatement.setString(2, description);
                preparedStatement.setString(3, drug);
                preparedStatement.setDate(4, getDate(expirationDate));
                preparedStatement.setString(5, notes);
                preparedStatement.setLong(6, patientId);
                preparedStatement.setLong(7, doctorId);
                preparedStatement.setLong(8, id);
                System.out.println(preparedStatement);
                int res = preparedStatement.executeUpdate();
                System.out.println("Succesfully updated " + res + " row!");
                GeneralLogger.log(Level.INFO, "PRESCRIPTION | UPDATE: Prescription " + title + " updated");
                System.out.println("Succesfully updated prescription!");
            }
            catch (SQLException | ParseException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Metoda zmeny status predpisu na vymazany
     *
     * @param id
     */
    public void deletePrescription(long id) {
        if (!isPrescriptionExist(id)) {
            System.out.println("Prescription with this id not exists!");
        }
        else {
            String query = "UPDATE prescriptions SET deleted=true WHERE id=?";
            try {
                Connection connection = DriverManager.getConnection(url, user, pswd);
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setLong(1, id);
                System.out.println(preparedStatement);
                int res = preparedStatement.executeUpdate();
                System.out.println("Succesfully updated " + res + " row!");
                String title = getTitleByPrescriptionId(id);
                GeneralLogger.log(Level.INFO, "PRESCRIPTION | DELETE: Prescription " + title + " deleted");
                System.out.println("Succesfully deleted prescription!");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Metoda skontroluje ci recept existuje
     * @param id
     * @return
     */
    private Boolean isPrescriptionExist(long id) {
        String query = "SELECT * FROM prescriptions WHERE id=?;";
        return getaBoolean(id, query);
    }

    /**
     *  Metoda vrati recept podla ID receptu
     * @param prescriptionId
     * @return
     */
    public Prescription getPrescriptionById(Long prescriptionId) {
        Prescription result = new Prescription();
        try {
            String query = "SELECT * FROM prescriptions WHERE id=?";
            Connection connection = DriverManager.getConnection(url, user, pswd);
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setLong(1, prescriptionId);
            ResultSet resultSet = preparedStatement.executeQuery();
            System.out.println(preparedStatement);
            resultSet.next();
            result = createPrescriptionFromResultSet(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Metoda vrati nazov receptu podla ID receptu
     * @param prescriptionId
     * @return
     */
    private String getTitleByPrescriptionId(long prescriptionId) {
        String result = "";
        try {
            String query = "SELECT * FROM prescriptions WHERE id=?;";
            Connection connection = DriverManager.getConnection(url, user, pswd);
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setLong(1, prescriptionId);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            result = resultSet.getString("title");
            System.out.println(preparedStatement);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Metoda vrati zoznam nevymazanych receptov podla zadanej entity ID
     * @param entity
     * @param id
     * @return
     */
    public ObservableList<Prescription> getAllNotDeletedPrescriptionsByEntityID(String entity, long id) {
        String query = "SELECT * from prescriptions WHERE " + entity + "_id=? AND deleted=false;";
        ObservableList<Prescription> result = FXCollections.observableArrayList();
        try {
            Connection connection = DriverManager.getConnection(url, user, pswd);
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            System.out.println(preparedStatement);
            while (resultSet.next()) {
                result.add(createPrescriptionFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }


    // Record related methods

    /**
     * Metoda vrati vytvoreny zaznam podla odpovedi databazy
     * @param resultSet
     * @return
     * @throws SQLException
     */
    private Record createRecordFromResultSet(ResultSet resultSet) throws SQLException {
        Record record = new Record();
        record.setId(resultSet.getLong("id"));
        record.setTitle(resultSet.getString("title"));
        record.setDescription(resultSet.getString("description"));
        record.setDateExecuted(resultSet.getDate("date_executed"));
        record.setNotes(resultSet.getString("notes"));
        record.setPatientId(resultSet.getLong("patient_id"));
        record.setDoctorId(resultSet.getLong("doctor_id"));
        record.setCreatedAt(resultSet.getObject("created_at", LocalDateTime.class));
        record.setUpdatedAt(resultSet.getObject("updated_at", LocalDateTime.class));
        record.setDeleted(resultSet.getBoolean("deleted"));
        return record;
    }

    /**
     * Metoda vytvory zaznam podla zadanych parametrov
     *
     * @param title
     * @param description
     * @param executeDate
     * @param notes
     * @param patientId
     * @param doctorId
     */
    public void createRecord(String title, String description, String executeDate, String notes, long patientId, long doctorId) {
        String query = "INSERT INTO records VALUES(default, ?, ?, ?, ?, ?, ?, now(), now(),  false);";
        try {
            Connection connection = DriverManager.getConnection(url, user, pswd);
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, title);
            preparedStatement.setString(2, description);
            preparedStatement.setDate(3, getDate(executeDate));
            preparedStatement.setString(4, notes);
            preparedStatement.setLong(5, patientId);
            preparedStatement.setLong(6, doctorId);
            System.out.println(preparedStatement);
            preparedStatement.executeUpdate();
            GeneralLogger.log(Level.INFO, "RECORD | CREATE: Record " + title + " created");
            System.out.println("Succesfully created record!");
        }
        catch (SQLException | ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metoda aktualizuje udaje zaznamu podla ID zaznamu
     *
     * @param id
     * @param title
     * @param description
     * @param executeDate
     * @param notes
     * @param patientId
     * @param doctorId
     */
    public void updateRecord(long id, String title, String description, String executeDate, String notes, long patientId, long doctorId) {
        if (!isRecordExist(id)) {
            GeneralLogger.log(Level.WARNING, "RECORD | UPDATE | FAILED: Record " + title + " not found");
            System.out.println("Record with this id not exists!");
        }
        else {
            String query = "UPDATE records SET title=?, description=?, date_executed=?, notes=?, patient_id=?, doctor_id=?, updated_at=now() WHERE id=?";
            try {
                Connection connection = DriverManager.getConnection(url, user, pswd);
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, title);
                preparedStatement.setString(2, description);
                preparedStatement.setDate(3, getDate(executeDate));
                preparedStatement.setString(4, notes);
                preparedStatement.setLong(5, patientId);
                preparedStatement.setLong(6, doctorId);
                preparedStatement.setLong(7, id);
                System.out.println(preparedStatement);
                int res = preparedStatement.executeUpdate();
                System.out.println("Succesfully updated " + res + " row!");
                GeneralLogger.log(Level.INFO, "RECORD | UPDATE: Record " + title + " updated");
            }
            catch (SQLException | ParseException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Metoda zmeny status zaznamu na vymazany
     *
     * @param id
     */
    public void deleteRecord(long id) {
        if (!isRecordExist(id)) {
            System.out.println("Record with this id not exists!");
        }
        else {
            String query = "UPDATE records SET deleted=true WHERE id=?";
            try {
                Connection connection = DriverManager.getConnection(url, user, pswd);
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setLong(1, id);
                System.out.println(preparedStatement);
                int res = preparedStatement.executeUpdate();
                System.out.println("Succesfully updated " + res + " row!");
                String title = getTitleByRecordId(id);
                GeneralLogger.log(Level.INFO, "RECORD | DELETE: RECORD " + title + " deleted");
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Metoda skontroluje ci zaznam existuje
     * @param id
     * @return
     */
    private Boolean isRecordExist(long id) {
        String query = "SELECT * FROM records WHERE id=?;";
        return getaBoolean(id, query);
    }

    /**
     * Metoda vrati zaznam podla ID zaznamu
     * @param recordId
     * @return
     */
    public Record getRecordById(Long recordId) {
        Record result = new Record();
        try {
            String query = "SELECT * FROM records WHERE id=?;";
            Connection connection = DriverManager.getConnection(url, user, pswd);
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setLong(1, recordId);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            result = createRecordFromResultSet(resultSet);
            System.out.println(preparedStatement);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Metoda vrati nazov zaznamu podla ID zaznamu
     * @param recordId
     * @return
     */
    private String getTitleByRecordId(long recordId) {
        String result = "";
        try {
            String query = "SELECT * FROM records WHERE id=?;";
            Connection connection = DriverManager.getConnection(url, user, pswd);
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setLong(1, recordId);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            result = resultSet.getString("title");
            System.out.println(preparedStatement);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     *  Metoda vrati zoznam nevymazanych zaznamov podla ID pacienta
     * @param patientId
     * @return
     */
    public ObservableList<Record> getAllNotDeletedRecordsByPatientId(long patientId) {
        String query = "SELECT * from records WHERE patient_id=? AND deleted=false;";
        ObservableList<Record> result = FXCollections.observableArrayList();
        try {
            Connection connection = DriverManager.getConnection(url, user, pswd);
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setLong(1, patientId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                result.add(createRecordFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

}

