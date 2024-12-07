package com.frauddetection;
import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.frauddetection.DATABASE.DatabaseConnection;

import java.util.Iterator;

public class GestionRule implements Serializable {

    // Liste en mémoire pour stocker les règles
    private List<Rule> rules;

    // Constructeur
    public GestionRule() {
        this.rules = new ArrayList<>();

        }

    // Ajouter une règle à la base de données et à la liste en mémoire
    public boolean addRule(Rule rule) {
        String sql = "INSERT INTO rules (rule_id, rule_state, grouping_key_names, aggregator_function_type, "
                + "limit_operator_type, limit_cond, window_minutes) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            // Ajouter la règle à la base de données
            statement.setInt(1, rule.getRuleId());
            statement.setString(2, rule.getRuleState());
            statement.setString(3, rule.getGroupingKeyNames());
            statement.setString(4, rule.getAggregatorFunctionType());
            statement.setString(5, rule.getLimitOperatorType());
            statement.setDouble(6, rule.getLimit());
            statement.setString(7, rule.getWindowMinutes());

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                rules.add(rule);
                return true;
            } else {
                System.err.println("Erreur lors de l'ajout de la règle à la base de données.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Supprimer une règle de la base de données et de la liste en mémoire
    public boolean removeRule(int ruleId) {
        String sql = "DELETE FROM rules WHERE rule_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            // Supprimer la règle de la base de données
            statement.setInt(1, ruleId);
            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                // Supprimer la règle de la liste en mémoire si la suppression de la base de données réussit
                Iterator<Rule> iterator = rules.iterator();
                while (iterator.hasNext()) {
                    Rule rule = iterator.next();
                    if (rule.getRuleId() == ruleId) {
                        iterator.remove();
                        return true;
                    }
                }
            } else {
                System.err.println("Aucune règle trouvée à supprimer dans la base de données.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Mettre à jour une règle dans la base de données et dans la liste en mémoire
    public boolean updateRule(Rule updatedRule) {
        String sql = "UPDATE rules SET rule_state = ?, grouping_key_names = ?, aggregator_function_type = ?, "
                + "limit_operator_type = ?, limit_cond = ?, window_minutes = ? WHERE rule_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            // Mettre à jour la règle dans la base de données
            statement.setString(1, updatedRule.getRuleState());
            statement.setString(2, updatedRule.getGroupingKeyNames());
            statement.setString(3, updatedRule.getAggregatorFunctionType());
            statement.setString(4, updatedRule.getLimitOperatorType());
            statement.setDouble(5, updatedRule.getLimit());
            statement.setString(6, updatedRule.getWindowMinutes());
            statement.setInt(7, updatedRule.getRuleId());

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                // Mettre à jour la règle dans la liste en mémoire si la mise à jour dans la base de données réussit
                for (int i = 0; i < rules.size(); i++) {
                    if (rules.get(i).getRuleId() == updatedRule.getRuleId()) {
                        rules.set(i, updatedRule);
                        return true;
                    }
                }
            } else {
                System.err.println("Erreur lors de la mise à jour de la règle dans la base de données.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Récupérer toutes les règles depuis la base de données
    public List<Rule> getAllRules() {
        List<Rule> rulesFromDb = new ArrayList<>();
        String sql = "SELECT * FROM rules";

        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                int ruleId = resultSet.getInt("rule_id");
                String ruleState = resultSet.getString("rule_state");
                String groupingKeyNames = resultSet.getString("grouping_key_names");
                String aggregatorFunctionType = resultSet.getString("aggregator_function_type");
                String limitOperatorType = resultSet.getString("limit_operator_type");
                double limit = resultSet.getDouble("limit_cond");
                String windowMinutes = resultSet.getString("window_minutes");

                rulesFromDb.add(new Rule(ruleId, ruleState, groupingKeyNames, aggregatorFunctionType,
                        limitOperatorType, limit, windowMinutes));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rulesFromDb;
    }

    // Récupérer une règle par son ID depuis la base de données
    public Rule getRuleById(int ruleId) {
        String sql = "SELECT * FROM rules WHERE rule_id = ?";
        Rule rule = null;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, ruleId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String ruleState = resultSet.getString("rule_state");
                String groupingKeyNames = resultSet.getString("grouping_key_names");
                String aggregatorFunctionType = resultSet.getString("aggregator_function_type");
                String limitOperatorType = resultSet.getString("limit_operator_type");
                double limit = resultSet.getDouble("limit_cond");
                String windowMinutes = resultSet.getString("window_minutes");

                rule = new Rule(ruleId, ruleState, groupingKeyNames, aggregatorFunctionType, limitOperatorType, limit, windowMinutes);
            } else {
                System.err.println("Règle introuvable avec l'ID : " + ruleId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rule;
    }

    // Récupérer les règles actives depuis la base de données
    public List<Rule> getActiveRules() {
        List<Rule> activeRulesFromDb = new ArrayList<>();
        String sql = "SELECT * FROM rules WHERE rule_state = 'ACTIVE'";

        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                int ruleId = resultSet.getInt("rule_id");
                String ruleState = resultSet.getString("rule_state");
                String groupingKeyNames = resultSet.getString("grouping_key_names");
                String aggregatorFunctionType = resultSet.getString("aggregator_function_type");
                String limitOperatorType = resultSet.getString("limit_operator_type");
                double limit = resultSet.getDouble("limit_cond");
                String windowMinutes = resultSet.getString("window_minutes");

                activeRulesFromDb.add(new Rule(ruleId, ruleState, groupingKeyNames, aggregatorFunctionType,
                        limitOperatorType, limit, windowMinutes));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return activeRulesFromDb;
    }

    // Basculer l'état d'une règle
    public boolean toggleRuleState(int ruleId, String newState) {
        String sql = "UPDATE rules SET rule_state = ? WHERE rule_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, newState);
            statement.setInt(2, ruleId);
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                // Mettre à jour l'état de la règle dans la liste en mémoire
                for (Rule rule : rules) {
                    if (rule.getRuleId() == ruleId) {
                        rule.setRuleState(newState);
                        return true;
                    }
                }
            } else {
                System.err.println("Erreur lors de la mise à jour de l'état de la règle.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Fonction de test pour afficher les règles en mémoire
    public void displayRules() {
        for (Rule rule : rules) {
            System.out.println(rule);
        }
    }


}