package com.frauddetection;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;



import java.time.Duration;
import java.util.List;

public class FraudDetectionApp {

    public static void main(String[] args) throws Exception {

        try {
            new Thread(() -> FraudDetectionUI.main(args)).start();

            // Créer l'environnement d'exécution Flink
            final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
            GestionRule gestionRule = new GestionRule();
            // Charger les règles actives initiales
            List<Rule> activeRules = gestionRule.getActiveRules();
            if (activeRules.isEmpty()) {
                System.err.println("Aucune règle active trouvée dans la base de données.");
                return;
            }


            // Ajouter des timestamps et des watermarks au flux de données
            DataStream<Transaction> transactionStream = env
                    .addSource(new RandomTransactionSource())
                    .assignTimestampsAndWatermarks(
                            WatermarkStrategy.<Transaction>forBoundedOutOfOrderness(Duration.ofSeconds(5))
                                    .withTimestampAssigner((transaction, timestamp) -> transaction.getTimeTamp())
                    );
            // Appliquer le groupement en fonction des règles
            KeyedStream<Transaction, String> keyedStream = transactionStream.keyBy(transaction -> {
                for (Rule rule : gestionRule.getActiveRules()) {
                    if ("payerId".equals(rule.getGroupingKeyNames())) {
                        return transaction.getPayerId();
                    } else if ("beneficiaryId".equals(rule.getGroupingKeyNames())) {
                        return transaction.getBeneficiaryId();
                    }
                }
                throw new IllegalArgumentException("Invalid grouping key in any rule");
            });

            // Appliquer la logique de détection de fraude en utilisant les règles
            DataStream<Alert> alerts = keyedStream.process(new FraudDetectionProcessFunction(gestionRule.getActiveRules()));

            // Ajouter des sinks pour envoyer les transactions et alertes vers des logs ou une interface
            transactionStream.print("Transaction Stream");
            alerts.print("Alert Stream");
            transactionStream.addSink(new TransactionSink());
            alerts.addSink(new AlertSink());

            // Lancer l'application Flink
            env.execute("Fraud Detection Application");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Une erreur s'est produite : " + e.getMessage());
        }
        // Démarrage de l'interface utilisateur en parallèle


    }
}
