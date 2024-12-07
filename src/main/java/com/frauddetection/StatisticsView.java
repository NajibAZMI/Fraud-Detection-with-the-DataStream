package com.frauddetection;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StatisticsView {

    private XYChart.Series<Number, Number> transactionsSeries = new XYChart.Series<>();
    private XYChart.Series<Number, Number> alertsSeries = new XYChart.Series<>();
    private XYChart.Series<Number, Number> fraudPercentageSeries = new XYChart.Series<>();
    private int currentTime = 0;

    private static final int INTERVAL = 5; // Intervalle de 5 minutes
    private MainView mainView;
    private TableView<RuleAlertStats> ruleAlertTable = new TableView<>();
    private PieChart ruleAlertPieChart = new PieChart();

    public StatisticsView(MainView mainView) {
        this.mainView = mainView;
        createRuleAlertTable(); // Initialise la TableView
        startPieChartUpdater();
    }

    public VBox getView() {
        // Créer les trois onglets
        TabPane tabPane = new TabPane();

        Tab transactionsTab = new Tab("Transactions & Alerts", createTransactionsAlertsChart());
        Tab fraudPercentageTab = new Tab("Fraud Percentage", createFraudPercentageChart());
        Tab ruleAlertTab = new Tab("Rule Alert Distribution", createRuleAlertPieChart());
        Tab ruleAlertTableTab = new Tab("Rule Alert Table", ruleAlertTable);

        // Empêcher la fermeture des onglets
        transactionsTab.setClosable(false);
        fraudPercentageTab.setClosable(false);
        ruleAlertTab.setClosable(false);
        ruleAlertTableTab.setClosable(false);

        // Ajouter les onglets au TabPane
        tabPane.getTabs().addAll(transactionsTab, fraudPercentageTab, ruleAlertTab, ruleAlertTableTab);

        // Encapsuler le TabPane dans un VBox
        VBox vbox = new VBox(tabPane);
        return vbox; // Retourner le VBox comme un Node
    }

    private LineChart<Number, Number> createTransactionsAlertsChart() {
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Time (minutes)");
        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(0);
        xAxis.setUpperBound(60);
        xAxis.setTickUnit(1);

        NumberAxis yAxisCounts = new NumberAxis();
        yAxisCounts.setLabel("Transactions / Alerts Count");

        LineChart<Number, Number> transactionsAlertsChart = new LineChart<>(xAxis, yAxisCounts);
        transactionsAlertsChart.setTitle("Transactions & Alerts Over Time");
        transactionsSeries.setName("Transactions");
        alertsSeries.setName("Alerts");
        transactionsAlertsChart.getData().addAll(transactionsSeries, alertsSeries);

        return transactionsAlertsChart;
    }

    private LineChart<Number, Number> createFraudPercentageChart() {
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Time (minutes)");
        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(0);
        xAxis.setUpperBound(60);
        xAxis.setTickUnit(1);

        NumberAxis yAxisPercentage = new NumberAxis(0, 100, 10);
        yAxisPercentage.setLabel("Fraud Percentage (%)");

        LineChart<Number, Number> fraudPercentageChart = new LineChart<>(xAxis, yAxisPercentage);
        fraudPercentageChart.setTitle("Fraud Percentage Over Time");
        fraudPercentageSeries.setName("Fraud Percentage");
        fraudPercentageChart.getData().add(fraudPercentageSeries);

        return fraudPercentageChart;
    }

    private PieChart createRuleAlertPieChart() {
        // Récupérer les données directement à partir de la TableView
        ObservableList<RuleAlertStats> ruleAlertData = ruleAlertTable.getItems();  // Récupérer les éléments de la table


        // Créer une liste observable pour les données du PieChart
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        // Pour chaque élément dans la TableView, on ajoute une entrée dans le PieChart
        ruleAlertData.forEach(ruleAlert -> {
            String ruleId = ruleAlert.getRuleId();
            int count = ruleAlert.getAlertCount();
            pieChartData.add(new PieChart.Data(ruleId + " (" + count + ")", count));
        });

        // Créer le PieChart avec les données
        ruleAlertPieChart.setData(pieChartData);
        ruleAlertPieChart.setTitle("Alert Distribution by Rule");
        ruleAlertPieChart.setTitle("Alert Distribution by Rule");
        ruleAlertPieChart.setStyle("-fx-pref-width: 600px; -fx-pref-height: 600px;");
        return ruleAlertPieChart;
    }

    private void createRuleAlertTable() {
        // Colonne pour l'ID de règle
        TableColumn<RuleAlertStats, String> ruleIdColumn = new TableColumn<>("Rule ID");
        ruleIdColumn.setCellValueFactory(new PropertyValueFactory<>("ruleId"));
        ruleIdColumn.setPrefWidth(200);

        // Colonne pour le nombre d'alertes
        TableColumn<RuleAlertStats, Integer> alertCountColumn = new TableColumn<>("Alert Count");
        alertCountColumn.setCellValueFactory(new PropertyValueFactory<>("alertCount"));
        alertCountColumn.setPrefWidth(200);

        TableColumn<RuleAlertStats, String> alertDescColumn = new TableColumn<>("Description");
        alertDescColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        alertDescColumn.setPrefWidth(600);
        // Ajouter les colonnes à la table
        ruleAlertTable.getColumns().addAll(ruleIdColumn, alertCountColumn,alertDescColumn);
    }

    public void startStatisticsUpdater() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.minutes(0.01), event -> {
            updateChart();

        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void updateChart() {
        currentTime++;

        // Ajouter les nouvelles données
        transactionsSeries.getData().add(new XYChart.Data<>(currentTime, mainView.getTransactionList().size()));
        alertsSeries.getData().add(new XYChart.Data<>(currentTime, mainView.getAlertsList().size()));

        double fraudPercentage = mainView.getTransactionList().isEmpty() ? 0 :
                ((double) mainView.getAlertsList().size() / mainView.getTransactionList().size()) * 100;
        fraudPercentageSeries.getData().add(new XYChart.Data<>(currentTime, fraudPercentage));
    }

    private void updateRuleAlertTable() {
        // Regrouper les alertes par ID de règle
        Map<String, List<Alert>> groupedAlerts = mainView.getAlertsList().stream()
                .collect(Collectors.groupingBy(Alert::getAlertRuleID));

        // Créer une liste observable pour la TableView
        ObservableList<RuleAlertStats> ruleAlertData = FXCollections.observableArrayList();
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        groupedAlerts.forEach((ruleId, alerts) -> {
            int alertCount = alerts.size();

            // Récupérer les détails de la dernière alerte
            String description = alerts.get(alerts.size() - 1).getAlertDetails();

            // Ajouter les données à la liste observable
            ruleAlertData.add(new RuleAlertStats(ruleId, alertCount, description));

            pieChartData.add(new PieChart.Data(ruleId + " (" + alertCount + ")", alertCount));
        });

        // Mettre à jour les données de la table
        ruleAlertTable.setItems(ruleAlertData);

        ruleAlertPieChart.setData(pieChartData);

        // Ajouter des tooltips pour chaque secteur du PieChart
        ruleAlertPieChart.getData().forEach(data -> {
            Tooltip tooltip = new Tooltip(data.getName() + ": " + (int) data.getPieValue());
            Tooltip.install(data.getNode(), tooltip);
        });
    }
    public void startPieChartUpdater() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.minutes(1), event -> {
            updateRuleAlertTable();
        }));
        timeline.setCycleCount(Timeline.INDEFINITE); // Exécuter indéfiniment
        timeline.play(); // Lancer la mise à jour
    }

}