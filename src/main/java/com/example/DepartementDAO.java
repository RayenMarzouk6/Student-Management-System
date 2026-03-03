package com.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DepartementDAO {

    private static final List<String> DEFAULT_DEPARTEMENTS = Arrays.asList(
            "Informatique",
            "Mathematiques",
            "Gestion",
            "Commerce",
            "Lettres"
    );

    public static void initDatabase() {
        String sql = "CREATE TABLE IF NOT EXISTS departement (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nom TEXT NOT NULL UNIQUE" +
                ")";
        try (Connection c = DBHelper.getConnection(); Statement s = c.createStatement()) {
            s.execute(sql);
            seedDefaults(c);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Departement> findAll() {
        List<Departement> departements = new ArrayList<>();
        String sql = "SELECT id, nom FROM departement ORDER BY nom";
        try (Connection c = DBHelper.getConnection(); Statement s = c.createStatement(); ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) {
                departements.add(new Departement(rs.getInt("id"), rs.getString("nom")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return departements;
    }

    public static Departement findById(int id) {
        String sql = "SELECT id, nom FROM departement WHERE id = ?";
        try (Connection c = DBHelper.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Departement(rs.getInt("id"), rs.getString("nom"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void seedDefaults(Connection connection) throws SQLException {
        String countSql = "SELECT COUNT(*) FROM departement";
        try (Statement countStmt = connection.createStatement(); ResultSet rs = countStmt.executeQuery(countSql)) {
            if (rs.next() && rs.getInt(1) > 0) {
                return;
            }
        }
        String insertSql = "INSERT INTO departement(nom) VALUES(?)";
        try (PreparedStatement ps = connection.prepareStatement(insertSql)) {
            for (String name : DEFAULT_DEPARTEMENTS) {
                ps.setString(1, name);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }
}
