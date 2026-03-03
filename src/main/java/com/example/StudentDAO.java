package com.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    public static void initDatabase() {
        String sql = "CREATE TABLE IF NOT EXISTS student (" +
                "matricule TEXT PRIMARY KEY," +
                "nom TEXT NOT NULL," +
                "adresse TEXT NOT NULL," +
                "age INTEGER NOT NULL," +
                "departement_id INTEGER NOT NULL," +
                "FOREIGN KEY(departement_id) REFERENCES departement(id) ON DELETE RESTRICT ON UPDATE CASCADE" +
                ")";
        try (Connection c = DBHelper.getConnection(); Statement s = c.createStatement()) {
            s.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean insert(Student student) {
        String sql = "INSERT INTO student(matricule, nom, adresse, age, departement_id) VALUES(?,?,?,?,?)";
        try (Connection c = DBHelper.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, student.getMatricule());
            ps.setString(2, student.getNom());
            ps.setString(3, student.getAdresse());
            ps.setInt(4, student.getAge());
            ps.setInt(5, student.getDepartement().getId());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean update(Student student) {
        String sql = "UPDATE student SET nom = ?, adresse = ?, age = ?, departement_id = ? WHERE matricule = ?";
        try (Connection c = DBHelper.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, student.getNom());
            ps.setString(2, student.getAdresse());
            ps.setInt(3, student.getAge());
            ps.setInt(4, student.getDepartement().getId());
            ps.setString(5, student.getMatricule());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean delete(String matricule) {
        String sql = "DELETE FROM student WHERE matricule = ?";
        try (Connection c = DBHelper.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, matricule);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static List<Student> findAll() {
        return query("SELECT matricule, nom, adresse, age, departement_id FROM student ORDER BY nom", null);
    }

    public static List<Student> findPage(int limit, int offset) {
        return query("SELECT matricule, nom, adresse, age, departement_id FROM student ORDER BY nom LIMIT ? OFFSET ?", ps -> {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
        });
    }

    public static List<Student> search(String keyword) {
        String sql = "SELECT matricule, nom, adresse, age, departement_id FROM student " +
                "WHERE LOWER(matricule) LIKE ? OR LOWER(nom) LIKE ? OR LOWER(adresse) LIKE ? " +
                "ORDER BY nom";
        String pattern = "%" + keyword.toLowerCase() + "%";
        return query(sql, ps -> {
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);
        });
    }

    public static int countAll() {
        String sql = "SELECT COUNT(*) FROM student";
        try (Connection c = DBHelper.getConnection(); Statement s = c.createStatement(); ResultSet rs = s.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static List<Student> query(String sql, StatementConfigurer configurer) {
        List<Student> students = new ArrayList<>();
        try (Connection c = DBHelper.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            if (configurer != null) {
                configurer.configure(ps);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    students.add(mapStudent(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }

    private static Student mapStudent(ResultSet rs) throws SQLException {
        String matricule = rs.getString("matricule");
        String nom = rs.getString("nom");
        String adresse = rs.getString("adresse");
        int age = rs.getInt("age");
        int departementId = rs.getInt("departement_id");
        Departement departement = DepartementDAO.findById(departementId);
        return new Student(matricule, nom, adresse, age, departement);
    }

    @FunctionalInterface
    private interface StatementConfigurer {
        void configure(PreparedStatement ps) throws SQLException;
    }
}
