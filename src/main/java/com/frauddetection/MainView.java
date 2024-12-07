package com.frauddetection;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

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
        // Charger l'image
        Image telechargeIcon = new Image(getClass().getResourceAsStream("/images/telechargements.png"));
        if (telechargeIcon.isError()) {
            System.out.println("Error loading image");
        }

        Image transactionNumberIcon = new Image(getClass().getResourceAsStream("/images/transaction.png"));
        if (telechargeIcon.isError()) {
            System.out.println("Error loading image");
        }
        Image AlertNumberIcon = new Image(getClass().getResourceAsStream("/images/alert.png"));
        if (telechargeIcon.isError()) {
            System.out.println("Error loading image");
        }
        Image PourcentageIcon = new Image(getClass().getResourceAsStream("/images/fleches.png"));
        if (telechargeIcon.isError()) {
            System.out.println("Error loading image");
        }

        // Créer les instances d'ImageView séparées pour chaque bouton
        ImageView telechargeIconForTransaction = new ImageView(telechargeIcon);
        telechargeIconForTransaction.setFitHeight(20);
        telechargeIconForTransaction.setFitWidth(20);

        ImageView telechargeIconForAlert = new ImageView(telechargeIcon);
        telechargeIconForAlert.setFitHeight(20);
        telechargeIconForAlert.setFitWidth(20);

        ImageView IconForTransaction = new ImageView(transactionNumberIcon);
        IconForTransaction.setFitHeight(35);
        IconForTransaction.setFitWidth(35);

        ImageView IconForAlert = new ImageView(AlertNumberIcon);
        IconForAlert.setFitHeight(35);
        IconForAlert.setFitWidth(35);

        ImageView IconForPourcentage = new ImageView(PourcentageIcon);
        IconForPourcentage.setFitHeight(35);
        IconForPourcentage.setFitWidth(35);
        totalTransactionsLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #0078D7; "
                + "-fx-border-color: #0078D7; -fx-border-width: 2px; -fx-background-color: #FFFFFF; "
                + "-fx-padding: 10px; -fx-border-radius: 5px; -fx-background-radius: 5px;");
        totalTransactionsLabel.setGraphic(IconForTransaction);

        totalAlertsLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #FF5733; "
                + "-fx-border-color: #FF5733; -fx-border-width: 2px; -fx-background-color: #FFFFFF; "
                + "-fx-padding: 10px; -fx-border-radius: 5px; -fx-background-radius: 5px;");
        totalAlertsLabel.setGraphic(IconForAlert);

        fraudPercentageLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #28A745; "
                + "-fx-border-color: #28A745; -fx-border-width: 2px; -fx-background-color: #FFFFFF; "
                + "-fx-padding: 10px; -fx-border-radius: 5px; -fx-background-radius: 5px;");
        fraudPercentageLabel.setGraphic(IconForPourcentage);
        // Conteneur horizontal pour les labels
        HBox statsBox = new HBox(20, totalTransactionsLabel, totalAlertsLabel, fraudPercentageLabel);
        statsBox.setAlignment(Pos.CENTER); // Centrer les labels horizontalement
        statsBox.setStyle("-fx-padding: 10px;"); // Espacement autour des labels

        // Conteneur pour la table des transactions avec le bouton en bas
        Button transactionButton = new Button("Exporter Transactions");
        // Ajouter l'icône au bouton
        transactionButton.setGraphic(telechargeIconForTransaction);
        transactionButton.setOnAction(e -> {
            // Action spécifique pour les transactions
            System.out.println("Bouton Transactions cliqué");
            exportToCSV(transactionList, "Transactions");
        });

        // Hover effect for transaction button
        // Hover effect for transaction button




        VBox transactionContainer = new VBox(10, transactionTable, transactionButton);
        transactionContainer.setAlignment(Pos.CENTER);
        transactionContainer.setStyle("-fx-padding: 10px;");

        // Conteneur pour la table des alertes avec le bouton en bas
        Button alertsButton = new Button("Exporter Alertes");
        // Ajouter l'icône au bouton
        alertsButton.setGraphic(telechargeIconForAlert);
        alertsButton.setOnAction(e -> {
            // Action spécifique pour les alertes
            System.out.println("Bouton Alertes cliqué");
            exportToCSV(alertsList, "Alertes");
        });

        // Transaction Button
        transactionButton.setStyle("-fx-font-size: 16px; -fx-text-fill: #28A745; -fx-background-color: #FFFFFF; " +
                "-fx-border-color: #28A745; -fx-border-width: 2px; -fx-padding: 10px 20px; " +
                "-fx-border-radius: 5px; -fx-background-radius: 5px;");

        transactionButton.setOnMouseEntered(event -> {
            transactionButton.setStyle("-fx-font-size: 16px; -fx-text-fill: #FFFFFF; -fx-background-color: #28A745; " +
                    "-fx-border-color: #FFFFFF; -fx-border-width: 2px; -fx-padding: 10px 20px; " +
                    "-fx-border-radius: 5px; -fx-background-radius: 5px;");
        });

        transactionButton.setOnMouseExited(event -> {
            System.out.println("Mouse exited transaction button");
            transactionButton.setStyle("-fx-font-size: 16px; -fx-text-fill: #28A745; -fx-background-color: #FFFFFF; " +
                    "-fx-border-color: #28A745; -fx-border-width: 2px; -fx-padding: 10px 20px; " +
                    "-fx-border-radius: 5px; -fx-background-radius: 5px;");
        });
        alertsButton.setStyle("-fx-font-size: 16px; -fx-text-fill: #28A745; -fx-background-color: #FFFFFF; " +
                "-fx-border-color: #28A745; -fx-border-width: 2px; -fx-padding: 10px 20px; " +
                "-fx-border-radius: 5px; -fx-background-radius: 5px;");

        alertsButton.setOnMouseEntered(event -> {
            alertsButton.setStyle("-fx-font-size: 16px; -fx-text-fill: #FFFFFF; -fx-background-color: #28A745; " +
                    "-fx-border-color: #FFFFFF; -fx-border-width: 2px; -fx-padding: 10px 20px; " +
                    "-fx-border-radius: 5px; -fx-background-radius: 5px;");
        });

        alertsButton.setOnMouseExited(event -> {
            System.out.println("Mouse exited alert button");
            alertsButton.setStyle("-fx-font-size: 16px; -fx-text-fill: #28A745; -fx-background-color: #FFFFFF; " +
                    "-fx-border-color: #28A745; -fx-border-width: 2px; -fx-padding: 10px 20px; " +
                    "-fx-border-radius: 5px; -fx-background-radius: 5px;");
        });



        VBox alertsContainer = new VBox(10, alertsTable, alertsButton);
        alertsContainer.setAlignment(Pos.CENTER);
        alertsContainer.setStyle("-fx-padding: 10px;");

        // Conteneur horizontal pour les deux sections
        HBox tablesContainer = new HBox(20, transactionContainer, alertsContainer);
        tablesContainer.setStyle("-fx-padding: 10px; -fx-background-color: #ffffff; -fx-border-color: #dddddd; " +
                "-fx-border-width: 2px; -fx-border-radius: 5px;");
        tablesContainer.setPrefHeight(900); // Hauteur des tables
        tablesContainer.setPrefWidth(800);  // Largeur totale
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
        transactionIdCol.setPrefWidth(70);

        TableColumn<Transaction, String> payerIdCol = new TableColumn<>("Payer ID");
        payerIdCol.setCellValueFactory(new PropertyValueFactory<>("payerId"));
        transactionTable.getColumns().add(payerIdCol);
        payerIdCol.setPrefWidth(100);

        TableColumn<Transaction, Double> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        transactionTable.getColumns().add(amountCol);
        amountCol.setPrefWidth(100);

        TableColumn<Transaction, String> beneficiaryIdCol = new TableColumn<>("Beneficiary ID");
        beneficiaryIdCol.setCellValueFactory(new PropertyValueFactory<>("beneficiaryId"));
        transactionTable.getColumns().add(beneficiaryIdCol);
        beneficiaryIdCol.setPrefWidth(100);

        TableColumn<Transaction, String> timestampCol = new TableColumn<>("Timestamp");
        timestampCol.setCellValueFactory(new PropertyValueFactory<>("formattedTimestamp"));
        transactionTable.getColumns().add(timestampCol);
        timestampCol.setPrefWidth(150);

        transactionTable.setItems(transactionList);
    }

    private void createAlertTable() {
        TableColumn<Alert, String> alertTransactionIdCol = new TableColumn<>("Rule");
        alertTransactionIdCol.setCellValueFactory(new PropertyValueFactory<>("alertRuleID"));
        alertsTable.getColumns().add(alertTransactionIdCol);
        alertTransactionIdCol.setPrefWidth(50);

        TableColumn<Alert, String> messageCol = new TableColumn<>("Details");
        messageCol.setCellValueFactory(new PropertyValueFactory<>("alertDetails"));
        alertsTable.getColumns().add(messageCol);
        messageCol.setPrefWidth(400);

        TableColumn<Alert, String> timestampCol = new TableColumn<>("Timestamp");
        timestampCol.setCellValueFactory(new PropertyValueFactory<>("formattedTimestamp"));
        alertsTable.getColumns().add(timestampCol);
        timestampCol.setPrefWidth(150);

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

    // Méthode pour exporter les données en CSV
    private void exportToCSV(ObservableList<?> dataList, String type) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showSaveDialog(new Stage());

        if (file != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                // Ecrire l'entête
                if (type.equals("Transactions")) {
                    writer.write("Transaction ID,Payer ID,Amount,Beneficiary ID,Timestamp\n");
                    for (Transaction transaction : (ObservableList<Transaction>) dataList) {
                        writer.write(transaction.getTransactionId() + ","
                                + transaction.getPayerId() + ","
                                + transaction.getAmount() + ","
                                + transaction.getBeneficiaryId() + ","
                                + transaction.getFormattedTimestamp() + "\n");
                    }
                } else if (type.equals("Alertes")) {
                    writer.write("Rule,Details,Timestamp\n");
                    for (Alert alert : (ObservableList<Alert>) dataList) {
                        writer.write(alert.getAlertRuleID() + ","
                                + alert.getAlertDetails() + ","
                                + alert.getFormattedTimestamp() + "\n");
                    }
                }
                System.out.println(type + " exported to CSV successfully.");
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error while exporting to CSV.");
            }
        }
    }
}

