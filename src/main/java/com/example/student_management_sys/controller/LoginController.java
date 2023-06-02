package com.example.student_management_sys.controller;


import com.example.student_management_sys.model.ConnectionDatabase;

import com.example.student_management_sys.model.DatabaseModel;
import com.example.student_management_sys.model.Student;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class LoginController {

    //Chức năng đăng nhập và đăng xuất
    private  String username;

    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordPasswordField;
    @FXML
    private Button homeButton;
    @FXML
    private MenuItem exitButton;
    @FXML
    private MenuButton buttonAccount;
    @FXML
    private MenuButton menuButton;


    @FXML
    private TableView tableView;

    public void setAccountName(String accountName) {
        buttonAccount.setText(accountName);
    }

    private Stage primaryStage;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    public void loginButtonClicked() {

        try {
            username=usernameTextField.getText();
            Connection databaseConnection = ConnectionDatabase.getConnection();

                String nameQuery = "SELECT Name_CN FROM caNhan,sinhVien WHERE sinhVien.CCCD = caNhan.CCCD AND sinhVien.Ma_SV='" + username + "'";
                Statement statement = databaseConnection.createStatement();
                ResultSet nameResultSet = statement.executeQuery(nameQuery);
                String nameAccount = "";
                if (nameResultSet.next()) {
                    nameAccount = nameResultSet.getString("Name_CN");
                }
                nameResultSet.close();

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/student_management_sys/view/home_view.fxml"));

                Parent root = loader.load();

                LoginController homeController = loader.getController();
                homeController.setAccountName(nameAccount);

                Scene scene = new Scene(root, 1424, 750);

                Stage homeStage = new Stage();
                homeStage.setScene(scene);
                homeStage.setTitle("Hệ thống quản lý sinh viên");

                Stage loginStage = (Stage) homeButton.getScene().getWindow();
                loginStage.close();
                homeStage.show();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error access");
            System.out.println(e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
        @FXML
        public void exitButtonClicked () {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/student_management_sys/view/login_view.fxml"));
                Parent root = loader.load();

                Stage loginStage = new Stage();
                loginStage.setTitle("Đăng nhập hệ thống");
                loginStage.setScene(new Scene(root, 600, 400));
                loginStage.show();

                Stage currentStage = (Stage) exitButton.getParentPopup().getOwnerWindow();
                currentStage.close();

            } catch (IOException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Lỗi");
                alert.setHeaderText(null);
                alert.setContentText("Đã xảy ra lỗi. Vui lòng thử lại!");
                alert.showAndWait();
            }
        }
    }
