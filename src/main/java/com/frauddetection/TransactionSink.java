package com.frauddetection;

import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import javafx.application.Platform;

public class TransactionSink implements SinkFunction<Transaction> {

    @Override
    public void invoke(Transaction value, Context context) {
        Platform.runLater(() -> FraudDetectionUI.getInstance().getMainView().addTransaction(value));
    }
}