package com.frauddetection;

import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FraudDetectionProcessFunction extends KeyedProcessFunction<String, Transaction, Alert> {

    private List<Rule> rules;  // Liste de règles à appliquer

    // État pour stocker les agrégations (par exemple, COUNT, SUM)
    private transient ValueState<Map<String, Double>> aggregationState;

    // État pour enregistrer l'heure de la première transaction
    private transient ValueState<Long> firstTransactionTimestampState;

    // Constructeur qui prend la liste de règles en paramètre
    public FraudDetectionProcessFunction(List<Rule> rules) {
        this.rules = rules;
    }

    @Override
    public void open(org.apache.flink.configuration.Configuration parameters) {
        // Initialiser l'état pour stocker les agrégations avec un TypeInformation explicite
        ValueStateDescriptor<Map<String, Double>> aggregationDescriptor = new ValueStateDescriptor<>(
                "aggregationState",
                Types.MAP(Types.STRING, Types.DOUBLE)  // TypeInformation spécifique
        );
        aggregationState = getRuntimeContext().getState(aggregationDescriptor);

        // Initialiser l'état pour stocker l'heure de la première transaction
        ValueStateDescriptor<Long> timestampDescriptor = new ValueStateDescriptor<>(
                "firstTransactionTimestamp", Long.class);
        firstTransactionTimestampState = getRuntimeContext().getState(timestampDescriptor);
    }

    @Override
    public void processElement(Transaction transaction, Context context, Collector<Alert> out) throws Exception {
        System.out.println("Processing transaction: " + transaction);

        // Récupérer les états actuels
        Map<String, Double> aggregationValues = aggregationState.value();
        if (aggregationValues == null) {
            aggregationValues = new HashMap<>();
        }

        Long firstTransactionTimestamp = firstTransactionTimestampState.value();
        if (firstTransactionTimestamp == null) {
            firstTransactionTimestamp = transaction.getTimeTamp();
            firstTransactionTimestampState.update(firstTransactionTimestamp);
        }

        System.out.println("Current first transaction timestamp: " + firstTransactionTimestamp);

        // Appliquer toutes les règles à la transaction
        for (Rule rule : rules) {
            // Vérifier si l'intervalle de temps dépasse la durée de la fenêtre définie dans la règle
            long windowMillis = Long.parseLong(rule.getWindowMinutes()) * 60 * 1000;  // Convertir en millisecondes
            if (transaction.getTimeTamp() - firstTransactionTimestamp > windowMillis) {
                System.out.println("Resetting aggregation values due to time window exceeding: " + windowMillis);
                // Réinitialiser l'état si l'intervalle est dépassé
                aggregationValues.clear();
                firstTransactionTimestamp = transaction.getTimeTamp();
                firstTransactionTimestampState.update(firstTransactionTimestamp);
            }

            // Appliquer l'agrégateur basé sur la règle
            String[] groupingKeys = rule.getGroupingKeyNames().split(",");
            for (String key : groupingKeys) {
                // Effectuer l'agrégation pour chaque clé de regroupement
                if ("COUNT".equalsIgnoreCase(rule.getAggregatorFunctionType())) {
                    aggregationValues.put(key, aggregationValues.getOrDefault(key, 0.0) + 1);
                } else if ("SUM".equalsIgnoreCase(rule.getAggregatorFunctionType())) {
                    aggregationValues.put(key, aggregationValues.getOrDefault(key, 0.0) + transaction.getAmount());
                } else if ("AVERAGE".equalsIgnoreCase(rule.getAggregatorFunctionType())) {
                    // Utiliser la somme et le compte pour calculer la moyenne
                    double count = aggregationValues.getOrDefault(key + "_count", 0.0);
                    double sum = aggregationValues.getOrDefault(key + "_sum", 0.0);
                    aggregationValues.put(key + "_count", count + 1);
                    aggregationValues.put(key + "_sum", sum + transaction.getAmount());
                    aggregationValues.put(key, (sum + transaction.getAmount()) / (count + 1));
                }
            }

            aggregationState.update(aggregationValues);

            // Vérifier si la condition de seuil est respectée pour cette règle (par exemple, GREATER, LESS)
            double aggregatedValue = aggregationValues.getOrDefault(rule.getGroupingKeyNames(), 0.0);
            boolean isThresholdCrossed = false;

            if ("GREATER".equalsIgnoreCase(rule.getLimitOperatorType()) && aggregatedValue > rule.getLimit()) {
                isThresholdCrossed = true;
            } else if ("LESS".equalsIgnoreCase(rule.getLimitOperatorType()) && aggregatedValue < rule.getLimit()) {
                isThresholdCrossed = true;
            }

            // Détecter une fraude si le seuil est franchi pour cette règle
            if (isThresholdCrossed) {
                String groupById = rule.getGroupingKeyNames().equals("payerId") ? transaction.getPayerId() :
                        rule.getGroupingKeyNames().equals("beneficiaryId") ? transaction.getBeneficiaryId() : "Unknown";
                out.collect(new Alert(
                        "ID:" + rule.getRuleId(),
                        "Fraud detected for rule " + rule.getRuleId() + ": Condition " + rule.getAggregatorFunctionType() + " " +
                                rule.getLimitOperatorType() + " " + rule.getLimit() + " for " + rule.getGroupingKeyNames() +
                                " for ID: " + groupById,
                        System.currentTimeMillis()
                ));

                // Réinitialiser l'état (si nécessaire)
                aggregationState.clear();
                firstTransactionTimestampState.clear();
            }
        }
    }
}