package com.frauddetection;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
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
        dateLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #333333; -fx-padding: 10px;");
        updateDate(); // Mettre à jour la date une première fois

        // Configurer un Timeline pour mettre à jour la date chaque seconde
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(1), e -> updateDate())
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        // Créer les vues
        StatisticsView statisticsView = new StatisticsView(mainView);
        RuleManagementView ruleManagementView = new RuleManagementView(gestionRule);

        // Créer le TabPane
        TabPane tabPane = new TabPane();

        Tab mainTab = new Tab("Transactions & Alerts", mainView.getView());
        mainTab.setClosable(false);

        Tab statisticsTab = new Tab("Statistics", statisticsView.getView());
        statisticsTab.setClosable(false);

        Tab ruleManagementTab = new Tab("Rule Management", ruleManagementView.getView());
        ruleManagementTab.setClosable(false);

        tabPane.getTabs().addAll(mainTab, statisticsTab, ruleManagementTab);

        // Appliquer les styles pour TabPane
        tabPane.setStyle(
                "-fx-padding: 10px;" +
                        "-fx-border-color: #dddddd;" +
                        "-fx-border-width: 2px;" +
                        "-fx-border-radius: 5px;" +
                        "-fx-tab-min-width: 150px;" +
                        "-fx-tab-max-width: 200px;"
        );

        // Ajouter le label de date et le TabPane à un VBox
        VBox root = new VBox(dateLabel, tabPane);
        root.setStyle("-fx-background-color: white;"); // Appliquer un fond blanc

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
