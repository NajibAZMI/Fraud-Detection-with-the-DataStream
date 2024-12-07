package com.frauddetection;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.layout.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
public class RuleManagementView {
    private GestionRule gestionRule;

    private TextField ruleIdField;
    private ComboBox<String> ruleStateField;
    private TextField groupingKeyNamesField;
    private ComboBox<String> aggregatorFunctionTypeField;
    private ComboBox<String> limitOperatorTypeField;
    private TextField limitField;
    private TextField windowMinutesField;
    private ListView<String> ruleListView;
    private VBox ruleCard; // Pour afficher les informations de la règle sélectionnée

    public RuleManagementView(GestionRule gestionRule) {
        this.gestionRule = gestionRule;

        // Initialisation des composants
        ruleIdField = new TextField();
        ruleStateField = new ComboBox<>();
        ruleStateField.setItems(FXCollections.observableArrayList("ACTIVE", "INACTIVE"));
        ruleStateField.setValue("ACTIVE");

        groupingKeyNamesField = new TextField();

        aggregatorFunctionTypeField = new ComboBox<>();
        aggregatorFunctionTypeField.setItems(FXCollections.observableArrayList("SUM", "AVERAGE", "COUNT"));
        aggregatorFunctionTypeField.setValue("SUM");

        limitOperatorTypeField = new ComboBox<>();
        limitOperatorTypeField.setItems(FXCollections.observableArrayList("GREATER", "LESS", "GREATER_EQUAL", "LESS_EQUAL"));
        limitOperatorTypeField.setValue("GREATER");

        limitField = new TextField();
        windowMinutesField = new TextField();

        ruleListView = new ListView<>();
        ruleListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                showRuleDetails(newSelection);
            }
        });

        // Initialisation des boutons
        Button addButton = new Button("Add Rule");
        Button updateButton = new Button("Update Rule");
        Button removeButton = new Button("Remove Rule");

        styleButton(addButton, Color.GREEN);
        styleButton(updateButton, Color.ORANGE);
        styleButton(removeButton, Color.RED);

        addButton.setOnAction(e -> addRule());
        updateButton.setOnAction(e -> updateRule());
        removeButton.setOnAction(e -> removeRule());

        // Création de la carte d'information
        ruleCard = new VBox();
        ruleCard.setPadding(new Insets(10));
        ruleCard.setSpacing(10);
        ruleCard.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, new CornerRadii(5), BorderWidths.DEFAULT)));
        ruleCard.setVisible(false);

        // Layout principal
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setVgap(8);
        grid.setHgap(10);

        grid.add(new Label("Rule ID:"), 0, 0);
        grid.add(ruleIdField, 1, 0);
        grid.add(new Label("Rule State:"), 0, 1);
        grid.add(ruleStateField, 1, 1);
        grid.add(new Label("Grouping Key Names:"), 0, 2);
        grid.add(groupingKeyNamesField, 1, 2);
        grid.add(new Label("Aggregator Function Type:"), 0, 3);
        grid.add(aggregatorFunctionTypeField, 1, 3);
        grid.add(new Label("Limit Operator Type:"), 0, 4);
        grid.add(limitOperatorTypeField, 1, 4);
        grid.add(new Label("Limit:"), 0, 5);
        grid.add(limitField, 1, 5);
        grid.add(new Label("Window Minutes:"), 0, 6);
        grid.add(windowMinutesField, 1, 6);

        HBox buttonBox = new HBox(10, addButton, updateButton, removeButton);
        grid.add(buttonBox, 0, 7, 2, 1);

        grid.add(new Label("Rules List:"), 0, 8);
        grid.add(ruleListView, 0, 9, 2, 1);
        grid.add(new Label("Rule Details:"), 0, 10);
        grid.add(ruleCard, 0, 11, 2, 1);

        refreshRuleListView();
    }

    private void styleButton(Button button, Color color) {
        button.setStyle("-fx-background-color: " + toRgbString(color) + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-border-radius: 5;");
        button.setPrefWidth(100);
    }

    private String toRgbString(Color c) {
        return "rgb(" + (c.getRed() * 255) + "," + (c.getGreen() * 255) + "," + (c.getBlue() * 255) + ")";
    }
    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Méthode pour ajouter une nouvelle règle
    private void addRule() {
        if (ruleIdField.getText().isEmpty() || limitField.getText().isEmpty() || windowMinutesField.getText().isEmpty() || groupingKeyNamesField.getText().isEmpty()) {
            showAlert("Input Error", "Please fill all required fields.", Alert.AlertType.ERROR);
            return;
        }

        int ruleId = Integer.parseInt(ruleIdField.getText());
        if (gestionRule.getAllRules().stream().anyMatch(rule -> rule.getRuleId() == ruleId)) {
            showAlert("Duplicate Rule ID", "A rule with this ID already exists.", Alert.AlertType.ERROR);
            return;
        }

        Rule rule = new Rule(
                ruleId,
                ruleStateField.getValue(),
                groupingKeyNamesField.getText(),  // Récupération de la chaîne de caractères entrée
                aggregatorFunctionTypeField.getValue(),
                limitOperatorTypeField.getValue(),
                Double.parseDouble(limitField.getText()),
                windowMinutesField.getText()  // La durée de la fenêtre est toujours une chaîne
        );

        gestionRule.addRule(rule);
        refreshRuleListView();
        showAlert("Success", "Rule added successfully.", Alert.AlertType.INFORMATION);
    }

    // Méthode pour mettre à jour une règle
    private void updateRule() {
        if (ruleIdField.getText().isEmpty()) {
            showAlert("Input Error", "Please enter the Rule ID to update.", Alert.AlertType.ERROR);
            return;
        }

        int ruleId = Integer.parseInt(ruleIdField.getText());
        Rule rule = new Rule(
                ruleId,
                ruleStateField.getValue(),
                groupingKeyNamesField.getText(),  // Récupération de la chaîne de caractères entrée
                aggregatorFunctionTypeField.getValue(),
                limitOperatorTypeField.getValue(),
                Double.parseDouble(limitField.getText()),
                windowMinutesField.getText()  // La durée de la fenêtre est toujours une chaîne
        );

        if (!gestionRule.updateRule(rule)) {
            showAlert("Update Error", "No rule found with this ID.", Alert.AlertType.ERROR);
        } else {
            refreshRuleListView();
            showAlert("Success", "Rule updated successfully.", Alert.AlertType.INFORMATION);
        }
    }

    // Méthode pour supprimer une règle
    private void removeRule() {
        int ruleId;
        try {
            ruleId = Integer.parseInt(ruleIdField.getText());
        } catch (NumberFormatException e) {
            showAlert("Input Error", "Invalid Rule ID.", Alert.AlertType.ERROR);
            return;
        }

        if (!gestionRule.removeRule(ruleId)) {
            showAlert("Remove Error", "No rule found with this ID.", Alert.AlertType.ERROR);
        } else {
            refreshRuleListView();
            showAlert("Success", "Rule removed successfully.", Alert.AlertType.INFORMATION);
        }
    }


    private void showRuleDetails(String ruleString) {
        ruleCard.getChildren().clear();
        String[] details = ruleString.split(", ");
        for (String detail : details) {
            ruleCard.getChildren().add(new Label(detail));
        }
        ruleCard.setVisible(true);
    }

    private void refreshRuleListView() {
        ruleListView.getItems().clear();
        for (Rule rule : gestionRule.getAllRules()) {
            ruleListView.getItems().add(rule.toString());
        }
    }

    public GridPane getView() {
        return (GridPane) ruleListView.getParent();
    }
}