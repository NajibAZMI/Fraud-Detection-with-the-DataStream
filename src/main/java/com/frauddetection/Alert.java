package com.frauddetection;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
public class Alert implements Serializable {
    private String ruleID ;
    private String details;
    private long timestamp;
    public Alert(String ruleID ,String details, long timestamp) {
        this.ruleID =ruleID;
        this.details= details;
        this.timestamp=timestamp;
    }

    public String getAlertRuleID() {
        return ruleID;
    }

    public String getAlertDetails() {
        return details;
    }
    public long getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    @Override
    public String toString() {
        return "Alert{rule=" + ruleID+ ", details" + details + "}";
    }
    public String getFormattedTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date(timestamp));
    }
}

