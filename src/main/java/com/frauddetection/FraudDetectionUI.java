package com.frauddetection;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FraudDetectionUI extends Application {

    private static FraudDetectionUI instance;
    private MainView mainView;
    private GestionRule gestionRule;
    private Label dateLabel; // Label pour afficher la date actuelle

    public FraudDetectionUI() {
        instance = this;
        this.mainView = new MainView(); // Initialiser MainView
        this.gestionRule = new GestionRule();
    }

    public static FraudDetectionUI getInstance() {
        return instance;
    }

    public MainView getMainView() {
        return mainView; // Retourner l'instance de MainView
    }

    @Override
    public void start(Stage stage) {
        // Initialiser le label pour afficher la date actuelle
        dateLabel = new Label();
        dateLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #FFFFFF;");

        updateDate(); // Mettre à jour la date une première fois

        // Configurer un Timeline pour mettre à jour la date chaque seconde
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(1), e -> updateDate())
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        // Créer une barre horizontale
        HBox topBar = new HBox(dateLabel);
        topBar.setPrefHeight(80); // Fixer la hauteur de la barre
        topBar.setStyle("-fx-background-color: #007BFF; -fx-alignment: center-left; -fx-padding: 10px;");

        // Créer les vues
        StatisticsView statisticsView = new StatisticsView(mainView);
        RuleManagementView ruleManagementView = new RuleManagementView(gestionRule);

        // Créer le TabPane
        TabPane tabPane = new TabPane();
        Tab mainTab = new Tab("Transactions & Alerts", mainView.getView());
        mainTab.setClosable(false);
        mainTab.setStyle("-fx-background-color: #007BFF; -fx-text-fill: white;");

        Tab statisticsTab = new Tab("Statistics", statisticsView.getView());
        statisticsTab.setClosable(false);
        statisticsTab.setStyle("-fx-background-color: #28A745; -fx-text-fill: white;");

        Tab ruleManagementTab = new Tab("Rule Management", ruleManagementView.getView());
        ruleManagementTab.setClosable(false);
        ruleManagementTab.setStyle("-fx-background-color: #FFC107; -fx-text-fill: white; ");

        tabPane.getTabs().addAll(mainTab, statisticsTab, ruleManagementTab);

        // Appliquer un style minimaliste au TabPane
        tabPane.setStyle(
                "-fx-tab-min-width: 390px; " +
                        "-fx-tab-max-width: 1000px; " +
                        "-fx-background-color: #F9F9F9; " +
                        "-fx-border-color: #CCCCCC;"
        );

        // Ajouter la barre horizontale et le TabPane à un VBox
        VBox root = new VBox(topBar, tabPane);
        root.setStyle("-fx-background-color: #FFFFFF; -fx-padding: 0;");

        // Configurer la scène
        Scene scene = new Scene(root, 1000, 1000);
        stage.setScene(scene);
        stage.setTitle("Fraud Detection System");
        stage.show();

        // Lancer la mise à jour des statistiques
        statisticsView.startStatisticsUpdater();
    }

    /**
     * Mettre à jour le label avec la date et l'heure actuelles.
     */
    private void updateDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        dateLabel.setText("Current Date: " + LocalDateTime.now().format(formatter));
    }

    public static void main(String[] args) {
        launch(args); // Lancer l'interface JavaFX
    }
}
