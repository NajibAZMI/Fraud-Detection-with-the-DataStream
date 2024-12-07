package com.frauddetection;

import org.apache.flink.streaming.api.functions.source.SourceFunction;
import java.util.Random;

public class RandomTransactionSource implements SourceFunction<Transaction> {
    private boolean isRunning = true;
    private Random rand = new Random();

    @Override
    public void run(SourceContext<Transaction> ctx) throws Exception {
        while (isRunning) {
            // Générer une transaction aléatoire avec les nouveaux champs
            String transactionId = "TX" + rand.nextInt(100000); // Exemple : TX12345, TX98765, ...
            String payerId = "Payer" + rand.nextInt(100); // Exemple : Payer1, Payer2, ...
            double amount = rand.nextDouble() * 5000; // Montant entre 0 et 5000
            String beneficiaryId = "Beneficiary" + rand.nextInt(100); // Exemple : Beneficiary1, Beneficiary2, ...
            String transactionType = rand.nextBoolean() ? "Debit" : "Credit"; // Choisir aléatoirement entre Debit ou Credit

            // Créer une nouvelle transaction avec le transactionId unique
            Transaction transaction = new Transaction(transactionId, payerId, System.currentTimeMillis(),amount, beneficiaryId, transactionType );

            // Émettre la transaction
            ctx.collect(transaction);

            // Pause de 1 seconde entre les transactions
            Thread.sleep(1000);
        }
    }

    @Override
    public void cancel() {
        isRunning = false;
    }
}

