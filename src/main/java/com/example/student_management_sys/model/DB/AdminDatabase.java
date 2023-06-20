package com.example.student_management_sys.model.DB;

import com.example.student_management_sys.model.CourseData;
import com.example.student_management_sys.model.GiaoVien;
import com.example.student_management_sys.model.Student;
import com.example.student_management_sys.model.StudentNew;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AdminDatabase extends DatabaseModel {


    private Connection connection;

    public AdminDatabase() {
        connection = ConnectionDatabase.getConnection();
    }

    public ObservableList<Student> timKiem(String queries) {
        List<String> list = new ArrayList<>();
        ObservableList<Student> listStudent = FXCollections.observableArrayList();
        String query = "\n" +
                "SELECT *\n" +
                "FROM (\n" +
                "    SELECT CONCAT_WS('/', SV.Ma_SV, caNhan.Name_CN, Gender, TrangThai, caNhan.Que_Quan, Name_Lop,ldt.Name_Loai, caNhan.Ngay_Sinh, caNhan.Sdt_CN, hdt.Name_He, chuyenNganh.Name_ChuyenNganh, nganhHoc.Name_Nganh,NgayVao) AS concatenated_values\n" +
                "    FROM sinhVien SV\n" +
                "    INNER JOIN caNhan ON SV.CCCD = caNhan.CCCD\n" +
                "    INNER JOIN chuyenNganh ON chuyenNganh.Ma_ChuyenNganh = SV.Ma_ChuyenNganh\n" +
                "    INNER JOIN loaiDaoTao ldt ON ldt.Ma_Loai = SV.Ma_Loai\n" +
                "    INNER JOIN heDaoTao hdt ON hdt.Ma_He = ldt.Ma_He\n" +
                "    INNER JOIN nganhHoc ON nganhHoc.Ma_Nganh = chuyenNganh.Ma_Nganh\n" +
                ") AS subquery\n" +
                "WHERE concatenated_values LIKE N'%" + queries + "%'";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String thongTin = resultSet.getString("concatenated_values");
                list.add(thongTin);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        for (String s : list) {

            String[] arr = s.split("/");
            Student student = new Student(arr[0], arr[1], arr[2], arr[3], arr[4], arr[5], arr[6], arr[7], arr[8], arr[9], arr[10], arr[11], arr[12]);
            listStudent.add(student);
        }
        return listStudent;
    }

    public ObservableList<CourseData> timKiemMonHoc(String queries) {
        String query = "\n" +
                "select * from(\n" +
                "SELECT CONCAT_WS(';', \n" +
                "    Ma_MH, Name_Mh, So_Tin, Loai_HP\n" +
                ") AS mh\n" +
                "FROM monHoc\n" +
                "\n" +
                "WHERE Ma_MH NOT IN (\n" +
                "    SELECT Ma_MH FROM Trash_MH\n" +
                ")\n" +
                ") as monhoc\n" +
                "WHERE mh LIKE N'%" + queries + "%'";
        List<String> list = new ArrayList<>();
        ObservableList<CourseData> listCourse = FXCollections.observableArrayList();
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String thongTin = resultSet.getString("mh");
                list.add(thongTin);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        for (String s : list) {

            String[] arr = s.split(";");
            CourseData courseData = new CourseData(arr[0], arr[1], arr[2], arr[3]);
            listCourse.add(courseData);
        }
        return listCourse;
    }


    public void deleteMonHoc(String maMH) {
        String query = "INSERT INTO Trash_MH values (?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, maMH);
            statement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Không thể xóa môn học này");
        }
    }

    public void updateMonHoc(CourseData CD) {
        String query = "UPDATE monHoc SET Name_MH = ?, So_Tin = ?, Loai_HP = ? WHERE Ma_MH = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, CD.getNameMH());
            String soTin = String.valueOf(CD.getSoTin());
            statement.setString(2, soTin);
            statement.setString(3, CD.getLoaiHP());
            statement.setString(4, CD.getMaMH());
            statement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Không thể cập nhật môn học này");
        }
    }
    public void insertSinhVien(StudentNew studentNew) {
        String canhanSql = "INSERT INTO caNhan (CCCD, Name_CN, Email, Ngay_Sinh, Gender, Sdt_CN, Que_Quan) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        String sinhvienSql = "INSERT INTO sinhVien (Ma_SV, CCCD, Pass_SV, Name_Lop, TrangThai, NgayVao) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        String lopSql = "INSERT INTO lopHoc (Name_Lop) VALUES (?)";

        try (Connection databaseConnection = ConnectionDatabase.getConnection();
             PreparedStatement canhanStatement = databaseConnection.prepareStatement(canhanSql);
             PreparedStatement sinhvienStatement = databaseConnection.prepareStatement(sinhvienSql);
             PreparedStatement lopStatement = databaseConnection.prepareStatement(lopSql)) {

            // Kiểm tra sự tồn tại của CCCD trong bảng caNhan
            String checkExistenceSql = "SELECT CCCD FROM caNhan WHERE CCCD = ?";
            try (PreparedStatement checkExistenceStatement = databaseConnection.prepareStatement(checkExistenceSql)) {
                checkExistenceStatement.setString(1, studentNew.getCCCD());
                ResultSet resultSet = checkExistenceStatement.executeQuery();

                if (resultSet.next()) {
                    //check CCCD đã tồn tại hay chưa
                    System.out.println("CCCD already exists in caNhan table");
                } else {
                    canhanStatement.setString(1, studentNew.getCCCD());
                    canhanStatement.setString(2, studentNew.getHoTen());
                    canhanStatement.setString(3, studentNew.getEmail());
                    canhanStatement.setString(4, studentNew.getNgaySinh());
                    canhanStatement.setString(5, studentNew.getGioiTinh());
                    canhanStatement.setString(6, studentNew.getSoDienThoai());
                    canhanStatement.setString(7, studentNew.getQueQuan());

                    canhanStatement.executeUpdate();

                    lopStatement.setString(1, studentNew.getLop().toUpperCase());

                    lopStatement.executeUpdate();


                    sinhvienStatement.setString(1, studentNew.getMSSV());
                    sinhvienStatement.setString(2, studentNew.getCCCD());
                    sinhvienStatement.setString(3, studentNew.getPass());
                    sinhvienStatement.setString(4, studentNew.getLop());
                    sinhvienStatement.setString(5, studentNew.getTrangThai());
                    sinhvienStatement.setString(6, studentNew.getNgayVao());

                    sinhvienStatement.executeUpdate();

                    System.out.println("Insert successful!");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void updateSinhVien(String maSV, Student student) {
        String updateSinhVienQuery = "UPDATE sinhVien " +
                "SET Name_Lop = ?, TrangThai = ?, NgayVao = ? " +
                "WHERE Ma_SV = ?;";
        String updateCaNhanQuery = "UPDATE caNhan " +
                "SET Name_CN = ?, Ngay_Sinh = ?, Gender = ?, Sdt_CN = ?, Que_Quan = ? " +
                "WHERE CCCD IN (SELECT CCCD FROM sinhVien WHERE Ma_SV = ?)";
        String updateHeDaoTaoQuery = "UPDATE heDaoTao " +
                "SET Name_He = ? " +
                "WHERE Ma_He IN (" +
                "    SELECT Ma_Loai " +
                "    FROM sinhVien " +
                "    WHERE Ma_SV = ?" +
                ")";
        String updateLoaiDaoTaoQuery = "UPDATE loaiDaoTao " +
                "SET Name_Loai = ? " +
                "WHERE Ma_Loai IN (" +
                "    SELECT Ma_Loai " +
                "    FROM sinhVien " +
                "    WHERE Ma_SV = ?" +
                ")";
        String updateNganhHocQuery = "UPDATE nganhHoc " +
                "SET Name_Nganh = ? " +
                "WHERE Ma_Nganh IN (" +
                "    SELECT Ma_Nganh " +
                "    FROM chuyenNganh " +
                "    WHERE Ma_ChuyenNganh IN (" +
                "        SELECT Ma_ChuyenNganh " +
                "        FROM sinhVien " +
                "        WHERE Ma_SV = ?" +
                "    )" +
                ")";
        String updateChuyenNganhQuery = "UPDATE chuyenNganh " +
                "SET Name_ChuyenNganh = ? " +
                "WHERE Ma_ChuyenNganh IN (" +
                "    SELECT Ma_ChuyenNganh " +
                "    FROM sinhVien " +
                "    WHERE Ma_SV = ?" +
                ")";

        try (Connection databaseConnection = ConnectionDatabase.getConnection();
             PreparedStatement updateSinhVienStatement = databaseConnection.prepareStatement(updateSinhVienQuery);
             PreparedStatement updateCaNhanStatement = databaseConnection.prepareStatement(updateCaNhanQuery);
             PreparedStatement updateHeDaoTaoStatement = databaseConnection.prepareStatement(updateHeDaoTaoQuery);
             PreparedStatement updateLoaiDaoTaoStatement = databaseConnection.prepareStatement(updateLoaiDaoTaoQuery);
             PreparedStatement updateNganhHocStatement = databaseConnection.prepareStatement(updateNganhHocQuery);
             PreparedStatement updateChuyenNganhStatement = databaseConnection.prepareStatement(updateChuyenNganhQuery)) {

            databaseConnection.setAutoCommit(false);

            updateSinhVienStatement.setString(1, student.getLop());
            updateSinhVienStatement.setString(2, student.getTrangThai());
            updateSinhVienStatement.setString(3, student.getNgayVao());
            updateSinhVienStatement.setString(4, maSV);

            int rowsAffected = updateSinhVienStatement.executeUpdate();

            if (rowsAffected > 0) {
                updateCaNhanStatement.setString(1, student.getHoTen());
                updateCaNhanStatement.setString(2, student.getNgaySinh());
                updateCaNhanStatement.setString(3, student.getGioiTinh());
                updateCaNhanStatement.setString(4, student.getSoDienThoai());
                updateCaNhanStatement.setString(5, student.getQueQuan());
                updateCaNhanStatement.setString(6, maSV);

                updateCaNhanStatement.executeUpdate();

                updateHeDaoTaoStatement.setString(1, student.getHeDaoTao());
                updateHeDaoTaoStatement.setString(2, maSV);

                updateHeDaoTaoStatement.executeUpdate();

                updateLoaiDaoTaoStatement.setString(1, student.getLoaiDaoTao());
                updateLoaiDaoTaoStatement.setString(2, maSV);

                updateLoaiDaoTaoStatement.executeUpdate();

                updateNganhHocStatement.setString(1, student.getNganhHoc());
                updateNganhHocStatement.setString(2, maSV);

                updateNganhHocStatement.executeUpdate();

                updateChuyenNganhStatement.setString(1, student.getChuyenNganh());
                updateChuyenNganhStatement.setString(2, maSV);

                updateChuyenNganhStatement.executeUpdate();

                databaseConnection.commit();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Thông báo");
                alert.setHeaderText(null);
                alert.setContentText("Cập nhật thành công");
                alert.showAndWait();
            } else {
                databaseConnection.rollback();

                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText(null);
                alert.setContentText("Không tìm thấy sinh viên để cập nhật");
                alert.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText(null);
            alert.setContentText("Đã xảy ra lỗi khi cập nhật dữ liệu");
            alert.showAndWait();
        }
    }


    public static void main(String[] args) {
        AdminDatabase adminDatabase = new AdminDatabase();
        ObservableList<CourseData> list = adminDatabase.timKiemMonHoc("Vật Lý");

    }

    public String getMaGV(String maMH) {
//        select Ma_GV from giangDay where Ma_MH = '10020109'
        String query = "select Ma_GV from giangDay where Ma_MH = ?";
        String maGV = "";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, maMH);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                maGV = resultSet.getString("Ma_GV");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return maGV;
    }

    public void PhanCongGV(String maMH, String maGV) {
        String query = "MERGE giangDay AS target\n" +
                "USING (VALUES ('" + maMH + "', '" + maGV + "')) AS source (Ma_MH, Ma_GV)\n" +
                "ON (target.Ma_MH = source.Ma_MH AND target.Ma_GV = source.Ma_GV)\n" +
                "WHEN MATCHED THEN\n" +
                "    UPDATE SET target.Ma_MH = source.Ma_MH, target.Ma_GV = source.Ma_GV\n" +
                "WHEN NOT MATCHED THEN\n" +
                "    INSERT (Ma_MH, Ma_GV) VALUES (source.Ma_MH, source.Ma_GV);";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    //
//select * from (
//SELECT CONCAT_WS(';',
//    Ma_GV, TrinhDo, Name_CN, Sdt_CN
//) AS concatenated_values
//FROM giaoVien
//INNER JOIN caNhan cn ON cn.CCCD = giaoVien.CCCD
//) as sub
//where concatenated_values like N'%Mai%'
    public ObservableList<GiaoVien> timGV(String queries) {
        String query = "select * from (\n" +
                "SELECT CONCAT_WS(';', \n" +
                "    Ma_GV, TrinhDo, Name_CN, Sdt_CN\n" +
                ") AS concatenated_values\n" +
                "FROM giaoVien\n" +
                "INNER JOIN caNhan cn ON cn.CCCD = giaoVien.CCCD\n" +
                ") as sub\n" +
                "where concatenated_values like N'%" + queries + "%'";
        List<String> list = new ArrayList<>();
        ObservableList<GiaoVien> listGV = FXCollections.observableArrayList();
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String thongTin = resultSet.getString("concatenated_values");
                list.add(thongTin);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        for (String s : list) {

            String[] arr = s.split(";");
            GiaoVien giaoVien = new GiaoVien(arr[0], arr[1], arr[2], arr[3]);
            listGV.add(giaoVien);
        }
        return listGV;
    }
}
