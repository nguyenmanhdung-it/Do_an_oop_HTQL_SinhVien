package com.example.student_management_sys.controller;

import com.example.student_management_sys.model.DatabaseModel;
import com.example.student_management_sys.model.Student;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.sql.SQLException;

public class StudentController{
    @FXML
    private TextField tfMaSV;
    @FXML
    private TextField tfHoTen;
    @FXML
    private TextField tfGioiTinh;
    @FXML
    private TextField tfStatus;
    @FXML
    private TextField tfAdress;
    @FXML
    private TextField tfClassQL;
    @FXML
    private TextField tfLoaiDT;
    @FXML
    private TextField tfBirthDay;
    @FXML
    private TextField tfChuyenNganh;
    @FXML
    private TextField tfSDT;
    @FXML
    private TextField tfVaoTruong;
    @FXML
    private ImageView imageSinhVien;
    public void showInferStudent(String maSV){
        try {
            DatabaseModel databaseModel = new DatabaseModel();
            Student std = databaseModel.getInformation(maSV);
            tfMaSV.setText(std.getMSSV());
            tfHoTen.setText(std.getHoTen());
            tfGioiTinh.setText(std.getGioiTinh());
            tfStatus.setText(std.getTrangThai());
            tfAdress.setText(std.getQueQuan());
            tfClassQL.setText(std.getLop());
            tfLoaiDT.setText(std.getLoaiDaoTao());
            tfBirthDay.setText(std.getNgaySinh());
            tfChuyenNganh.setText(std.getChuyenNganh());
            tfSDT.setText(std.getSoDienThoai());
            tfVaoTruong.setText(std.getNgayVao());
            String gender = std.getGioiTinh();
            String imageName = gender.equalsIgnoreCase("Nam") ? "nam.jpg" : "nu.jpg";
            String imagePath = getClass().getResource("/com/example/student_management_sys/view/" + imageName).toExternalForm();
            Image image = new Image(imagePath);
            imageSinhVien.setImage(image);
        } catch (SQLException e) {
            System.out.println(" lỗi truy vấn thông tin sinh viên ở StudentController ");
            System.out.println(e.getMessage());
        }
    }

}