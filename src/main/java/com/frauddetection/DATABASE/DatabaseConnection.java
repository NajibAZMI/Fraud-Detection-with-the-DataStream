package com.frauddetection.DATABASE;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/FRAUD_DETECTION";
    private static final String USER = "postgres";
    private static final String PASSWORD = "NouveauMotDePasse";

    // Méthode pour obtenir la connexion à la base de données
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}