package com.frauddetection;

import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import javafx.application.Platform;

public class AlertSink implements SinkFunction<Alert> {

    @Override
    public void invoke(Alert value, Context context) {
          Platform.runLater(() -> FraudDetectionUI.getInstance().getMainView().addAlert(value));
    }
}