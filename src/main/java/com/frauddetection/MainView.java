package com.frauddetection;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.cell.PropertyValueFactory;

public class MainView {

    private static ObservableList<Transaction> transactionList = FXCollections.observableArrayList();
    private static ObservableList<Alert> alertsList = FXCollections.observableArrayList();

    private Label totalTransactionsLabel = new Label("Total Transactions: 0");
    private Label totalAlertsLabel = new Label("Total Alerts: 0");
    private Label fraudPercentageLabel = new Label("Fraud Percentage: 0%");
    private Button restartButton = new Button("Redémarrer l'application");
    private TableView<Transaction> transactionTable = new TableView<>();
    private TableView<Alert> alertsTable = new TableView<>();

    private AlertTransactionUpdate alertTransactionUpdate = new AlertTransactionUpdate(); // Instancier la classe ici

    public MainView() {
        createTransactionTable();
        createAlertTable();
    }

    public VBox getView() {
        // Styles pour chaque label avec bordure et couleur de fond
        totalTransactionsLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #0078D7; "
                + "-fx-border-color: #0078D7; -fx-border-width: 2px; -fx-background-color: #D9EAF7; "
                + "-fx-padding: 10px; -fx-border-radius: 5px; -fx-background-radius: 5px;");

        totalAlertsLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #FF5733; "
                + "-fx-border-color: #FF5733; -fx-border-width: 2px; -fx-background-color: #FDE4E4; "
                + "-fx-padding: 10px; -fx-border-radius: 5px; -fx-background-radius: 5px;");

        fraudPercentageLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #28A745; "
                + "-fx-border-color: #28A745; -fx-border-width: 2px; -fx-background-color: #E7F8EC; "
                + "-fx-padding: 10px; -fx-border-radius: 5px; -fx-background-radius: 5px;");

        // Conteneur horizontal pour les labels
        HBox statsBox = new HBox(20, totalTransactionsLabel, totalAlertsLabel, fraudPercentageLabel);
        statsBox.setAlignment(Pos.CENTER); // Centrer les labels horizontalement
        statsBox.setStyle("-fx-padding: 10px;"); // Espacement autour des labels

        // Conteneur horizontal pour les tables
        HBox tablesContainer = new HBox(20, transactionTable, alertsTable);
        tablesContainer.setStyle("-fx-padding: 10px; -fx-background-color: #ffffff; -fx-border-color: #dddddd; " +
                "-fx-border-width: 2px; -fx-border-radius: 5px;");
        tablesContainer.setPrefHeight(400); // Hauteur fixe des tables
        tablesContainer.setPrefWidth(800);  // Largeur totale des tables
        tablesContainer.setAlignment(Pos.CENTER);

        // Retourner un VBox contenant les labels et les tables côte à côte
        return new VBox(20, statsBox, tablesContainer);
    }

    public ObservableList<Transaction> getTransactionList() {
        return transactionList;
    }

    // Méthode pour obtenir la liste des alertes
    public ObservableList<Alert> getAlertsList() {
        return alertsList;
    }

    private void createTransactionTable() {
        TableColumn<Transaction, String> transactionIdCol = new TableColumn<>("Transaction ID");
        transactionIdCol.setCellValueFactory(new PropertyValueFactory<>("transactionId"));
        transactionTable.getColumns().add(transactionIdCol);
        transactionIdCol.setPrefWidth(200);

        TableColumn<Transaction, String> payerIdCol = new TableColumn<>("Payer ID");
        payerIdCol.setCellValueFactory(new PropertyValueFactory<>("payerId"));
        transactionTable.getColumns().add(payerIdCol);
        payerIdCol.setPrefWidth(200);

        TableColumn<Transaction, Double> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        transactionTable.getColumns().add(amountCol);
        amountCol.setPrefWidth(200);

        TableColumn<Transaction, String> beneficiaryIdCol = new TableColumn<>("Beneficiary ID");
        beneficiaryIdCol.setCellValueFactory(new PropertyValueFactory<>("beneficiaryId"));
        transactionTable.getColumns().add(beneficiaryIdCol);
        beneficiaryIdCol.setPrefWidth(200);

        TableColumn<Transaction, String> timestampCol = new TableColumn<>("Timestamp");
        timestampCol.setCellValueFactory(new PropertyValueFactory<>("formattedTimestamp"));
        transactionTable.getColumns().add(timestampCol);
        timestampCol.setPrefWidth(200);

        transactionTable.setItems(transactionList);


    }

    private void createAlertTable() {
        TableColumn<Alert, String> alertTransactionIdCol = new TableColumn<>("Rule");
        alertTransactionIdCol.setCellValueFactory(new PropertyValueFactory<>("alertRuleID"));
        alertsTable.getColumns().add(alertTransactionIdCol);
        alertTransactionIdCol.setPrefWidth(200);

        TableColumn<Alert, String> messageCol = new TableColumn<>("Details");
        messageCol.setCellValueFactory(new PropertyValueFactory<>("alertDetails"));
        alertsTable.getColumns().add(messageCol);
        messageCol.setPrefWidth(600);

        TableColumn<Alert, String> timestampCol = new TableColumn<>("Timestamp");
        timestampCol.setCellValueFactory(new PropertyValueFactory<>("formattedTimestamp"));
        alertsTable.getColumns().add(timestampCol);
        timestampCol.setPrefWidth(200);

        alertsTable.setItems(alertsList);


    }

    // Méthode pour styliser les tables


    public void addTransaction(Transaction transaction) {
        transactionList.add(transaction);
        updateTransactionCount();


        // Appeler la méthode pour enregistrer les statistiques des transactions
        alertTransactionUpdate.addTransaction();
        updateFraudPercentage();
    }

    public void addAlert(Alert alert) {
        alertsList.add(alert);
        updateAlertCount();


        // Appeler la méthode pour enregistrer les statistiques des alertes
        alertTransactionUpdate.addAlert(alert);
        updateFraudPercentage();
    }

    private void updateTransactionCount() {
        totalTransactionsLabel.setText("Total Transactions: " + transactionList.size()); // MAJ du compteur des transactions
    }

    private void updateAlertCount() {
        totalAlertsLabel.setText("Total Alerts: " + alertsList.size()); // MAJ du compteur des alertes
    }

    private void updateFraudPercentage() {
        if (!transactionList.isEmpty()) {
            double fraudPercentage = ((double) alertsList.size() / transactionList.size()) * 100;
            fraudPercentageLabel.setText(String.format("Fraud Percentage: %.2f%%", fraudPercentage));
        } else {
            fraudPercentageLabel.setText("Fraud Percentage: 0%");
        }
    }
}