package com.frauddetection;

import com.frauddetection.DATABASE.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;

public class AlertTransactionUpdate {

    // Méthode pour enregistrer le nombre de transactions par jour
    public void recordDailyTransactionStatistics(int transactionCount) {
        LocalDate today = LocalDate.now();
        String sql = "SELECT * FROM alert_par_jour WHERE date = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement checkStatement = connection.prepareStatement(sql)) {

            checkStatement.setDate(1, Date.valueOf(today));
            ResultSet resultSet = checkStatement.executeQuery();

            if (resultSet.next()) {
                // Met à jour uniquement le nombre de transactions
                int existingTransactions = resultSet.getInt("nbr_transactions");

                String updateSql = "UPDATE alert_par_jour SET nbr_transactions = ? WHERE date = ?";
                try (PreparedStatement updateStatement = connection.prepareStatement(updateSql)) {
                    updateStatement.setInt(1, existingTransactions + transactionCount);
                    updateStatement.setDate(2, Date.valueOf(today));
                    updateStatement.executeUpdate();
                }
            } else {
                // Insère une nouvelle ligne pour le jour courant
                String insertSql = "INSERT INTO alert_par_jour (date, nbr_transactions, nbr_alertes, description) VALUES (?, ?, ?, ?)";
                try (PreparedStatement insertStatement = connection.prepareStatement(insertSql)) {
                    insertStatement.setDate(1, Date.valueOf(today));
                    insertStatement.setInt(2, transactionCount);
                    insertStatement.setInt(3, 0); // Initialiser les alertes à 0
                    insertStatement.setString(4, null); // Pas de description au départ
                    insertStatement.executeUpdate();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour enregistrer le nombre d'alertes par jour (avec la dernière alerte uniquement)
    public void recordDailyAlertStatistics(int alertCount, String ruleDetails) {
        LocalDate today = LocalDate.now();
        String sql = "SELECT * FROM alert_par_jour WHERE date = ?";
        String rlsd=ruleDetails.substring(0,75);
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement checkStatement = connection.prepareStatement(sql)) {

            checkStatement.setDate(1, Date.valueOf(today));
            ResultSet resultSet = checkStatement.executeQuery();

            if (resultSet.next()) {
                // Met à jour le nombre d'alertes et remplace la description par la dernière alerte
                int existingAlerts = resultSet.getInt("nbr_alertes");
                String updateSql = "UPDATE alert_par_jour SET nbr_alertes = ?, description = ? WHERE date = ?";
                try (PreparedStatement updateStatement = connection.prepareStatement(updateSql)) {
                    updateStatement.setInt(1, existingAlerts + alertCount);
                    updateStatement.setString(2, rlsd); // Remplacer par la dernière description
                    updateStatement.setDate(3, Date.valueOf(today));
                    updateStatement.executeUpdate();
                }
            } else {
                // Insère une nouvelle ligne avec l'alerte courante
                String insertSql = "INSERT INTO alert_par_jour (date, nbr_transactions, nbr_alertes, description) VALUES (?, ?, ?, ?)";
                try (PreparedStatement insertStatement = connection.prepareStatement(insertSql)) {
                    insertStatement.setDate(1, Date.valueOf(today));
                    insertStatement.setInt(2, 0); // Pas de transactions au départ
                    insertStatement.setInt(3, alertCount);
                    insertStatement.setString(4, rlsd); // Ajouter la nouvelle alerte
                    insertStatement.executeUpdate();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour ajouter une transaction
    public void addTransaction() {
        int transactionCount = 1; // Exemple de transaction unique
        recordDailyTransactionStatistics(transactionCount);
    }

    // Méthode pour ajouter une alerte
    public void addAlert(Alert alert) {
        int alertCount = 1; // Une alerte à ajouter
        String ruleDetails = "Rule: " + alert.getAlertRuleID() + ", Details: " + alert.getAlertDetails();
        // Enregistrer l'alerte avec uniquement la dernière description
        recordDailyAlertStatistics(alertCount, ruleDetails);
    }
}
